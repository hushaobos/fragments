package com.hjc.sign.core.common;

import java.io.Serializable;

public class EncryCredentialInfo implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 8754417913547945123L;

    /**
     * 用户的唯一标识
     */
    private Long userId;

    /**应用的唯一标识
     *
     */
    private String appId;

    /**
     *密钥
     */
    private String key;

    /**盐值
     *
     */
    private String salt;

    /**创建时间戳
     *
     */
    private Long createTime;

    /**截止时间戳
     *
     */
    private Long expiryTime;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(Long expiryTime) {
        this.expiryTime = expiryTime;
    }
}
