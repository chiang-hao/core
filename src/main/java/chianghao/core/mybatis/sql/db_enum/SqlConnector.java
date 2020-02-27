package chianghao.core.mybatis.sql.db_enum;

public enum SqlConnector {

	and("and"),or("or"),andStart("and ("),orStart("or ("),end(")");
	
	private SqlConnector(String connector) {
		this.connector = connector;
	}
	
	private String connector;

	public String getConnector() {
		return connector;
	}

	public void setConnector(String connector) {
		this.connector = connector;
	}
	
	
	
}
