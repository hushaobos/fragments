package com.hjc.sign.core.resolve;

import com.hjc.sign.core.common.Credential;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class CompleteCredentailResolver extends BasicCredentailResolver implements CredentailResolver {
    @Autowired
    private UserPwdCredentailResolver userPwdCredentailResolver;

    @Autowired
    private EncryCredentialResolver encryCredentialResolver;

    /**从request中解析出凭据
     *
     * @param request
     * @return
     */
    @Override
    public Credential resolveCredentail(HttpServletRequest request){
        Credential credential = null;

        if(credential == null)
        {
            credential = userPwdCredentailResolver.resolveCredentail(request);//获取request中的用户名密码
        }

        if(credential == null)
        {
            credential = encryCredentialResolver.resolveCredentail(request);//获取cookie
        }
        return credential;
    }
}
