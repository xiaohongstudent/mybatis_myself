package com.taolong.mymvc.argumentResolver;

import java.lang.reflect.Method;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.taolong.mymvc.annotatioin.MyService;

@MyService("httpServletResponseArgumentResolver")
public class HttpServletResponseArgumentResolver implements ArgumentResolver {
    //判断传进来的参数是否为response
    public boolean support(Class<?> type, int paramIndex, Method method) {
        return ServletResponse.class.isAssignableFrom(type);
    }
    //如果返回的参数是response,则直接返回
    public Object argumentResolver(HttpServletRequest request,
            HttpServletResponse response, Class<?> type, int paramIndex,
            Method method) {
        return response;
    }
    
}
