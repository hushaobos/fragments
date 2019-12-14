package com.hjc.http.resp;

/**
 * @author 胡绍波
 * 本类把较常用的返回结果
 */
public class ResponseConstant {
    /**  HTTP 状态码  **/
    public static final Response UNAUTHORIZED_RESULT = new DefaultResponse(RespResult.UNAUTHORIZED);//验证失败
    public static final Response FORBIDDEN = new DefaultResponse(RespResult.FORBIDDEN);//拒绝访问

    /**  1000 基础状态码 **/
    public static final Response SUCCESS_RESULT = new DefaultResponse(RespResult.SUCCESS);//成功
    public static final Response PARAM_ERROR_RESULT = new DefaultResponse(RespResult.PARAM_ERROR);//参数错误
    public static final Response ID_NULL_RESULT = new DefaultResponse(RespResult.ID_NULL);//id为空
    public static final Response DELETE_FAIL_RESULT = new DefaultResponse(RespResult.DELETE_FAIL);//删除失败
    public static final ObjectResponse INSERT_FAILED_RESULT = new DefaultObjectResponse(RespResult.INSERT_FAILED);//新增失败
    public static final Response UPDATE_FAILED_RESULT = new DefaultResponse(RespResult.UPDATE_FAILED);//修改失败
    public static final Response DATA_NOT_EXIST_RESULT = new DefaultResponse(RespResult.DATA_NOT_EXIST);//数据不存在

    /**  2000  登录状态码   **/
    public static final Response LOGIN_EMPRT_RESULT = new DefaultResponse(RespResult.LOGIN_EMPRT);//登录信息为空
    public static final Response USERNAME_NOTEXIST_RESULT = new DefaultResponse(RespResult.USERNAME_NOTEXIST);//用户名不存在时返回的状态码
    public static final Response PASSWORD_INCORRECT_RESULT = new DefaultResponse(RespResult.PASSWORD_INCORRECT);//密码错误时返回的状态码
}
