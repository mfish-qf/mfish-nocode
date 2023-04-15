package cn.com.mfish.common.core.utils;

import cn.com.mfish.common.core.exception.MyRuntimeException;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @description: ZIP压缩通用类
 * @author: mfish
 * @date: 2023/4/14
 */
@Slf4j
public class ZipUtils {
    private static final byte[] buf = new byte[1024];
    private static final String SEPARATE = "/";

    /**
     * 压缩成ZIP
     *
     * @param filePath 要压缩的文件路径
     * @param keepDir  是否保留原来的目录结构,true:保留目录结构;
     *                 false:所有文件跑到压缩包根目录下
     *                 (注意：不保留目录结构可能会出现同名文件,会压缩失败)
     * @throws RuntimeException 压缩失败会抛出运行时异常
     */
    public static Boolean toZip(String filePath, boolean keepDir) {
        long start = System.currentTimeMillis();
        filePath = filePath.replace("\\", SEPARATE);
        File sourceFile = new File(filePath);
        String fileName = "";
        if (sourceFile.isDirectory()) {
            if (filePath.endsWith(SEPARATE)) {
                fileName = filePath.substring(0, filePath.length() - 1);
            } else {
                fileName = filePath;
            }
        } else {
            int index = filePath.lastIndexOf(".");
            if (index > 0) {
                fileName = filePath.substring(0, index);
            } else {
                fileName = filePath;
            }
        }
        fileName += ".zip";
        try (FileOutputStream fileOutputStream = new FileOutputStream(fileName);
             ZipOutputStream zos = new ZipOutputStream(fileOutputStream)) {
            compress(sourceFile, zos, sourceFile.getName(), keepDir);
            long end = System.currentTimeMillis();
            log.info("压缩完成，耗时：" + (end - start) + " 毫秒");
        } catch (Exception e) {
            String error = "错误:压缩失败";
            log.error(error, e);
            throw new MyRuntimeException(error, e);
        }
        return true;
    }

    /**
     * 多个文件压缩到一个目录
     *
     * @param files   需要压缩的文件列表
     * @param zipName 压缩文件输出
     * @throws RuntimeException 压缩失败会抛出运行时异常
     */
    public static void toZip(String zipName, List<File> files) {
        long start = System.currentTimeMillis();
        try (FileOutputStream fileOutputStream = new FileOutputStream(zipName);
             ZipOutputStream zos = new ZipOutputStream(fileOutputStream)) {
            for (File srcFile : files) {
                compress(srcFile, zos, srcFile.getName(), true);
            }
            long end = System.currentTimeMillis();
            System.out.println("压缩完成，耗时：" + (end - start) + " 毫秒");
        } catch (Exception e) {
            String error = "错误:压缩失败";
            log.error(error, e);
            throw new MyRuntimeException(error, e);
        }
    }


    /**
     * 递归压缩方法
     *
     * @param sourceFile 源文件
     * @param zos        zip输出流
     * @param name       压缩后的名称
     * @param keepDir    是否保留原来的目录结构,true:保留目录结构;
     *                   false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     * @throws Exception
     */
    private static void compress(File sourceFile, ZipOutputStream zos, String name,
                                 boolean keepDir) throws IOException {
        if (sourceFile.isFile()) {
            try (FileInputStream in = new FileInputStream(sourceFile)) {
                // 向zip输出流中添加一个zip实体，构造器中name为zip实体的文件的名字
                zos.putNextEntry(new ZipEntry(name));
                // copy文件到zip输出流中
                int len;
                while ((len = in.read(buf)) != -1) {
                    zos.write(buf, 0, len);
                }
                // 完成输入
                zos.closeEntry();
                return;
            } catch (IOException ex) {
                throw ex;
            }
        }
        File[] listFiles = sourceFile.listFiles();
        if (listFiles == null || listFiles.length == 0) {
            // 需要保留原来的文件结构时,需要对空文件夹进行处理
            if (keepDir) {
                // 空文件夹的处理
                zos.putNextEntry(new ZipEntry(name + SEPARATE));
                // 没有文件，不需要文件的copy
                zos.closeEntry();
            }
            return;
        }
        for (File file : listFiles) {
            String zipName = file.getName();
            // 判断是否需要保留原来的文件结构
            if (keepDir) {
                zipName = name + SEPARATE + zipName;
            }
            compress(file, zos, zipName, keepDir);
        }
    }
}
