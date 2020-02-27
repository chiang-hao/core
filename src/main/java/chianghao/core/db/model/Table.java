package chianghao.core.db.model;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import chianghao.core.db.annotation.Entity;
import chianghao.core.utils.ClassUtils;
import chianghao.core.utils.StringUtils;



/***
 * 类的对象信息
 * @author chianghao
 *
 */
public class Table  {

	private static Map<String,Table> tableMap = new ConcurrentHashMap<String,Table>();
	
	public static Table getTable(String classname) {
		if(tableMap.containsKey(classname)) {
			return null;
		}
		return tableMap.get(classname); 
	}
	
	public static Map<String,Table> getTableMap() {
		return tableMap;
	}
	
	public static Table getTable(Class<?> clazz) {
		if(!tableMap.containsKey(clazz.getName())) {
			return null;
		}
		return tableMap.get(clazz.getName()); 
	}
	
	public static void putTable(Table entityInfo) {
		tableMap.put(entityInfo.getClazz().getName(), entityInfo);
	}
	
	private Class<?> clazz;
	private String   tableName;
	private Map<String,Column> columnMap;
	private Map<String,Field>  fieldMap;
 	
	public Table() {
		
	}
	
	public Table(Class<?> clazz) {
		this.clazz = clazz;
		String simpleClassName = this.clazz.getSimpleName();
	    this.tableName = StringUtils.camelToUnderline(simpleClassName);
	    Entity entity = clazz.getAnnotation(Entity.class);
	    if(entity.table()!=null&&!"".equals(entity.table())) {
	    	this.tableName = entity.table();
	    }
	    columnMap = new HashMap<String,Column>();
	    fieldMap = new HashMap<String,Field>();
		ClassUtils.getFields(this.clazz, fieldMap);
		for(String key:fieldMap.keySet()) {
			if(key.equals("serialVersionUID")) {
				//序列化的属性无需生成
				continue;
			}
			Column column = Column.createProperty(this.clazz,fieldMap.get(key));
			if(column!=null) {
				columnMap.put(key, column);
			}
		}
	}
	
	public Class<?> getClazz() {
		return clazz;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	public Map<String, Column> getColumnMap() {
		return columnMap;
	}

	public void setColumnMap(Map<String, Column> columnMap) {
		this.columnMap = columnMap;
	}

	public Map<String, Field> getFieldMap() {
		return fieldMap;
	}

	public void setFieldMap(Map<String, Field> fieldMap) {
		this.fieldMap = fieldMap;
	}

	/**
	 * 获取对象名称
	 * @return
	 */
	public String getObjectName() {
		return clazz.getSimpleName();
	}
	
	/**
	 * 获取默认别名
	 * @return
	 */
	public String getDefaultAlias() {
		return StringUtils.firstCharLowercase(clazz.getSimpleName());
	}
	
	
	
	
	/***
	 * 获取property
	 * @param field
	 * @return
	 */
	public Column getColumnInfo(String fieldName) {
		return columnMap.get(fieldName);
	}

	public Set<String> getPrimaryKeys() {
		if(this.columnMap==null||this.columnMap.size()==0) {
			return new HashSet<String>();
		}
		Set<String> sets = new HashSet<String>();
		for(String key:this.columnMap.keySet()) {
			Column column = columnMap.get(key);
			if(column.isPrimary()) {
				sets.add(column.getName());
			}
		}
		return sets;
	}

	/**
	 *  判断是否含有这个字段
	 * @param fieldName
	 * @return
	 */
	public boolean hasColumn(String fieldName) {
		if(this.columnMap.containsKey(StringUtils.camelToUnderline(fieldName))) {
			return true;
		}
		return false;
	}

	public String getColumnName(String fieldName) {
		if(this.columnMap.containsKey(fieldName)) {
			return columnMap.get(fieldName).getName();
		}
		return null;
	}
	 
}
