package haoframe.core.db.manage;

/**
 * 数据类型
 * @author chianghao
 * @time   2018年4月18日
 */
public enum DBType {

	mysql("MYSQL"),
	oracle("ORACLE"),
	mssql("MSSQL");
	
	private String name;
	private String driver;
	private String url;
	
	
	private DBType(String name){
		this.name   = name;
		this.driver = "";
		this.url    = "";
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDriver() {
		return driver;
	}
	public void setDriver(String driver) {
		this.driver = driver;
	}
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}


	public static DBType getDbType(String name){
		for(DBType dbtype:DBType.values()){
			if(dbtype.getName().equals(name)){
				return dbtype;
			}
		}
		return null;
	}
	
}
