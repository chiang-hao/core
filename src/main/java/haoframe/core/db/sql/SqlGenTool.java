package haoframe.core.db.sql;

import java.util.ArrayList;
import java.util.List;

import haoframe.core.db.model.Column;
import haoframe.core.db.model.Table;
import haoframe.core.exception.ErrorInfo;
import haoframe.core.exception.HaoException;
import haoframe.core.utils.ClassUtils;

public class SqlGenTool {

	/**
	 * 生成查询语句
	 * @param table
	 * @return
	 */
	public static Sql getSelectSql(Table table) {
		if(table==null) {
			return null;
		}
		StringBuffer sb = new StringBuffer("select ");
		int index=0;
		for(String fieldName:table.getColumnMap().keySet()) {
			Column c = table.getColumnInfo(fieldName);
			if(index==0) {
				sb.append(" `"+c.getName()+"`");
			}else {
				sb.append(" ,`"+c.getName()+"`");
			}
			index++;
		}
		sb.append(" from `"+table.getTableName()+"` ");
		return new Sql(sb.toString(),null);
	}

	public static <T> Sql getSelectSql(Table table, T bean) {
		if(table==null) {
			return null;
		}
		StringBuffer sb = new StringBuffer("select ");
		int index=0;
		for(String fieldName:table.getColumnMap().keySet()) {
			Column c = table.getColumnInfo(fieldName);
			if(index==0) {
				sb.append(" `"+c.getName()+"`");
			}else {
				sb.append(" ,`"+c.getName()+"`");
			}
			index++;
		}
		sb.append(" from `"+table.getTableName()+"` ");
		
		List<Object> args = new ArrayList<Object>();
		if(bean!=null) {
			StringBuffer where = new StringBuffer();
			index=0;
			for(String fieldName:table.getColumnMap().keySet()) {
				Object value = ClassUtils.getFieldValue(bean, fieldName); 
				if(value!=null) {
					Column c = table.getColumnInfo(fieldName);
					if(index==0) {
						where.append(" `"+c.getName()+"`=?");
					}else {
						where.append(" and `"+c.getName()+"`=?");
					}
					args.add(value);
					index++;
				}
			}
			if(index>0) {
				sb.append(" where "+where.toString());
			}
		}	
		return new Sql(sb.toString(),args);
	}

	/**
	 * 获取根据主键删除的语句
	 * @param table
	 * @param code
	 * @return
	 */
	public static Sql getDelByCodeSql(Table table, Object code) {
		if(!table.hasColumn("code")) {
			throw new HaoException(ErrorInfo.build_sql_error,"数据库中指定的CODE不存在");
		}
		String sql = "delete from `"+table.getTableName()+"` where `code`=?";
		List<Object> args = new ArrayList<Object>();
		args.add(code);
		return new Sql(sql,args);
	}

	public static Sql getSelectByCodeSql(Table table, Object code) {
		if(!table.hasColumn("code")) {
			throw new HaoException(ErrorInfo.build_sql_error,"数据库中指定的CODE不存在");
		}
		String sql = "select * from `"+table.getTableName()+"` where `code`=?";
		List<Object> args = new ArrayList<Object>();
		args.add(code);
		return new Sql(sql,args);
	}

	
	
	public static <T> Sql getInsterBatchSql(Table table, List<T> list) {
		StringBuffer sb = new StringBuffer();
		StringBuffer fields = new StringBuffer();
		List<String> fieldNames  = new ArrayList<String>();
		StringBuffer values = new StringBuffer("(");
		int index=0;
		for(String fieldName:table.getColumnMap().keySet()) {
			Column c = table.getColumnInfo(fieldName);
			if(index==0) {
				fields.append("`"+c.getName()+"`");
				values.append("?");
			}else {
				fields.append(",`"+c.getName()+"`");
				values.append(",?");
			}
			fieldNames.add(fieldName);
			index++;
		}
		values.append(")");
		sb.append("insert into `"+table.getTableName()+"`("+fields+")values");
		
		index=0;
		List<Object> args = new ArrayList<Object>();
		for(T bean :list) {
			
			if(index==(list.size()-1)) {
				sb.append(values);
			}else {
				sb.append(values+",");
			}
			for(String fieldName:fieldNames) {
				Object value = ClassUtils.getFieldValue(bean, fieldName);
				args.add(value);
			}
			index++;
		}
		return new Sql(sb.toString(),args);
	}
	
	
	
	/**
	 * 创建插入语句
	 * @param table
	 * @param bean
	 * @return
	 */
	public static <T> Sql getInsterSql(Table table, T bean) {
		if(bean!=null) {
			StringBuffer fields = new StringBuffer();
			StringBuffer values = new StringBuffer();
			List<Object> args = new ArrayList<Object>();
			int index=0;
			for(String fieldName:table.getColumnMap().keySet()) {
				Object value = ClassUtils.getFieldValue(bean, fieldName); 
				if(value!=null) {
					Column c = table.getColumnInfo(fieldName);
					if(index==0) {
						fields.append("`"+c.getName()+"`");
						values.append("?");
						args.add(value);
					}else {
						fields.append(",`"+c.getName()+"`");
						values.append(",?");
						args.add(value);
					}
					index++;
				}
			}
			if(index>0) {
				String sql = "insert into `"+table.getTableName()+"`("+fields+")values("+values+")";
				return new Sql(sql,args);
			}
		}	
		return null;
	}

	public static <T> Sql getDeleteSql(Table table, T where) {
		StringBuffer sb = new StringBuffer();
		sb.append("delete from `"+table.getTableName()+"` ");
		List<Object> args = new ArrayList<Object>();
		if(where!=null) {
			StringBuffer whereString = new StringBuffer();
			int index= 0;
			for(String fieldName:table.getColumnMap().keySet()) {
				Object value = ClassUtils.getFieldValue(where, fieldName); 
				if(value!=null) {
					Column c = table.getColumnInfo(fieldName);
					if(index==0) {
						whereString.append("`"+c.getName()+"`=?");
					}else {
						whereString.append(" and `"+c.getName()+"`=?");
					}
					args.add(value);
					index++;
				}
			}
			if(index>0) {
				sb.append(" where "+whereString);
			}
		}
		return new Sql(sb.toString(),args);
	}

	public static <T> Sql getUpdateSql(Table table, T bean,T where) {
		StringBuffer sb = new StringBuffer();
		sb.append("update `"+table.getTableName()+"` ");
		int fieldSize = 0;
		List<Object> args = new ArrayList<Object>();
		if(bean!=null) {
			StringBuffer fieldString = new StringBuffer();
			int index= 0;
			for(String fieldName:table.getColumnMap().keySet()) {
				Object value = ClassUtils.getFieldValue(bean, fieldName); 
				if(value!=null) {
					Column c = table.getColumnInfo(fieldName);
					if(index==0) {
						fieldString.append("set `"+c.getName()+"` =?");
					}else {
						fieldString.append(",`"+c.getName()+"`=?");
					}
					args.add(value);
					index++;
					fieldSize++;
				}
			}
			if(fieldSize==0) {
				return null;
			}
			sb.append(fieldString);
		}
		if(where!=null) {
			StringBuffer whereString = new StringBuffer();
			int index= 0;
			for(String fieldName:table.getColumnMap().keySet()) {
				Object value = ClassUtils.getFieldValue(where, fieldName); 
				if(value!=null) {
					Column c = table.getColumnInfo(fieldName);
					if(index==0) {
						whereString.append("`"+c.getName()+"`=?");
					}else {
						whereString.append(" and `"+c.getName()+"`=?");
					}
					args.add(value);
					index++;
				}
			}
			if(index>0) {
				sb.append(" where "+whereString);
			}
		}
		return new Sql(sb.toString(),args);
	}

	public static <T> Sql getUpdateByCodeSql(Table table, T bean,Object code) {
		if(!table.hasColumn("code")) {
			throw new HaoException(ErrorInfo.build_sql_error,"数据库中指定的CODE不存在");
		}
		StringBuffer sb = new StringBuffer();
		sb.append("update `"+table.getTableName()+"` ");
		int fieldSize = 0;
		List<Object> args = new ArrayList<Object>();
		if(bean!=null) {
			StringBuffer fieldString = new StringBuffer();
			int index= 0;
			for(String fieldName:table.getColumnMap().keySet()) {
				Object value = ClassUtils.getFieldValue(bean, fieldName); 
				if(value!=null) {
					Column c = table.getColumnInfo(fieldName);
					if(index==0) {
						fieldString.append("set `"+c.getName()+"`=?");
					}else {
						fieldString.append(",`"+c.getName()+"`=?");
					}
					args.add(value);
					index++;
					fieldSize++;
				}
			}
			if(fieldSize==0) {
				return null;
			}
			sb.append(fieldString);
		}
		sb.append(" where `code`=?");
		args.add(code);
		return new Sql(sb.toString(),args);
	}

	public static Sql SqlWhereToSql(List<SqlWhere> wheres) {
		if(wheres==null||wheres.isEmpty()) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		List<Object> args = new ArrayList<Object>();
		for(SqlWhere where:wheres) {
			Sql sql = where.getSql();
			sb.append(sql.getSql());
			args.addAll(sql.getArgs());
		}
		
		String sql = sb.toString().trim();
		if(sql.toUpperCase().endsWith("AND")) {
			sql  = sql.substring(0,sql.length()-3);
		}
		if(sql.toUpperCase().endsWith("OR")) {
			sql  = sql.substring(0,sql.length()-2);
		}
		
		return new Sql(sql,args);
	}

}
