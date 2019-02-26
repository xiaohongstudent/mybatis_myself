package com.taolong.mybatis_myself.executor;

import java.sql.SQLException;
import java.util.List;

import com.taolong.mybatis_myself.config.MappedStatement;

/**
 * @author hongtaolong
 * sqlsession操作数据库的任务都是由executor完成
 */
public interface Executor {

	<E> List<E> query(MappedStatement ms,Object parameter) throws SQLException;
}
