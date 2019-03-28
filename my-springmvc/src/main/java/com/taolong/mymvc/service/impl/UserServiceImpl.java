package com.taolong.mymvc.service.impl;

import com.taolong.mymvc.annotatioin.MyService;
import com.taolong.mymvc.service.UserService;

@MyService
public class UserServiceImpl implements UserService {

	@Override
	public String saveUser(String name, String passwd) {
		System.out.println("name="+name+" passwd="+passwd);
		return "save user successful..."+"name="+name+" passwd="+passwd;
	}

}
