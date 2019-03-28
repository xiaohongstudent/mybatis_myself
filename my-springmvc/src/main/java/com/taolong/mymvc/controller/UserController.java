package com.taolong.mymvc.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.taolong.mymvc.annotatioin.MyAutoWired;
import com.taolong.mymvc.annotatioin.MyController;
import com.taolong.mymvc.annotatioin.MyRequestMapping;
import com.taolong.mymvc.annotatioin.MyRequestParam;
import com.taolong.mymvc.service.UserService;

@MyController("UserController")
@MyRequestMapping("/user")
public class UserController {

	@MyAutoWired
	private UserService userServiceabc;
	
	@MyRequestMapping("/saveUser")
	public void saveUser(HttpServletRequest request,HttpServletResponse response,
							@MyRequestParam("userName")String userName,
							@MyRequestParam("passwd") String password) {
		try {
			PrintWriter printWriter = response.getWriter();
			String result = userServiceabc.saveUser(userName, password);
			printWriter.write(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
