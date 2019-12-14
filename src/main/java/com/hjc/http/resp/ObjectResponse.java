package com.hjc.http.resp;

/**
 * @author 胡绍波
 * 本接口用于定义对象返回结果类
 */
public interface ObjectResponse<T> extends Response{
    @Override
    RespResult getResult();

    @Override
    void setResult(RespResult result);
    /**
     * 设置非list对象
     * @param info
     */
    void setInfo(T info);


    /**
     * 获取对象
     * @return
     */
    Object getInfo();
}
