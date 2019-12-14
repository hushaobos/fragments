package com.hjc.sign.core.common;

import java.util.Map;

public interface Parameter {
    void setParams(Map<String, Object> objectMap);

    Object getParam(String paramName);

    Map<String,Object> getParams();
}

