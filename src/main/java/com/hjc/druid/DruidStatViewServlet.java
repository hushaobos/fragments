package com.hjc.druid;

import com.alibaba.druid.support.http.StatViewServlet;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

/**
 * Created by server1 on 2017/7/12.
 */
@SuppressWarnings("serial")
//@WebServlet(urlPatterns = "/druid/*",
//        initParams = {
//                @WebInitParam(name = "allow", value = "127.0.0.1"),// IP白名单 (没有配置或者为空，则允许所有访问)
//                @WebInitParam(name = "loginUsername", value = "hjc718"),// 用户名
//                @WebInitParam(name = "loginPassword", value = "Ble654321"),// 密码
//                @WebInitParam(name = "resetEnable", value = "true")// 禁用HTML页面上的“Reset All”功能
//        })
public class DruidStatViewServlet extends StatViewServlet {

}
