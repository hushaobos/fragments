package com.hjc.sign.core.resolve;

import com.hjc.sign.core.common.AuthConstant;
import com.hjc.sign.core.common.Credential;
import com.hjc.sign.core.common.EncryCredential;
import com.hjc.sign.core.common.EncryCredentialInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

@Service
public class EncryCredentialResolver implements CredentailResolver {
    Logger logger = LoggerFactory.getLogger(EncryCredentialResolver.class);

    /**设置credential
     *
     * @param value
     * @return
     */
    public EncryCredential setEncryCredentialValue(String value, Long userId)
    {
        EncryCredential credential = new EncryCredential();
        credential.setCredential(value);//设置认证凭证
        EncryCredentialInfo credentialInfo = new EncryCredentialInfo();
        credentialInfo.setUserId(userId);
        return credential;
    }

    /**解析凭据
     *
     * @param request
     * @return
     */
    @Override
    public Credential resolveCredentail(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();//获取request中的cookie
        logger.debug("cookies "+cookies);
        if(cookies != null && cookies.length > 0)//cookies不为空并则遍历cookie
        {
            String value = null;
            Long userId = null;
            for (Cookie cookie : cookies)
            {
                logger.debug("cookies name "+cookie.getName());
                if(AuthConstant.TOKEN_NAME.toString().equals(cookie.getName()))//如果cookie名字为wlf_token，则设置EncryCredential
                {
                    value = cookie.getValue();
                }
                if(AuthConstant.ID_NAME.toString().equals(cookie.getName()))//如果cookie名字为wlf_token，则设置EncryCredential
                {
                    userId = Long.valueOf(cookie.getValue());
                }
            }

            if (value == null)
            {
                value = request.getParameter(AuthConstant.TOKEN_NAME);
            }
            if (userId == null)
            {
                userId = Long.valueOf(request.getParameter(AuthConstant.ID_NAME));
            }

            if (value == null)
            {
                value = request.getHeader(AuthConstant.TOKEN_NAME);
            }
            if (userId == null)
            {
                userId = Long.valueOf(request.getHeader(AuthConstant.ID_NAME));
            }

            try {
                value = URLDecoder.decode(value,"UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            return setEncryCredentialValue(value,userId);//设置认证凭证
        }
        return null;
    }
}
