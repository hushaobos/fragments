package com.hjc.sign.core.common;

public class EncryCredential extends AbstractCredential {
    private String credential;

    /**
     * 加密凭据对应的加密凭据信息对象。
     */
    private EncryCredentialInfo encryCredentialInfo;

    public String getCredential() {
        return credential;
    }

    public void setCredential(String credential) {
        this.credential = credential;
    }

    @Override
    public boolean isOriginal() {
        return false;
    }

    public EncryCredentialInfo getEncryCredentialInfo() {
        return encryCredentialInfo;
    }

    public void setEncryCredentialInfo(EncryCredentialInfo encryCredentialInfo) {
        this.encryCredentialInfo = encryCredentialInfo;
    }
}
