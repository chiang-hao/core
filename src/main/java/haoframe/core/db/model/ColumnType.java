package haoframe.core.db.model;

/***
 * 数据表字段类型
 * @author chianghao
 *
 */
public enum ColumnType {
	
	BIT           (   -7,"BIT","java.lang.Integer"),
	TINYINT       (   -6,"TINYINT","java.lang.Integer"),
	SMALLINT      (    5,"",""),
	INTEGER       (    4,"INTEGER","java.lang.Integer"),
	BIGINT        (   -5,"BIGINT","java.lang.Long"),
	FLOAT         (    6,"FLOAT","java.lang.Float"),
	REAL          (    7,"",""),
	DOUBLE        (    8,"DOUBLE","java.lang.Double"),
	NUMERIC       (    2,"NUMERIC"," java.math.BigDecimal"),
	DECIMAL       (    3,"DECIMAL"," java.math.BigDecimal"),
	CHAR          (    1,"CHAR","java.lang.Character"),
	VARCHAR       (   12,"VARCHAR","java.lang.String"),
	LONGVARCHAR   (   -1,"",""),
	DATE          (   91,"DATE","java.util.Date"),
	TIME          (   92,"TIME","java.util.Time"),
	TIMESTAMP     (   93,"DATETIME","java.util.Timestamp"),
	BINARY        (   -2,"",""),
	VARBINARY     (   -3,"",""),
	LONGVARBINARY (   -4,"",""),
	NULL          (    0,"",""),
	OTHER         ( 1111,"",""),
	JAVA_OBJECT   ( 2000,"",""),
	DISTINCT      ( 2001,"",""),
	STRUCT        ( 2002,"",""),
	ARRAY         ( 2003,"",""),
	BLOB          ( 2004,"BLOB","java.lang.Object"),
	CLOB          ( 2005,"mediumtext","java.lang.Object"),
	REF           ( 2006,"REAL","java.lang.Float"),
	DATALINK      (   70,"",""),
	BOOLEAN       (   16,"",""),
	ROWID         (   -8,"",""),
	NCHAR         (  -15,"",""),
	NVARCHAR      (   -9,"",""),
	LONGNVARCHAR  (  -16,"",""),
	NCLOB         ( 2011,"",""),
	SQLXML        ( 2009,"","");
	
	
	
	private int    code;     //对应java.sql.types
	private String mysqlDBType;
	private String javatype;
	
	private ColumnType(int code,String mysqlDBType,String javatype) {
		this.code = code;
		this.mysqlDBType = mysqlDBType;
		this.javatype = javatype;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
	
	public String getMysqlDBType() {
		return mysqlDBType;
	}

	public void setMysqlDBType(String mysqlDBType) {
		this.mysqlDBType = mysqlDBType;
	}

	public String getJavatype() {
		return javatype;
	}

	public void setJavatype(String javatype) {
		this.javatype = javatype;
	}
	
	public static ColumnType getByCode(String code) {
		for(ColumnType type:ColumnType.values()) {
			if(String.valueOf(type.getCode()).equals(code)) {
				return type;
			}
		}
		return null;
	}
	
}
