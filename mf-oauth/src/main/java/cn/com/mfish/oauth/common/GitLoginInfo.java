package cn.com.mfish.oauth.common;

import lombok.Getter;

/**
 * @description: Git登录信息
 * @author: mfish
 * @date: 2025/10/22
 */
@Getter
public class GitLoginInfo {
    private final String token;
    private final String gitInfo;
    private final String account;
    public GitLoginInfo(String token, String account, String gitInfo) {
        this.token = token;
        this.account = account;
        this.gitInfo = gitInfo;
    }
}
