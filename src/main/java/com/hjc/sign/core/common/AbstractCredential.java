package com.hjc.sign.core.common;

public abstract class AbstractCredential  extends AbstractParameter implements Credential {
    private String ip;
    @Override
    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public String getIp() {
        return ip;
    }
}
