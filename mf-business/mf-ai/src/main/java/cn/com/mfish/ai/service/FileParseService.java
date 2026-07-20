package cn.com.mfish.ai.service;

import cn.com.mfish.common.core.constants.RPCConstants;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.storage.api.entity.StorageInfo;
import cn.com.mfish.common.storage.api.remote.RemoteStorageService;
import cn.com.mfish.common.core.web.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.extractor.ExtractorFactory;
import org.apache.poi.extractor.POITextExtractor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * 文件解析服务
 * <p>
 * 根据文件fileKey列表，通过远程存储服务获取文件元数据与内容，
 * 将文件内容提取为LLM可理解的提示词片段。
 * <p>
 * 支持的文件类型：
 * <ul>
 *   <li>文本类：txt/log/md/json/xml/yml/csv/sql/源代码等（直接UTF-8读取）</li>
 *   <li>Office文档（仅OOXML新格式）：docx/xlsx/pptx（通过Apache POI提取文本）</li>
 *   <li>其他二进制：图片/压缩包/旧版Office（.doc/.xls/.ppt）等（返回提示信息，不解析内容）</li>
 * </ul>
 * <p>
 * 注意：旧版OLE2格式（.doc/.xls/.ppt）需要 poi-scratchpad 依赖，当前未引入，
 * 前端应限制只允许上传 .docx/.xlsx/.pptx 格式。
 * <p>
 * 调用链：FileParseService → RemoteStorageService(Feign) → mf-storage 服务
 * BearerTokenInterceptor 自动中继用户令牌，保证私有文件可访问。
 *
 * @author: mfish
 * @date: 2026/07/20
 */
@Slf4j
@Service
public class FileParseService {
    /**
     * 单个文件读取的最大字符数，超过则截断，避免撑爆LLM上下文
     */
    private static final int MAX_CONTENT_CHARS = 51200;
    /**
     * 文本类contentType前缀集合，命中时按文本读取
     */
    private static final Set<String> TEXT_CONTENT_TYPE_PREFIXES = Set.of("text/");
    /**
     * 文本类contentType精确匹配集合
     */
    private static final Set<String> TEXT_CONTENT_TYPES = Set.of(
            "application/json",
            "application/xml",
            "application/javascript",
            "application/x-yaml",
            "application/yaml",
            "application/x-sh",
            "application/sql",
            "application/csv",
            "application/x-java",
            "application/x-tex",
            "application/manifest+json",
            "application/graphql-response+json"
    );
    /**
     * 文本类文件后缀集合（按扩展名兜底识别）
     */
    private static final Set<String> TEXT_SUFFIXES = Set.of(
            ".txt", ".log", ".md", ".markdown", ".json", ".xml", ".yaml", ".yml",
            ".csv", ".sql", ".js", ".ts", ".java", ".py", ".go", ".rs", ".c",
            ".cpp", ".h", ".hpp", ".cs", ".rb", ".php", ".swift", ".kt", ".scala",
            ".sh", ".bat", ".ps1", ".properties", ".conf", ".ini", ".toml",
            ".html", ".htm", ".css", ".scss", ".less", ".vue", ".tsx", ".jsx",
            ".gradle", ".gitignore", ".dockerfile", ".env", ".proto"
    );
    /**
     * Office文档后缀集合（仅OOXML新格式，通过Apache POI解析）
     * <p>
     * 旧版OLE2格式（.doc/.xls/.ppt）需要 poi-scratchpad 依赖，当前未引入，
     * 前端应限制只允许上传新格式。
     */
    private static final Set<String> OFFICE_SUFFIXES = Set.of(
            ".docx", ".xlsx", ".pptx", ".docm", ".dotm", ".xlsm", ".xltm", ".pptm", ".potm"
    );
    /**
     * Office文档contentType精确匹配集合（仅OOXML新格式）
     */
    private static final Set<String> OFFICE_CONTENT_TYPES = Set.of(
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.template",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.template",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation",
            "application/vnd.openxmlformats-officedocument.presentationml.template"
    );

    private final RemoteStorageService remoteStorageService;

    public FileParseService(RemoteStorageService remoteStorageService) {
        this.remoteStorageService = remoteStorageService;
    }

    /**
     * 加载文件内容并拼接为提示词片段
     * <p>
     * 遍历fileKey列表，逐个获取文件元数据与内容，将文本/Office文档内容拼接到StringBuilder。
     * 单个文件读取失败不影响其他文件，异常会被记录日志并跳过。
     *
     * @param fileIds 文件fileKey列表
     * @return 拼接好的文件内容提示词片段；列表为空或全部失败时返回空字符串
     */
    public String loadFileContents(List<String> fileIds) {
        if (fileIds == null || fileIds.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        int successCount = 0;
        for (String fileKey : fileIds) {
            if (StringUtils.isEmpty(fileKey)) {
                continue;
            }
            try {
                String segment = loadOneFile(fileKey);
                if (segment != null && !segment.isEmpty()) {
                    sb.append(segment).append("\n\n");
                    successCount++;
                }
            } catch (Exception e) {
                log.warn("加载文件内容失败 fileKey={} reason={}", fileKey, e.getMessage());
            }
        }
        if (successCount == 0) {
            return "";
        }
        return sb.toString();
    }

    /**
     * 加载单个文件并构建内容片段
     * <p>
     * 按文件类型分发到不同解析路径：
     * <ol>
     *   <li>文本类：直接UTF-8读取</li>
     *   <li>Office文档：通过POI提取文本</li>
     *   <li>其他二进制：返回提示信息</li>
     * </ol>
     *
     * @param fileKey 文件key
     * @return 文件内容片段；文件不存在返回null
     */
    private String loadOneFile(String fileKey) {
        Result<StorageInfo> infoResult = remoteStorageService.queryByKey(RPCConstants.INNER, fileKey);
        if (infoResult == null || !infoResult.isSuccess() || infoResult.getData() == null) {
            log.warn("文件信息查询失败 fileKey={} msg={}", fileKey,
                    infoResult == null ? "result is null" : infoResult.getMsg());
            return null;
        }
        StorageInfo storageInfo = infoResult.getData();
        String fileName = StringUtils.isEmpty(storageInfo.getFileName()) ? fileKey : storageInfo.getFileName();
        String fileType = storageInfo.getFileType();

        if (isTextFile(fileType, fileName)) {
            String content = readFileText(fileKey);
            return content == null ? buildReadFailureHint(fileName) : buildTextFileSegment(fileName, truncate(content));
        }
        if (isOfficeDocument(fileType, fileName)) {
            String content = extractOfficeText(fileKey, fileName);
            return content == null ? buildReadFailureHint(fileName) : buildTextFileSegment(fileName, truncate(content));
        }
        return buildBinaryFileHint(fileName, fileType, storageInfo.getFileSize());
    }

    /**
     * 通过Feign获取文件资源并读取为UTF-8文本
     *
     * @param fileKey 文件key
     * @return 文件文本内容；读取失败返回null
     */
    private String readFileText(String fileKey) {
        ResponseEntity<Resource> resp = remoteStorageService.fetch(RPCConstants.INNER, fileKey);
        if (resp == null || !resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
            log.warn("文件资源获取失败 fileKey={} status={}", fileKey,
                    resp == null ? "null" : resp.getStatusCode());
            return null;
        }
        try (InputStream is = resp.getBody().getInputStream()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.warn("文件内容读取失败 fileKey={} reason={}", fileKey, e.getMessage());
            return null;
        }
    }

    /**
     * 通过Apache POI提取Office文档文本
     * <p>
     * 使用 {@link ExtractorFactory} 自动识别文档类型（Word/Excel/PowerPoint，旧版/新版格式），
     * 无需根据后缀手动分发，POI内部根据文件魔数选择合适的Extractor。
     * <p>
     * 注意：POI要求InputStream支持mark/reset，Feign返回的InputStream需先缓存为字节数组再包装。
     *
     * @param fileKey  文件key
     * @param fileName 文件名（仅用于日志）
     * @return 提取的文本内容；提取失败返回null
     */
    private String extractOfficeText(String fileKey, String fileName) {
        ResponseEntity<Resource> resp = remoteStorageService.fetch(RPCConstants.INNER, fileKey);
        if (resp == null || !resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
            log.warn("Office文件资源获取失败 fileKey={} status={}", fileKey,
                    resp == null ? "null" : resp.getStatusCode());
            return null;
        }
        // POI的ExtractorFactory.createExtractor需要File或mark/reset支持的InputStream
        // ByteArrayInputStream支持mark/reset，避免POI内部reset失败
        try (InputStream is = resp.getBody().getInputStream()) {
            byte[] bytes = is.readAllBytes();
            try (InputStream poiStream = new java.io.ByteArrayInputStream(bytes);
                 POITextExtractor extractor = ExtractorFactory.createExtractor(poiStream)) {
                String text = extractor.getText();
                log.info("Office文档解析成功 fileName={} chars={}", fileName,
                        text == null ? 0 : text.length());
                return text;
            }
        } catch (Exception e) {
            log.warn("Office文档解析失败 fileKey={} fileName={} reason={}", fileKey, fileName, e.getMessage());
            return null;
        }
    }

    /**
     * 截断过长的内容，避免撑爆LLM上下文
     */
    private String truncate(String content) {
        if (content == null) {
            return "";
        }
        if (content.length() > MAX_CONTENT_CHARS) {
            return content.substring(0, MAX_CONTENT_CHARS)
                    + "\n...[文件内容过长，已截断，仅显示前" + MAX_CONTENT_CHARS + "字符]";
        }
        return content;
    }

    /**
     * 判断文件是否为文本类文件
     * <p>
     * 优先按contentType判断，再按文件名后缀兜底
     *
     * @param contentType 文件MIME类型
     * @param fileName    文件名
     * @return true表示可按文本读取
     */
    private boolean isTextFile(String contentType, String fileName) {
        if (StringUtils.isNotEmpty(contentType)) {
            String lower = contentType.toLowerCase(Locale.ROOT);
            if (TEXT_CONTENT_TYPE_PREFIXES.stream().anyMatch(lower::startsWith)) {
                return true;
            }
            if (TEXT_CONTENT_TYPES.contains(lower)) {
                return true;
            }
        }
        if (StringUtils.isNotEmpty(fileName)) {
            String lowerName = fileName.toLowerCase(Locale.ROOT);
            return TEXT_SUFFIXES.stream().anyMatch(lowerName::endsWith);
        }
        return false;
    }

    /**
     * 判断文件是否为Office文档
     * <p>
     * 优先按contentType判断，再按文件名后缀兜底
     *
     * @param contentType 文件MIME类型
     * @param fileName    文件名
     * @return true表示为Office文档（Word/Excel/PowerPoint）
     */
    private boolean isOfficeDocument(String contentType, String fileName) {
        if (StringUtils.isNotEmpty(contentType)) {
            String lower = contentType.toLowerCase(Locale.ROOT);
            if (OFFICE_CONTENT_TYPES.contains(lower)) {
                return true;
            }
        }
        if (StringUtils.isNotEmpty(fileName)) {
            String lowerName = fileName.toLowerCase(Locale.ROOT);
            return OFFICE_SUFFIXES.stream().anyMatch(lowerName::endsWith);
        }
        return false;
    }

    private String buildTextFileSegment(String fileName, String content) {
        return "=== 文件: " + fileName + " ===\n" + content + "\n=== 文件结束: " + fileName + " ===";
    }

    private String buildBinaryFileHint(String fileName, String fileType, Integer fileSize) {
        return "=== 文件: " + fileName + " ===\n" +
                "[二进制文件，无法直接作为文本解析] 类型: " + (StringUtils.isEmpty(fileType) ? "未知" : fileType) +
                " 大小: " + (fileSize == null ? "未知" : fileSize + " bytes") +
                "\n=== 文件结束: " + fileName + " ===";
    }

    private String buildReadFailureHint(String fileName) {
        return "=== 文件: " + fileName + " ===\n[文件内容读取失败]\n=== 文件结束: " + fileName + " ===";
    }
}
