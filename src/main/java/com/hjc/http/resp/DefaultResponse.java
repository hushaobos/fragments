package com.hjc.http.resp;

/**
 * @author 胡绍波
 * 本类为后台api接口状态返回对象类
 */
public class DefaultResponse implements Response{
    protected RespResult result;//状态信息

    public DefaultResponse() {
    }

    public DefaultResponse(RespResult result) {
        this.result = result;
    }

    @Override
    public RespResult getResult() {
        return result;
    }

    @Override
    public void setResult(RespResult result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "{" +
                "result:" + result +
                '}';
    }
}
