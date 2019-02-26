package com.taolong.mybatis_myself.binding;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;

import com.taolong.mybatis_myself.session.SqlSession;

/**
 * @author hongtaolong
 * mapper接口的动态代理类
 */
public class MapperProxy<T> implements InvocationHandler{

	private SqlSession sqlSession;
	
	private final Class<T> mapperInterface;
	
	
	
	public MapperProxy(SqlSession sqlSession, Class<T> mapperInterface) {
		super();
		this.sqlSession = sqlSession;
		this.mapperInterface = mapperInterface;//被代理的对象
	}

	private <T> boolean isCollection(Class<T> type) {
		return Collection.class.isAssignableFrom(type);
	}
	
	//动态代理类最终调用的方法
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Object result = null;
		//object本身的方法，不进行增强
		if (Object.class.equals(method.getDeclaringClass())) {
			return method.invoke(this, args);
		}
		//获取接口的返回值，然后选择是调用selectone还是selectList
		Class<?> returnType = method.getReturnType();
		if (isCollection(returnType)) {
			result = sqlSession.selectList(mapperInterface.getName()+"."+method.getName(), args);
		}else {
			result = sqlSession.selectOne(mapperInterface.getName()+"."+method.getName(), args);
		}
		return result;
	}
	
}
