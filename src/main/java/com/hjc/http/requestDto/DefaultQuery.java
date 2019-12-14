package com.hjc.http.requestDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * 默认查询参数类
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DefaultQuery implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty(value = "current",defaultValue = "1")
    private int pageNum = 1;
    @JsonProperty(value = "pageSize",defaultValue = "20")
    private int pageSize = 20;

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
