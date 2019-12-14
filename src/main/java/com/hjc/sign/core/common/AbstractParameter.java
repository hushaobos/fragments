package com.hjc.sign.core.common;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 胡绍波
 */
public abstract class AbstractParameter implements Parameter{
    /**
     * request的参数
     */
    Map<String,Object> paramMap = new HashMap<String, Object>();

    @Override
    public void setParams(Map<String, Object> objectMap) {
        this.paramMap = objectMap;
    }

    @Override
    public Object getParam(String paramName) {
        return paramName == null ? null : paramMap.get(paramName);
    }

    @Override
    public Map<String, Object> getParams() {
        return this.paramMap;
    }
}