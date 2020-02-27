package haoframe.core.mybatis.sql;

import java.util.HashMap;
import java.util.Map;

import haoframe.core.mybatis.sql.db_enum.SqlConnector;
import haoframe.core.mybatis.sql.db_enum.SqlOperators;

public class SqlCondition {

	private SqlOperators operators;
	private Object value;
	private String fieldName;
	
	private boolean isCondition=true;
	private SqlConnector connector;
	
	
	public SqlCondition(String column, Object value) {
		this(column, SqlOperators.equals, value);
	}
	
	public SqlCondition(String column, SqlOperators operators, Object value) {
		this.fieldName = column;
		this.operators = operators;
		this.value = value;
	}

	public SqlCondition(String column, SqlOperators operators, Object[] values) {
		this.fieldName = column;
		this.operators = operators;
		this.value = values;
	}

	public SqlCondition(SqlConnector connector) {
		this.connector = connector;
		this.isCondition = false;
	}
	

	public boolean isCondition() {
		return isCondition;
	}

	public void setCondition(boolean isCondition) {
		this.isCondition = isCondition;
	}

	public SqlConnector getConnector() {
		return connector;
	}

	public void setConnector(SqlConnector connector) {
		this.connector = connector;
	}

	public SqlOperators getOperators() {
		return operators;
	}

	public void setOperators(SqlOperators operators) {
		this.operators = operators;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
	
	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public Map<String,Object> getParam(){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put(this.fieldName, this.value);
		return map;
	}
	
}
