package chianghao.core.mybatis.sql.db_enum;

public enum SqlOrderType {
	asc("asc"),desc("desc");
	
	private SqlOrderType(String order) {
		this.order = order;
	}
	
	private String order;

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}
	
	
	
}
