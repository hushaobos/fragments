package com.hjc.http.resp;

import java.util.List;

/**
 * @author 胡绍波
 * 本类为默认列表结果返回类
 * 继承DefaultResponse
 * 实现ListResponse接口
 */
public class DefaultListResponse<T> extends DefaultResponse implements ListResponse<T>{
    private List<T> list;//列表
    private DefaultRespPagination pagination;//分页信息

    public DefaultListResponse() {
    }

    public DefaultListResponse(RespResult result, List<T> list, DefaultRespPagination pagination) {
        super(result);
        this.list = list;
        this.pagination = pagination;
    }

    @Override
    public void setList(List<T> list) {
        this.list = list;
    }

    @Override
    public List<T> getList() {
        return list;
    }

    @Override
    public DefaultRespPagination getPagination() {
        return pagination;
    }

    @Override
    public void setPagination(DefaultRespPagination pagination) {
        this.pagination = pagination;
    }

    @Override
    public String toString() {
        return "{" +
                "list:" + list +
                ", pagination:" + pagination +
                ", result:" + result +
                '}';
    }
}
