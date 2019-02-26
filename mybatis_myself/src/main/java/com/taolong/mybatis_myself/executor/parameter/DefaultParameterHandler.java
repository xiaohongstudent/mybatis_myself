package com.taolong.mybatis_myself.executor.parameter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DefaultParameterHandler implements ParameterHandler {

	private Object parameter;
	
	
	
	public DefaultParameterHandler(Object parameter) {
		super();
		this.parameter = parameter;
	}

	@Override
	public void setParameters(PreparedStatement ps) throws SQLException {
		if (parameter == null) return;
		if (parameter.getClass().isArray()) {
			Object[] paramArray = (Object[]) parameter;
			int parameterIndex = 1;
			for (Object param : paramArray) {
				if (param instanceof Integer) {
					ps.setInt(parameterIndex, (int)param);
				}else if(param instanceof String) {
					ps.setString(parameterIndex, (String)param);
				}//more type...
				parameterIndex ++;
			}
		}
	}

	public Object getParameter() {
		return parameter;
	}

	public void setParameter(Object parameter) {
		this.parameter = parameter;
	}
	
	

}
