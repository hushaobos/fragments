package com.hjc.sign.core.resolve;

import com.hjc.sign.core.common.Credential;
import com.hjc.sign.core.common.UserPwdCredential;
import com.hjc.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class UserPwdCredentailResolver implements CredentailResolver {
    Logger logger = LoggerFactory.getLogger(UserPwdCredentailResolver.class);

    //请求字段 username
    private static String REQUEST_PARAM_USERNAME="username";

    //请求字段password
    private static String REQUEST_PARAM_PASSWORD="password";

    @Override
    public Credential resolveCredentail(HttpServletRequest request){
        String username = request.getParameter(REQUEST_PARAM_USERNAME);//从request中获取用户名
        String passqord = request.getParameter(REQUEST_PARAM_PASSWORD);//从request中获取密码
        logger.debug("密码1 "+username+" "+passqord);
        UserPwdCredential userPwdCredential = null;
        boolean isComplete = !StringUtil.isEmpty(username) && !StringUtil.isEmpty(passqord);//判断用户名和密码是否为空
        if(isComplete)//用户名和密码都不为空时才存入凭据类当中
        {
            userPwdCredential = new UserPwdCredential();
            userPwdCredential.setUsername(username);
            userPwdCredential.setPassword(passqord);
        }
        return userPwdCredential;
    }
}
