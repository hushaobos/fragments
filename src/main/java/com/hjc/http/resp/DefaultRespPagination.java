package com.hjc.http.resp;

/**
 * @author 胡绍波
 * 本类为 分页信息类
 */
public class DefaultRespPagination {
    private long current;//当前页数
    private long total;//总页数
    private long pageSize;//每页个数

    public long getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }
    public long getCurrent() {
        return current;
    }

    public void setCurrent(long current) {
        this.current = current;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "{" +
                "current:" + current +
                ", total:" + total +
                ", pageSize:" + pageSize +
                '}';
    }
}
