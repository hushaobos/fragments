package com.hjc.sign.core.common;

import java.io.Serializable;

public interface Credential extends Serializable{
    boolean isOriginal();

    Object getParam(String paramName);

    void setIp(String ip);

    String getIp();
}
