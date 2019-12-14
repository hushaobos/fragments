package com.hjc.sign.core.common;

public final class AuthConstant {
    /****  字段名常量参数  *****/
    public static final String REDIRECT_PARAM_NAME = "redirectURL";//token字段

    public static final String CLIENT_NAME = "client";

    /****TODO      客户端标识      ****/
    public static final String WEB_CLIENT = "web";
    public static final String APP_CLIENT = "app";

    //TODO cookie常量参数
    public static final String TOKEN_NAME = "hjc_token";
    public static final String ID_NAME = "hjcid";
    public static final String NICKNAME = "hjc_nk";//呢称字段
    public static final String CK_ROOT_PATH = "/";
    public static final int CK_DEFALUT_AGE = 30 * 60;//cookie默认过期时间，30分钟，单位秒
}
