package com.taolong.mybatis_myself.mapper;

import java.util.List;

import com.taolong.mybatis_myself.bean.TUser;


public interface TUserMapper {

	TUser selectByPrimaryKey(Integer id);
    
    List<TUser> selectAll();
}
