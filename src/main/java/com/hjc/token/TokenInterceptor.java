package com.hjc.token;

import com.alibaba.fastjson.JSONObject;
import com.hjc.http.resp.RespResult;
import com.hjc.http.resp.Response;
import com.hjc.http.resp.ResponseConstant;
import com.hjc.redis.aspect.annotation.HjcCacheable;
import com.hjc.utils.AspectMethod;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.UUID;

/**
 * @author 胡绍波
 */
@Aspect
@Component
public class TokenInterceptor{
    final Logger logger = LoggerFactory.getLogger(TokenInterceptor.class);

    public static final String KEY_NAME = "token";//缓存字段

    @Pointcut("@annotation(com.hjc.token.Token)")
    public void hjcTokenPoint(){
    }

    @Around("hjcTokenPoint()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        HttpServletRequest request =  ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Method currentMethod = AspectMethod.getMethod(pjp);
        Token annotation = currentMethod.getAnnotation(Token.class);//获取注解类
        if(annotation != null)
        {
            Object token = request.getSession(true).getAttribute(KEY_NAME);
            if(annotation.save())
            {
                if(token == null)//如果token不存在则创建新的token
                {
                    token = UUID.randomUUID().toString();
                    request.getSession(true).setAttribute(KEY_NAME,token);
                }
                return pjp.proceed();
            }
            else if (annotation.remove())
            {
                boolean repeat = isRepeatSubmit(token);
                if(repeat){
                    Object object = pjp.proceed();
                    if(object instanceof Response){
                        Response response = (Response) object;
                        if(!RespResult.SUCCESS.equals(response.getResult())){
                            return object;
                        }
                    }
                    request.getSession(true).removeAttribute(KEY_NAME);
                    return object;
                }
            }

            Class returnType = currentMethod.getReturnType();
            if(Objects.equals(Response.class,returnType)){
                return ResponseConstant.FORBIDDEN;
            }
            else if(Objects.equals(ModelAndView.class,returnType)){
                return new ModelAndView(null,HttpStatus.FORBIDDEN);
            }
        }
        else
        {
            return pjp.proceed();
        }
        return null;
    }

    /**对比token是否一样
     *
     * @param token
     * @return
     */
    private boolean isRepeatSubmit(Object token)
    {
        if(Objects.isNull(token)){
            return false;
        }
        return true;
    }
}
