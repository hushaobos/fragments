package com.hjc.http.resp;

/**
 * @author 胡绍波
 * 本类为封装有对象信息的结果类
 */
public class DefaultObjectResponse<T> extends DefaultResponse implements ObjectResponse<T>{
    public T info;//非list对象

    public DefaultObjectResponse(T info) {
        this.info = info;
    }

    public DefaultObjectResponse(RespResult result, T info) {
        super(result);
        this.info = info;
    }

    @Override
    public void setInfo(T info) {
        this.info = info;
    }

    @Override
    public T getInfo() {
        return info;
    }

    @Override
    public String toString() {
        return "{" +
                "info:" + info +
                ", result:" + result +
                '}';
    }
}
