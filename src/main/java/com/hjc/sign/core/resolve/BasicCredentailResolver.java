package com.hjc.sign.core.resolve;

import com.hjc.sign.core.common.AbstractParameter;
import com.hjc.sign.core.common.Credential;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;

public abstract class BasicCredentailResolver implements CredentailResolver {
    public Credential paramResolve(HttpServletRequest request) {
        Credential credential =  resolveCredentail(request);//执行凭据解析
        if(credential instanceof AbstractParameter)//如果credential 继承自AbstractParameter
        {
            AbstractParameter parameter = (AbstractParameter) credential;
            parameter.setParams(WebUtils.getParametersStartingWith(request,null));//从request中取出参数
        }
        return credential;
    }

    @Override
    public Credential resolveCredentail(HttpServletRequest request){
        return null;
    }
}
