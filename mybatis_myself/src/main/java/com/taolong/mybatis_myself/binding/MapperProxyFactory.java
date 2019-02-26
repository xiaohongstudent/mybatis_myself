package com.taolong.mybatis_myself.binding;

import java.lang.reflect.Proxy;

import com.taolong.mybatis_myself.session.SqlSession;

/**
 * @author hongtaolong
 * 动态代理类的生产工厂
 */
public class MapperProxyFactory {

	public static <T> T getMapperProxy(SqlSession sqlSession,Class<T> mapperInterface) {
		//创建一个invocationhandler的动态代理对象
		MapperProxy<T> mapperProxy = new MapperProxy<>(sqlSession, mapperInterface);
		return (T)Proxy.newProxyInstance(mapperInterface.getClassLoader(),
					new Class[] {mapperInterface}, mapperProxy);
	}
}
