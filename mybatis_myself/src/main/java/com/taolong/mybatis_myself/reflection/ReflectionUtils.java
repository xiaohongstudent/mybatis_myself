package com.taolong.mybatis_myself.reflection;

import java.lang.reflect.Field;

/**
 * @author hongtaolong
 * 为bean设置属性值得工具类
 */
public class ReflectionUtils {
	
	public static void setBeanProp(Object bean,String propName,Object propValue) {
		Field field = null;
		try {
			field = bean.getClass().getDeclaredField(propName);
			field.setAccessible(true);
			field.set(bean, propValue);
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
