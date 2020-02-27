package haoframe.core.db.sql;

import java.util.List;

/**
 * sql查询内容
 * @author Administrator
 *
 */
public class Sql {

	private String sql;
	private List<Object> args;
	
	public Sql() {}
	
	public Sql(String sql, List<Object> args) {
		this.sql = sql;
		this.args = args;
	}
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	public List<Object> getArgs() {
		return args;
	}
	public void setArgs(List<Object> args) {
		this.args = args;
	}
	
}
