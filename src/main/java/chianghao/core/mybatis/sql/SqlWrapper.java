package chianghao.core.mybatis.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import chianghao.core.mybatis.sql.db_enum.SqlConnector;
import chianghao.core.mybatis.sql.db_enum.SqlOperators;
import chianghao.core.mybatis.sql.db_enum.SqlOrderType;

public class SqlWrapper {

	List<SqlCondition> sqlWhereList;
	
	Set<String> fieldNames;
	
	Map<String,Object> params;
	
	SqlOrder sqlOrder;
	
	public SqlWrapper() {
		sqlWhereList = new ArrayList<SqlCondition>();
		params = new HashMap<String,Object>();
		fieldNames = new HashSet<>();
		sqlOrder = new SqlOrder();
	}
	
	public String[] getFieldNames() {
		String[] array = fieldNames.toArray(new String[fieldNames.size()]);
		return array;
	}

	
	public SqlWrapper addFields(String... fields) {
		for(String field:fields) {
			fieldNames.add(field);
		}
		return this;
	}
	
	public SqlWrapper order(String fieldName,SqlOrderType type) {
		if(StringUtils.isEmpty(fieldName)) {
			return this;
		}
		this.sqlOrder.add(fieldName, type);
		return this;
	}
	
	public Map<String, Object> getParams() {
		return params;
	}
	public void setParams(Map<String, Object> params) {
		this.params = params;
	}

	public SqlWrapper addCondition(String fieldName, Object value) {
		sqlWhereList.add(new SqlCondition(fieldName,value));
		params.put(fieldName, value);
		return this;
	}
	
	public SqlWrapper addCondition(String fieldName, SqlOperators operators,Object value) {
		sqlWhereList.add(new SqlCondition(fieldName,operators,value));
		params.put(fieldName, value);
		return this;
	}

	public SqlWrapper addCondition(String fieldName, SqlOperators operators,Object[] value) {
		sqlWhereList.add(new SqlCondition(fieldName,value));
		params.put(fieldName, value);
		return this;
	}
	
	public SqlWrapper and() {
		sqlWhereList.add(new SqlCondition(SqlConnector.and));
		return this;
	}

	public SqlWrapper andStart() {
		sqlWhereList.add(new SqlCondition(SqlConnector.andStart));
		return this;
	}

	public SqlWrapper or() {
		sqlWhereList.add(new SqlCondition(SqlConnector.or));
		return this;
	}

	public SqlWrapper orStart() {
		sqlWhereList.add(new SqlCondition(SqlConnector.orStart));
		return this;
	}

	public SqlWrapper end() {
		sqlWhereList.add(new SqlCondition(SqlConnector.end));
		return this;
	}

	public List<SqlCondition> getSqlWhereList() {
		return sqlWhereList;
	}

	public void setSqlWhereList(List<SqlCondition> sqlWhereList) {
		this.sqlWhereList = sqlWhereList;
	}

	public SqlOrder getSqlOrder() {
		return sqlOrder;
	}

	public void setSqlOrder(SqlOrder sqlOrder) {
		this.sqlOrder = sqlOrder;
	}
	
	
}
