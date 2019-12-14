package com.hjc.http.utils;

import javax.servlet.http.Cookie;

public class CookieUtils {

    /**实例化一个cookie
     *
     * @param name cookie名
     * @param value cookie值
     * @param path cookie根目录
     * @param domain cookie的domain
     * @return
     */
    public static Cookie initCookie(String name, String value, String path,  String domain)
    {
        Cookie cookie = new Cookie(name,value);//设置cookie值
        cookie.setPath(path);//根目录
//        cookie.setMaxAge(maxAge);//有效时间
        cookie.setDomain(domain);//设置domain
        return cookie;
    }
}
