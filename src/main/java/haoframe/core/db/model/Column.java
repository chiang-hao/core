package haoframe.core.db.model;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;

import haoframe.core.db.annotation.Property;
import haoframe.core.exception.HaoException;
import haoframe.core.utils.StringUtils;

/***
 * 类对象属性
 * @author chianghao
 *
 */
public class Column {

	//-----------------对象信息-----------------//
	private Class<?>     clazz;
	private Field        field;
	private Class<?>     fieldType;
	//-----------------固定信息-----------------//
	private String       name;
	private ColumnType   type;
	//-----------------注解信息-----------------//
	private String       title;
	private String       remark;
	private int          length;
	private int          precision;
	private boolean      primary;
	private String       defaultValue;
	private boolean      isNull;
	
	
	public static Column createProperty(Class<?> clazz,Field field) {
		if(!field.isAnnotationPresent(Property.class)) {
			return null;
		}
		Column column = new Column(clazz,field);
		Property property  =  field.getAnnotation(Property.class);
		column.title         = property.title();
		column.remark        = column.title+"["+property.remark()+"]";
		column.length        = property.length();
		column.precision     = property.precision();
		column.primary       = property.primary();
		column.isNull        = property.isNull();
		column.defaultValue  = property.defaultValue();
		
		if(!StringUtils.isEmpty(property.columnName())) {
			column.name = property.columnName(); 
		}
		
		column.javaTypeToDbType();
		return column;
	}
	
	
	private void javaTypeToDbType() {
		if(this.fieldType.getName().equals("java.lang.Object")) {
			this.type = ColumnType.CLOB;
		}else if(this.fieldType.isAssignableFrom(Boolean.class)||this.fieldType.isAssignableFrom(boolean.class)) {
			this.type = ColumnType.TINYINT;
		}else if(this.fieldType.isAssignableFrom(BigDecimal.class)) {
			//小数
			this.type = ColumnType.DECIMAL;
		}else if(this.fieldType.isAssignableFrom(Date.class)) {
			//日期
			this.type = ColumnType.TIMESTAMP;
			//this.length=19;
		}else if(this.fieldType.isAssignableFrom(Timestamp.class)) {
			//时间
			this.type = ColumnType.TIMESTAMP;
			//this.length=19;
		}else if(this.fieldType.isAssignableFrom(char.class)) {
			//字符
			this.type = ColumnType.CHAR;
			this.length=1;
		}else if(this.fieldType.isAssignableFrom(byte.class)) {
			//字节.二进制的数据
			this.type = ColumnType.BIT;
			this.length=1;
		}else if(this.fieldType.getName().equals("java.lang.String")) {
			if(this.length==0) {
				this.length=255;
			}
			this.type = ColumnType.VARCHAR;
			if(this.length>4000) {
				this.type = ColumnType.CLOB;
			}
		}else if(this.fieldType.isAssignableFrom(Integer.class)||this.fieldType.isAssignableFrom(int.class)) {
			//整形
			this.type = ColumnType.INTEGER;
			if(this.length>10||this.length==0) {
				this.length=10;
			}
		}else if(this.fieldType.isAssignableFrom(Long.class)||this.fieldType.isAssignableFrom(long.class)) {
			//长整形
			this.type = ColumnType.BIGINT;
			if(this.length<10) {
				this.length=19;
			}
		}else if(this.fieldType.isAssignableFrom(Float.class)||this.fieldType.isAssignableFrom(float.class)) {
			//单精度
			this.type = ColumnType.FLOAT;
			if(this.precision==0) {
				this.precision = 2;
			}
		}else if(this.fieldType.isAssignableFrom(Double.class)||this.fieldType.isAssignableFrom(double.class)) {
			//双精度
			this.type = ColumnType.DOUBLE;
			if(this.precision==0) {
				this.precision = 6;
			}
		}
	}
	/**
	 * 构造函数
	 * @param clazz
	 * @param field
	 */
	private Column(Class<?> clazz,Field field) {
		this.clazz = clazz;
		this.field = field;
		this.name = StringUtils.camelToUnderline(this.field.getName());
		this.fieldType = field.getType();
	}
	
	public Column() {
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}


	public boolean isPrimary() {
		return primary;
	}

	public void setPrimary(boolean primary) {
		this.primary = primary;
	}
	public ColumnType getType() {
		return type;
	}

	public void setType(ColumnType type) {
		this.type = type;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getPrecision() {
		return precision;
	}

	public void setPrecision(int precision) {
		this.precision = precision;
	}

	public boolean isNull() {
		return isNull;
	}

	public void setNull(boolean isNull) {
		this.isNull = isNull;
	}

	public Class<?> getFieldType() {
		return fieldType;
	}

	public void setFieldType(Class<?> fieldType) {
		this.fieldType = fieldType;
	}

	public String getDefaultValue() {
		return defaultValue;
	}


	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}


	public String getFieldClassTypeName() {
		return this.fieldType.getName();
	}

	public String getFieldName() {
		return this.field.getName();
	}
	
	public Object getValue(ResultSet rs) {
		try {
			if(rs.getObject(this.name)==null) {
				return null;
			}
			if(this.fieldType.getName().equals("java.lang.Object")) {
				return rs.getString(name);
			}else if(this.fieldType.getName().equals("java.lang.String")) {
				return rs.getString(name);
			}else if(this.fieldType.isAssignableFrom(Integer.class)||this.fieldType.isAssignableFrom(int.class)) {
				return rs.getInt(name);
			}else if(this.fieldType.isAssignableFrom(Boolean.class)||this.fieldType.isAssignableFrom(boolean.class)) {
				return rs.getBoolean(name);
			}else if(this.fieldType.isAssignableFrom(Long.class)||this.fieldType.isAssignableFrom(long.class)) {
				//长整形
				return rs.getLong(name);
			}else if(this.fieldType.isAssignableFrom(Float.class)||this.fieldType.isAssignableFrom(float.class)) {
				//单精度
				return rs.getFloat(name);
			}else if(this.fieldType.isAssignableFrom(Double.class)||this.fieldType.isAssignableFrom(double.class)) {
				//双精度
				return rs.getDouble(name);
			}else if(this.fieldType.isAssignableFrom(BigDecimal.class)) {
				//小数
				return rs.getBigDecimal(name);
			}else if(this.fieldType.isAssignableFrom(Date.class)) {
				Timestamp t = rs.getTimestamp(name);
				return t != null ? new Date(t.getTime()) : null;
				//this.length=19;
			}else if(this.fieldType.isAssignableFrom(Timestamp.class)) {
				//时间
				return rs.getTimestamp(name);
			}else if(this.fieldType.isAssignableFrom(char.class)) {
				//字符
				return rs.getCharacterStream(name);
			}else if(this.fieldType.isAssignableFrom(byte.class)) {
				//字节.二进制的数据
				return rs.getByte(name);
			}
			throw new HaoException("Mapper_Result_Error",fieldType.getName());
		}catch(Exception e) {
			e.printStackTrace();
			throw new HaoException("Mapper_Result_Error","get" + name + " error"+e.getMessage());
		}
	}

	
	
//	public Object formatterObjectValue(Object value) {
//		if(int.class.isAssignableFrom(fieldType)||Integer.class.isAssignableFrom(fieldType)){
//			return ConverterUtils.objectToInt(value);
//		}else if(fieldType.isAssignableFrom(Short.class)) {
//			return ConverterUtils.objectToInt(value);
//		}else if(fieldType.isAssignableFrom(Long.class)) {
//			return ConverterUtils.objectToLong(value);
//		}else if(fieldType.isAssignableFrom(Float.class)) {
//			return ConverterUtils.objectToFloat(value);
//		}else if(fieldType.isAssignableFrom(Double.class)) {
//			return ConverterUtils.objectToDouble(value);
//		}else if(fieldType.isAssignableFrom(BigDecimal.class)) {
//			return ConverterUtils.objectToDecimal(value);
//		}else if(fieldType.isAssignableFrom(Date.class)) {
//			return ConverterUtils.objectToDate(value,"yyyy-MM-dd");
//		}else if(fieldType.isAssignableFrom(Timestamp.class)) {
//			return ConverterUtils.objectToTimestamp(value,"yyyy-MM-dd HH:mm:ss");
//		}else if(fieldType.isAssignableFrom(char.class)) {
//			return ConverterUtils.objectToChar(value);
//		}else if(fieldType.isAssignableFrom(byte.class)) {
//			return ConverterUtils.objectToByte(value);
//		}else if(fieldType.isAssignableFrom(String.class)) {
//			return ConverterUtils.objectToString(value);
//		}else if(Boolean.class.isAssignableFrom(fieldType)||boolean.class.isAssignableFrom(fieldType)) {
//			try {
//				if(value instanceof Integer) {
//					if(value.equals(1)) {
//						return true;
//					}
//				}
//				if(value instanceof String) {
//					return Boolean.parseBoolean(value.toString())||value.equals("1");
//				}
//				if(value instanceof Boolean) {
//					return (boolean)value;
//				}
//			}catch(Exception e) {
//				return false;
//			}
//			return false;
//		} else {
//			return value;
//		}
//	}
//
//	public Object formatterStringValue(String value) {
//		if(fieldType.isAssignableFrom(Integer.class)||fieldType.isAssignableFrom(int.class)) {
//			return ConverterUtils.stringToInt(value);
//		}else if(fieldType.isAssignableFrom(Long.class)||fieldType.isAssignableFrom(long.class)) {
//			return ConverterUtils.stringToLong(value);
//		}else if(fieldType.isAssignableFrom(Float.class)||fieldType.isAssignableFrom(float.class)) {
//			return ConverterUtils.stringToFloat(value);
//		}else if(fieldType.isAssignableFrom(Double.class)||fieldType.isAssignableFrom(double.class)) {
//			return ConverterUtils.stringToDouble(value);
//		}else if(fieldType.isAssignableFrom(Short.class)||fieldType.isAssignableFrom(short.class)) {
//			return ConverterUtils.stringToDouble(value);
//		}else if(fieldType.isAssignableFrom(BigDecimal.class)) {
//			return ConverterUtils.stringToDecimal(value);
//		}else if(fieldType.isAssignableFrom(Date.class)) {
//			return ConverterUtils.objectToDate(value,"yyyy-MM-dd");
//		}else if(fieldType.isAssignableFrom(Timestamp.class)) {
//			return ConverterUtils.objectToTimestamp(value,"yyyy-MM-dd HH:mm:ss");
//		}else if(fieldType.isAssignableFrom(char.class)) {
//			return ConverterUtils.stringToChar(value);
//		}else if(fieldType.isAssignableFrom(byte.class)) {
//			return ConverterUtils.stringToByte(value);
//		}else {
//			return value;
//		}
//	}

	
}
