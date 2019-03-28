package com.taolong.mymvc.servlet;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.taolong.mymvc.annotatioin.MyAutoWired;
import com.taolong.mymvc.annotatioin.MyController;
import com.taolong.mymvc.annotatioin.MyRequestMapping;
import com.taolong.mymvc.annotatioin.MyService;
import com.taolong.mymvc.handlerAdapter.HandlerAdapterService;

public class DispatcherServlet extends HttpServlet {

	//存扫描到的类的名称
	private List<String> classNames = new ArrayList<String>();
	//存放实例化的所有的类的对象
	private Map<String,Object> beans = new HashMap<String, Object>();
	//uri和method的对应关系
	private Map<String, Object> handlerMap = new HashMap<String,Object>();
	//uri和类的对应关系
	private Map<String, Object> clazzHandlerMap = new HashMap<String,Object>();
	
	private static String HANDLERADAPTER = "myHandlerAdapter";
	
	@Override
	public void init() throws ServletException {
		//1、扫描包下面的所有的类的全命名，加入到集合中
		scanPackage("com.taolong");
		for (String className : classNames) {
			System.out.println("className="+className);
		}
		//2、实例化所有的扫描出来的类名，加入到容器中
		instanceScanedBeans();
		for (Map.Entry<String, Object> entry : beans.entrySet()) {
			System.out.println(entry.getKey() + ":" + entry.getValue());
		}
		//3、依赖注入
		iocDI();
		//4、将url和method绑定
		handlerMapping();
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		String uri = req.getRequestURI();
		String context = req.getContextPath();
		System.out.println("uri="+uri+" context="+context);
		//去掉uri前面的context
		String path = uri.replace(context, "");
		if (!handlerMap.containsKey(path)) {
			resp.getWriter().write("404 NOT FOUNT");
			return;
		}
		//根据uri找到需要调用的方法
		Method method = (Method)handlerMap.get(path);
		//UserController instance = (UserController)beans.get("UserController");
		//简单处理，默认controller类的requestmapping存在且有值
		String clzzUrl = path.split("/")[1];
		Object instance = clazzHandlerMap.get(clzzUrl);
		//处理器
		HandlerAdapterService ha = (HandlerAdapterService) beans.get(HANDLERADAPTER);
		//使用策略模式处理method中的参数
		Object[] args = ha.hand(req, resp, method, beans);
		try {
			method.invoke(instance, args);		
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 将requestMapping中的url和对应的方法进行绑定
	 */
	private void handlerMapping() {
		if (beans.isEmpty() || beans.entrySet().isEmpty()) {
			System.out.println("没有实例化类！");
			return;
		}
		for (Map.Entry<String, Object> entry : beans.entrySet()) {
			Object instance = entry.getValue();
			Class<?> clazz = instance.getClass();
			//首先读取类中的requestmapping的url
			if (clazz.isAnnotationPresent(MyRequestMapping.class)) {
				MyRequestMapping annotation = clazz.getAnnotation(MyRequestMapping.class);
				String classPathUrl = annotation.value();
				//保存url关联的controller，方便dopost时找到对应的类
				//简单处理，默认是controller类中存在mapping值，可优化
				clazzHandlerMap.put(classPathUrl.replace("/", ""), instance);
				Method[] methods = clazz.getMethods();
				if (methods == null || methods.length == 0) {
					System.out.println("没有对应的方法!");
					return;
				}
				for (Method method : methods) {
					if (method.isAnnotationPresent(MyRequestMapping.class)) {
						MyRequestMapping annotationMethod = method.getAnnotation(MyRequestMapping.class);
						String methodPathUrl = annotationMethod.value();
						//key为对应的url，value为映射的方法
						handlerMap.put(classPathUrl+methodPathUrl, method);
					}else {
						continue;
					}
				}
			}else {
				continue;
			}
		}
	}
	
	/**
	 * 依赖注入，如controller中使用autowired注入service
	 */
	private void iocDI() {
		if (beans.isEmpty() || beans.entrySet().isEmpty()) {
			System.out.println("没有实例化的类！");
			return;
		}
		for (Map.Entry<String, Object> entry : beans.entrySet()) {
			Object instance = entry.getValue();
			Class<?> clazz = instance.getClass();
			//通过反射获取类的属性,为简化代码，这里只在controller中注入，service中无注入
			if (clazz.isAnnotationPresent(MyController.class)) {
				Field[] fields = clazz.getDeclaredFields();//获取所有属性
				for (Field field : fields) {
					//其实就是判断属性是否被autowired修饰
					if (field.isAnnotationPresent(MyAutoWired.class)) {
						MyAutoWired annotation = field.getAnnotation(MyAutoWired.class);
						String value = annotation.value();
						field.setAccessible(true);//增加权限
						if ("".equals(value)) {
							//这里也是简单处理，因为是面向接口编程的，所以为了找到前面实例化的service，应该拿到它的接口名
							//value=com.taolong.service.UserService
							value = field.getType().getName();
							//value=userService
							value = toLowerFirstWord(value.substring(value.lastIndexOf(".")+1));
						}
						try {
							//注入到属性中
							field.set(instance, beans.get(value));
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
					}else {
						continue;
					}
				}
			}else {
				continue;
			}
		}
	}
	
	/**
	 * 将扫描的类，通过反射实例化，然后加入到map中
	 */
	private void instanceScanedBeans() {
		if (classNames.isEmpty()) {
			System.out.println("未扫描到任何的类！");
			return;
		}
		for (String className : classNames) {
			//去掉.class，方便反射处理
			String cn = className.replace(".class", "");
			try {
				Class<?> clazz = Class.forName(cn);
				//判断类是否被mycontroller注释过，目前做简单处理，只实例化被mycontroller和myservice注释过的类
				if (clazz.isAnnotationPresent(MyController.class)) {
					MyController annotation = clazz.getAnnotation(MyController.class);
					Object instance = clazz.newInstance();//实例化
					String instanceKey = annotation.value();//获取annotation的value
					if ("".equals(instanceKey)) {//如果注释中没有值，则将类的名字当作key（简单处理）
						instanceKey = toLowerFirstWord(clazz.getSimpleName());
					}
					beans.put(instanceKey, instance);
				}else if(clazz.isAnnotationPresent(MyService.class)) {//判断是否被myservice注释
					MyService annotation = clazz.getAnnotation(MyService.class);
					Object instance = clazz.newInstance();
					String instanceKey = annotation.value();
					if ("".equals(instanceKey)) {
						//这里做了简单处理，直接获取serviceimpl的接口名当作key
						instanceKey = toLowerFirstWord(clazz.getInterfaces()[0].getSimpleName());
					}
					beans.put(instanceKey, instance);
				}else {
					continue;
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	
	/**
	 * 扫描指定包下面的所有的类名
	 * @param basePackage
	 */
	private void scanPackage(String basePackage) {
		URL url = this.getClass().getClassLoader().getResource("/"+replaceTo(basePackage));
		System.out.println("url = "+url);
		String parentPath = url.getFile();
		File file = new File(parentPath);
		String[] fileList = file.list();
		if (null == fileList || fileList.length == 0) return;
		for (String fileName : fileList) {
			File filePath = new File(parentPath+fileName);
			//如果是文件夹，则继续递归扫描
			if (filePath.isDirectory()) {
				scanPackage(basePackage+"."+fileName);
			}else {//将class文件加入到集合中，方便下次实例化
				classNames.add(basePackage+"."+filePath.getName());
			}
		}
	}
	
	private String replaceTo(String basePackage) {
		return basePackage.replaceAll("\\.", "/");
	}
	
	/**
	  * 把字符串的首字母小写
	  * @param name
	  * @return
	  * eg.UserService->userService
	  */
	 private String toLowerFirstWord(String name){
		 if (null == name || "".equals(name)) return null;
	   char[] charArray = name.toCharArray();
	   charArray[0] += 32;
	   return String.valueOf(charArray);
	 }
	
}
