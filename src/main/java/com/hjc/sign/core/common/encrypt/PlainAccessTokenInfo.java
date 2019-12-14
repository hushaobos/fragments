package com.hjc.sign.core.common.encrypt;

/**accessToken基本组成信息
 *
 */
public class PlainAccessTokenInfo {
    /**
     * 用户Id
     */
    private long userId;

    /**
     * 过期时间戳
     */
    private long loginTimestamp;

    public PlainAccessTokenInfo() {
    }

    public PlainAccessTokenInfo(long userId, long loginTimestamp) {
        this.userId = userId;
        this.loginTimestamp = loginTimestamp;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getLoginTimestamp() {
        return loginTimestamp;
    }

    public void setLoginTimestamp(long loginTimestamp) {
        this.loginTimestamp = loginTimestamp;
    }
}
