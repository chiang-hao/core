package chianghao.core.mybatis.sql;

import java.util.ArrayList;
import java.util.List;

import chianghao.core.mybatis.sql.db_enum.SqlOrderType;

public class SqlOrder {

	private List<String[]> orderby;
	
	public SqlOrder() {
		orderby=new ArrayList<String[]>();
	}
	
	public void add(String fieldName,SqlOrderType type) {
		orderby.add(new String[] {fieldName,type.getOrder()});
	}
	
	public List<String[]> getOrderby() {
		return orderby;
	}
	
}
