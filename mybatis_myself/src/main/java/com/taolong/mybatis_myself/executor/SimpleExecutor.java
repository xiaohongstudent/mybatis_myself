package com.taolong.mybatis_myself.executor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.taolong.mybatis_myself.config.Configuration;
import com.taolong.mybatis_myself.config.MappedStatement;
import com.taolong.mybatis_myself.executor.parameter.DefaultParameterHandler;
import com.taolong.mybatis_myself.executor.parameter.ParameterHandler;
import com.taolong.mybatis_myself.executor.resultset.DefaultResultSetHandler;
import com.taolong.mybatis_myself.executor.resultset.ResultSetHandler;
import com.taolong.mybatis_myself.executor.statement.DefaultStatementHandler;
import com.taolong.mybatis_myself.executor.statement.StatementHandler;

public class SimpleExecutor implements Executor {

	private Configuration configuration;
	
	
	public SimpleExecutor(Configuration configuration) {
		super();
		this.configuration = configuration;
	}


	@Override
	public <E> List<E> query(MappedStatement ms, Object parameter) throws SQLException {
		//1.获取连接
		Connection connection = getConnection();
		//2.实例化statementhandler
		StatementHandler statementHandler = new DefaultStatementHandler(ms);
		//3.获取prepareStatement
		PreparedStatement prepare = statementHandler.prepare(connection);
		//4.实例化prepareHandler对象，sql语句占位符
		ParameterHandler parameterHandler = new DefaultParameterHandler(parameter);
		parameterHandler.setParameters(prepare);
		//5.查询
		ResultSet resutSet = statementHandler.query(prepare);
		//对resultSet进行处理
		ResultSetHandler resultSetHandler = new DefaultResultSetHandler(ms);
		return resultSetHandler.handleResultSets(resutSet);
	}
	
	
	/**
	 * 获取数据库的连接，和jdbc一样的方式
	 * @return
	 */
	private Connection getConnection() {
		Connection connection = null;
		try {
			Class.forName(configuration.getDbDriver());
			connection = DriverManager.getConnection(configuration.getDbUrl(),
							configuration.getDbUserName(), configuration.getDbPassWord());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return connection;
	}


	public Configuration getConfiguration() {
		return configuration;
	}


	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}
	
	

}
