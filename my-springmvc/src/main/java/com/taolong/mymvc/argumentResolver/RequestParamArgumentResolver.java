package com.taolong.mymvc.argumentResolver;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.taolong.mymvc.annotatioin.MyRequestParam;
import com.taolong.mymvc.annotatioin.MyService;

@MyService("requestParamArgumentResolver")
//解析声明注解为RequestParam, 获取注解的值
public class RequestParamArgumentResolver implements ArgumentResolver {
	//判断传进来的参数是否为EnjoyRequestParam
    public boolean support(Class<?> type, int paramIndex, Method method) {
        
        Annotation[][] an = method.getParameterAnnotations();
        
        Annotation[] paramAns = an[paramIndex];
        
        for (Annotation paramAn : paramAns) {
        	//判断传进的paramAn.getClass()是不是 EnjoyRequestParam 类型
        	if (MyRequestParam.class.isAssignableFrom(paramAn.getClass())) {
                return true;
            }
        }
        return false;
    }
    //参数解析,并获取注解的值
    public Object argumentResolver(HttpServletRequest request,
            HttpServletResponse response, Class<?> type, int paramIndex,
            Method method) {
        
        Annotation[][] an = method.getParameterAnnotations();
        
        Annotation[] paramAns = an[paramIndex];
        
        for (Annotation paramAn : paramAns) {
            if (MyRequestParam.class.isAssignableFrom(paramAn.getClass())) {
            	MyRequestParam rp = (MyRequestParam)paramAn;
                
                String value = rp.value();
                return request.getParameter(value);
            }
        }
        
        return null;
    }
    
}
