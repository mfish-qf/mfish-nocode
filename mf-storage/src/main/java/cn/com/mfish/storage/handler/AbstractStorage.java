package cn.com.mfish.storage.handler;

import cn.com.mfish.storage.common.StorageUtils;

/**
 * @description: 抽象缓存处理类
 * @author: mfish
 * @date: 2023/3/14 10:24
 */
public abstract class AbstractStorage implements Storage {
    /**
     * 接口地址
     */
    private String address;

    public AbstractStorage(String address) {
        this.address = address;
    }

    /**
     * 获取本地服务端文件访问路径（父类为其他路径时可重写）
     *
     * @param filePath  文件全路径
     * @param isPrivate 是否私有
     * @return
     */
    @Override
    public String buildUrl(String filePath, Integer isPrivate) {
        int index = filePath.lastIndexOf("/");
        String keyName;
        if (index > 0 && index < filePath.length()) {
            keyName = filePath.substring(index + 1);
        } else {
            keyName = filePath;
        }
        return StorageUtils.formatFilePath(address) + "/" + keyName;
    }
}
