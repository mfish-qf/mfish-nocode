package cn.com.mfish.common.file.handler;

import org.springframework.core.io.Resource;

import java.io.File;
import java.io.InputStream;

/**
 * @description: 缓存接口
 * @author: mfish
 * @date: 2023/1/5 15:44
 */
public interface Storage {
    /**
     * 存储文件
     * <p>
     * 此方法用于将输入流中的数据保存到指定的文件路径。它主要用于处理文件的上传或更新，
     * 并确保文件的完整性和正确的MIME类型。
     *
     * @param inputStream 输入流，包含要存储的文件数据。
     * @param contentLength 文件内容长度，用于预先分配文件存储空间或进行完整性检查。
     * @param contentType 文件的MIME类型，用于验证文件类型是否符合预期。
     * @param filePath 要存储到的文件路径，包括文件名和扩展名。
     */
    void store(InputStream inputStream, long contentLength, String contentType, String filePath);

    /**
     * 根据文件路径加载资源为Resource对象
     *
     * @param filePath 文件的路径，用于定位和加载特定的文件资源
     * @return 返回一个Resource对象，表示请求的文件资源
     *         如果指定的文件不存在或无法访问，将返回null
     * <p>
     * 此方法的目的是通过文件路径加载文件资源，以便在不直接暴露文件系统结构的情况下，
     * 对文件进行操作。它对于文件的访问和操作提供了一层抽象。
     */
    Resource loadAsResource(String filePath);

    /**
     * 获取文件流
     * <p>
     * 该方法用于打开一个文件，并返回该文件的输入流。这对于读取文件内容到程序中有重要作用。
     * </p>
     *
     * @param filePath 文件的路径，指定了需要获取输入流的文件位置
     * @return 返回文件的输入流，允许调用者读取文件内容
     */
    InputStream getInputStream(String filePath);

    /**
     * 获取文件
     * <p>
     * 根据给定的文件路径，返回一个File对象，该对象表示的是存储在文件系统中的文件或目录
     *
     * @param filePath 文件的路径，以字符串形式表示
     * @return 返回一个File对象，如果文件不存在，则返回null
     */
    File getFile(String filePath);

    /**
     * 删除文件
     *
     * @param filePath 文件的路径，包括文件名和扩展名
     */
    void delete(String filePath);

    /**
     * 构建文件外部访问链接
     *
     * @param filePath 文件路径，用于定位文件位置
     * @param isPrivate 链接类型标识，决定链接的权限（0表示公开，1表示私有）
     * @return 返回文件的外部访问链接
     */
    String buildUrl(String filePath, Integer isPrivate);
}
