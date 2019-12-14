package com.hjc.http.resp;

import java.util.List;

/**
 * @author 胡绍波
 * 本接口定义列表返回结果
 */
public interface ListResponse<T> extends Response{

    @Override
    RespResult getResult();

    @Override
    void setResult(RespResult result);
    /**
     * 设置列表
     * @param list
     */
    void setList(List<T> list);

    /**
     * 获取列表
     * @return
     */
    List<T> getList();

    /**
     * 获取分页信息
     * @return
     */
    DefaultRespPagination getPagination();

    /**
     * 设置分页信息
     * @param pagination
     */
    void setPagination(DefaultRespPagination pagination);
}
