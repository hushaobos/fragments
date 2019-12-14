package com.hjc.http.resp;

import java.io.Serializable;

/**
 * 本类为接口返回结果类
 */
public interface Response extends Serializable {
    RespResult getResult();

    void setResult(RespResult result);
}
