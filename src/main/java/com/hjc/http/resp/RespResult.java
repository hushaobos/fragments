package com.hjc.http.resp;

import java.io.Serializable;

/**
 * @author 胡绍波
 * 本类为接口状态集
 */
public class RespResult implements Serializable {
    private int code;//状态码
    private String description;//状态信息

    public static final RespResult SUCCESS = new RespResult(0,"ok!");//请求成功时返回的状态码
    public static final RespResult BAD_REQUEST = new RespResult(400,"Bad Request");//客户端传输数据有误
    public static final RespResult UNAUTHORIZED = new RespResult(401,"unauthorized");//服务端验证失败
    public static final RespResult FORBIDDEN = new RespResult(403,"Forbidden");//服务端拒绝访问
    public static final RespResult NOT_FOUND = new RespResult(404,"未找到记录");//没有找到请求地址
    public static final RespResult SEVER_ERROR = new RespResult(500,"服务器错误!");//服务器错误
    public static final RespResult PARAM_ERROR = new RespResult(1000,"param error!");//请求参数错误时返回的状态码
    public static final RespResult INSERT_FAILED = new RespResult(1001,"insert failed!");//添加失败
    public static final RespResult UPDATE_FAILED = new RespResult(1002,"modify failed!");//修改失败
    public static final RespResult ID_NULL = new RespResult(1003,"ID is not allow null!");//id不能为空
    public static final RespResult DELETE_FAIL = new RespResult(1004,"delete failed!");//删除失败
    public static final RespResult DATA_NOT_EXIST = new RespResult(1005,"data not exist!");//数据不存在

    public static final RespResult LOGIN_EMPRT = new RespResult(2001,"The login information is not empty!");//登录信息为空
    public static final RespResult USERNAME_NOTEXIST = new RespResult(2002,"username does not exist!");//用户名不存在时返回的状态码
    public static final RespResult PASSWORD_INCORRECT = new RespResult(2003,"password is incorrect!");//密码错误时返回的状态码

    public RespResult() {
    }

    public RespResult(int code, String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public String toString() {
        return "{" +
                "code:" + code +
                ", description:'" + description + '\'' +
                '}';
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
