package com.taolong.mybatis_myself.executor.parameter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface ParameterHandler {
	
	void setParameters(PreparedStatement ps) throws SQLException;

}
