package haoframe.core.mybatis.method;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.builder.SqlSourceBuilder;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.xmltags.DynamicContext;
import org.apache.ibatis.session.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import haoframe.core.db.model.Column;
import haoframe.core.db.model.Table;
import haoframe.core.mybatis.sql.SqlCondition;
import haoframe.core.mybatis.sql.SqlOrder;
import haoframe.core.mybatis.sql.SqlWrapper;
import haoframe.core.mybatis.sql.db_enum.SqlConnector;
import haoframe.core.utils.ClassUtils;

public class MybatisSqlSource implements SqlSource{

	Logger logger = LoggerFactory.getLogger(MybatisSqlSource.class);
	
	private Table table;
	private Class<?> mapperClass;
	private BaseMapperMethodDefinition method;
	private final Configuration configuration;
	
	public MybatisSqlSource(Table table,Class<?> mapperClass,BaseMapperMethodDefinition method,Configuration configuration) {
		this.table = table;
		this.mapperClass = mapperClass;
		this.method = method;
		this.configuration = configuration;
	}
	
	public Table getTable() {
		return table;
	}

	public void setTable(Table table) {
		this.table = table;
	}
	
	public Class<?> getMapperClass() {
		return mapperClass;
	}
	
	public void setMapperClass(Class<?> mapperClass) {
		this.mapperClass = mapperClass;
	}

	public BaseMapperMethodDefinition getMethod() {
		return method;
	}

	public void setMethod(BaseMapperMethodDefinition method) {
		this.method = method;
	}

	@Override
	public BoundSql getBoundSql(Object parameterObject) {
		String sql="";
		switch(method) {
		case insert:
			sql = getInsertSql(parameterObject);
			break;
		case batchInsert:
			sql = getBatchInsertSql(parameterObject);
			break;
		case delete:
			sql = getDeleteSql((SqlWrapper) parameterObject);
			break;
		case update:
			sql = getUpdateSql(parameterObject);
			break;
		case queryOne:
			sql = getQueryBeanSql((SqlWrapper) parameterObject);
			break;
		case queryPageList:
			sql = getQueryPageListSql(parameterObject);
			break;
		case queryList:
			sql = getQueryListSql((SqlWrapper) parameterObject);
			break;
		case queryObject:
			sql = getQueryObjectSql((SqlWrapper) parameterObject);
			break;
		case updateByEntity:	
			sql=getUpdateByEntitySql(parameterObject);
			break;
		case deleteByEntity:	
			sql=getDeleteByEntitySql(parameterObject);
			break;
		case queryOneByEntity:	
			sql=getQueryOneByEntitySql(parameterObject);
			break;
		case queryListByEntity:	
			sql=getQueryListByEntitySql(parameterObject);
			break;
		case queryPageListByEntity:	
			sql=getQueryPageListByEntitySql(parameterObject);
			break;
		case queryObjectByEntity:	
			sql=getQueryObjectByEntitySql(parameterObject);
			break;
		case deleteByCode:	
			sql=getDeleteByCodeSql(parameterObject);
			break;
		case updateByCode:	
			sql=getUpdateByCodeSql(parameterObject);
			break;
		case queryOneByCode:	
			sql=getQueryOneByCodeSql(parameterObject);
			break;
		default:
			break;
		}
		logger.info("动态生成的原MyBatis Sql  ===>{}",sql);
	    DynamicContext context = new DynamicContext(configuration, parameterObject);
	    SqlSourceBuilder sqlSourceParser = new SqlSourceBuilder(configuration);
	    Class<?> parameterType = parameterObject == null ? Object.class : parameterObject.getClass();
	    
	    SqlSource sqlSource = sqlSourceParser.parse(sql, parameterType, context.getBindings());
	    BoundSql boundSql = sqlSource.getBoundSql(parameterObject);
	    // 对BoundSql对象保存额外的bind节点类型
	    for (Map.Entry<String, Object> entry : context.getBindings().entrySet()) {
	      boundSql.setAdditionalParameter(entry.getKey(), entry.getValue());
	    }
	    return boundSql;
	}
	
	
	//register begin 生成sql的工具方法
	String getOrderSql(SqlOrder sqlOrder) {
		StringBuffer sb = new StringBuffer();
		if(sqlOrder!=null&&sqlOrder.getOrderby().size()>0) {
			int i=0;
			for(String[] item:sqlOrder.getOrderby()) {
				String fieldName = item[0];
				String columnName = table.getColumnName(fieldName);
				if(StringUtils.isNotEmpty(columnName)) {
					fieldName = "`"+columnName+"`";
				}
				if(i==0) {
					sb.append(fieldName+" "+item[1]);
				}else {
					sb.append(","+fieldName+" "+item[1]);
				}
				i++;
			}
		}
		return sb.toString();
	}
	
	private String getQuerySqlByEntity(Object where,SqlOrder order ) {
		StringBuffer sb = new StringBuffer();
		sb.append(getQuerySqlByEntity("where",where));
		String orderSql = getOrderSql(order);
		if(StringUtils.isNotEmpty(orderSql)) {
			sb.append(" order by "+orderSql);
		}
		return sb.toString();
	}
	
	private String getQuerySqlByEntity(String prefix,Object where) {
		StringBuffer sb = new StringBuffer();
		sb.append("select "+getSelectFiled()+" from `"+this.table.getTableName()+"` ");
		String whereSql = getWhereSqlByEntity(prefix,where);
		if(StringUtils.isNotEmpty(whereSql)) {
			sb.append(" where "+whereSql);
		}
		return sb.toString();
	}
	
	private String getWhereSqlByEntity(String prefix,Object bean) {
		if(StringUtils.isNotEmpty(prefix)) {
			prefix = prefix+".";
		}
		StringBuffer where = new StringBuffer();
		if(bean!=null) {
			int index=0;
			for(String fieldName:table.getColumnMap().keySet()) {
				Object value = ClassUtils.getFieldValue(bean, fieldName); 
				if(value!=null) {
					Column c = table.getColumnInfo(fieldName);
					if(index==0) {
						where.append(" `"+c.getName()+"`=#{"+prefix+fieldName+"}");
					}else {
						where.append(" and `"+c.getName()+"`=#{"+prefix+fieldName+"}");
					}
					index++;
				}
			}
		}
		return where.toString();
	}
	
	private String getSelectFiled() {
		StringBuffer sb = new StringBuffer();
		int i=0;
		for(Map.Entry<String, Column> entiry:table.getColumnMap().entrySet()) {
			if(i==0) {
				sb.append("`"+entiry.getValue().getName() +"` as `"+entiry.getKey()+"`" );
			}else {
				sb.append(",`"+entiry.getValue().getName() +"` as `"+entiry.getKey()+"`" );
			}
			i++;
		}
		return sb.toString();
	}
	
	public String sqlWrapperToSqlAsMysql(String prefix,SqlWrapper sqlWrapper) {
		List<SqlCondition> sqlWhereList = sqlWrapper.getSqlWhereList();
		if(sqlWhereList==null||sqlWhereList.isEmpty()) {
			return "";
		}
		if(StringUtils.isNotEmpty(prefix)) {
			prefix=prefix+".params";
		}else {
			prefix="params";
		}
		StringBuffer sb = new StringBuffer();
		int count=0;
		for(int i=0;i<sqlWhereList.size();i++) {
			SqlCondition sqlCondition = sqlWhereList.get(i);
			if(count==0&&!sqlCondition.isCondition()) {
				continue;
			}
			String sql = sqlWhereToSqlAsMysql(prefix,sqlCondition);
			sb.append(sql);
			SqlCondition nextSqlWhere =null;
			if((i+1)==(sqlWhereList.size()-1)) {
				nextSqlWhere = sqlWhereList.get((i+1));
			}
			
			//当前和下一个都是where条件的添加默认的and
			if(sqlCondition.isCondition()&&nextSqlWhere!=null&&nextSqlWhere.isCondition()) {
				sb.append(" "+SqlConnector.and.getConnector()+" ");
			}
			count++;
		}
		//将无效的后缀截取掉
		String sql = sb.toString().trim();
		if(sql.toUpperCase().endsWith(SqlConnector.and.getConnector())) {
			sql  = sql.substring(0,sql.length()-SqlConnector.and.getConnector().length());
		}
		if(sql.toUpperCase().endsWith(SqlConnector.or.getConnector())) {
			sql  = sql.substring(0,sql.length()-SqlConnector.or.getConnector().length());
		}
		if(sql.toUpperCase().endsWith(SqlConnector.orStart.getConnector())) {
			sql  = sql.substring(0,sql.length()-SqlConnector.orStart.getConnector().length());
		}
		if(sql.toUpperCase().endsWith(SqlConnector.andStart.getConnector())) {
			sql  = sql.substring(0,sql.length()-SqlConnector.andStart.getConnector().length());
		}
		return sql;
	}
	
	
	public String sqlWhereToSqlAsMysql(String prefix,SqlCondition sqlCondition) {
		StringBuffer sb = new StringBuffer();
		String fieldName = sqlCondition.getFieldName();
		sb.append(" `"+this.table.getColumnName(fieldName)+"`");
		if(StringUtils.isNotBlank(prefix)) {
			prefix =prefix+".";
		}
		switch (sqlCondition.getOperators()) {
		case in:
			sb.append(" in( ");
			Object[] values1=(Object[]) sqlCondition.getValue();
			for (int j = 0; j < values1.length; j++) {
				if (j == 0) {
					sb.append("#{"+prefix+fieldName+"[0]}");
				} else {
					sb.append(",#{"+prefix+fieldName+"[1]}");
				}
			}
			sb.append(") ");
			break;
		case not_in:
			sb.append(" not in( ");
			Object[] values2=(Object[]) sqlCondition.getValue();
			for (int j = 0; j < values2.length; j++) {
				if (j == 0) {
					sb.append("#{"+prefix+fieldName+"[0]}");
				} else {
					sb.append(",#{"+prefix+fieldName+"[1]}");
				}
			}
			sb.append(") ");
			break;
		case between:
			sb.append(" between #{"+prefix+fieldName+"[0]} and #{params."+fieldName+"[1]} ");
			break;
		case not_between:
			sb.append(" not between #{"+prefix+fieldName+"[0]} and #{params."+fieldName+"[1]} ");
			break;
		case like:
			sb.append(" like concat('%',#{"+prefix+fieldName+"},'%') ");
			break;
		case front_like:
			sb.append(" like concat('%',#{"+prefix+fieldName+"}) ");
			break;
		case behind_like:
			sb.append(" like concat(#{"+prefix+fieldName+"},'%') ");
			break;
		case equals:
			sb.append(" = #{"+prefix+fieldName+"} ");
			break;
		case not_equals:
			sb.append(" != #{"+prefix+fieldName+"} ");
			break;
		case greater:
			sb.append(" > #{"+prefix+fieldName+"} ");
			break;
		case greater_equals:
			sb.append(" >= #{"+prefix+fieldName+"} ");
			break;
		case less:
			sb.append(" < #{"+prefix+fieldName+"} ");
			break;
		case less_equals:
			sb.append(" <= #{"+prefix+fieldName+"} ");
			break;
		default:
			logger.error("未找到对应的逻辑操作条件,你设置的sql信息如下{}", this);
			break;
		}
		return sb.toString();
	}
	
	private String getUpdateField(String prefix,Object bean) {
		StringBuffer fieldString = new StringBuffer();
		if(StringUtils.isNotEmpty(prefix)) {
			prefix = prefix+".";
		}
		if(bean!=null) {
			int index= 0;
			for(String fieldName:table.getColumnMap().keySet()) {
				Object value = ClassUtils.getFieldValue(bean, fieldName); 
				if(value!=null) {
					Column c = table.getColumnInfo(fieldName);
					if(index==0) {
						fieldString.append(" `"+c.getName()+"` = #{"+prefix+fieldName+"}");
					}else {
						fieldString.append(",`"+c.getName()+"`= #{"+prefix+fieldName+"}");
					}
					index++;
				}
			}
		}
		return fieldString.toString();
	}
	
	
	private String getSelectOneFiled(String fieldName) {
		String columnName  = table.getColumnName(fieldName);
		if(StringUtils.isNotEmpty(columnName)) {
			fieldName = "`"+columnName+'`';
		}
		return fieldName;
	}
	//register end 生成sql的工具方法
	
	
	private String getQueryOneByCodeSql(Object parameterObject) {
		if(!table.hasColumn("code")) {
			logger.error("表"+this.table.getTableName()+"中code字段不存在");
			return "";
		}
		StringBuffer sb = new StringBuffer();
		sb.append("select "+getSelectFiled()+" from `"+this.table.getTableName()+"` where `code`=#{code} ");
		return sb.toString();
	}
	
	@SuppressWarnings("unchecked")
	private String getUpdateByCodeSql(Object parameterObject) {
		if(!table.hasColumn("code")) {
			logger.error("表"+this.table.getTableName()+"中code字段不存在");
			return "";
		}
		Map<String,Object> params = (Map<String, Object>) parameterObject;
		Object bean = params.get("param1");
		String fieldString  = getUpdateField("bean",bean);
		if(StringUtils.isEmpty(fieldString)) {
			logger.error("更新"+this.table.getTableName()+"表时没有可更新的字段");
			return "";
		}
		StringBuffer sb = new StringBuffer();
		sb.append("update `"+table.getTableName()+"` set "+fieldString+" where `code`=#{code}");
		return sb.toString();
	}
	
	private String getDeleteByCodeSql(Object parameterObject) {
		if(!table.hasColumn("code")) {
			logger.error("表"+this.table.getTableName()+"中code字段不存在");
			return "";
		}
		String sql = "delete from `"+table.getTableName()+"` where `code`=#{code}";
		return sql;
	}
	
	@SuppressWarnings("unchecked")
	private String getQueryObjectByEntitySql(Object parameterObject) {
		Map<String,Object> params = (Map<String, Object>) parameterObject;
		String fieldName = (String) params.get("param1");
		if(StringUtils.isEmpty(fieldName)) {
			logger.error("查询"+this.table.getTableName()+"表时，select #字段 from。 #字段为空 ");
			return "";
		}
		Object where = params.get("param2");
		StringBuffer sb = new StringBuffer();
		
		sb.append("select "+getSelectOneFiled(fieldName)+" from `"+this.table.getTableName()+"` ");
		String whereSql = getWhereSqlByEntity("where",where);
		if(StringUtils.isNotEmpty(whereSql)) {
			sb.append(" where "+whereSql);
		}
		return sb.toString();
	}
	
	@SuppressWarnings("unchecked")
	private String getQueryPageListByEntitySql(Object parameterObject) {
		Map<String,Object> params = (Map<String, Object>) parameterObject;
		Object where = params.get("param2");
		SqlOrder order = (SqlOrder) params.get("param3");
		return getQuerySqlByEntity(where,order);
	}
	
	@SuppressWarnings("unchecked")
	private String getQueryListByEntitySql(Object parameterObject) {
		Map<String,Object> params = (Map<String, Object>) parameterObject;
		Object where = params.get("param1");
		SqlOrder order = (SqlOrder) params.get("param2");
		return getQuerySqlByEntity(where,order);
	}
	
	private String getQueryOneByEntitySql(Object parameterObject) {
		StringBuffer sb = new StringBuffer();
		sb.append("select "+getSelectFiled()+" from `"+this.table.getTableName()+"` ");
		String whereSql = getWhereSqlByEntity("",parameterObject);
		if(StringUtils.isNotEmpty(whereSql)) {
			sb.append(" where "+whereSql);
		}
		return sb.toString();
	}
	
	private String getDeleteByEntitySql(Object where) {
		StringBuffer sb = new StringBuffer();
		sb.append("delete from `"+table.getTableName()+"` ");
		String whereSql = getWhereSqlByEntity("",where);
		if(StringUtils.isNotEmpty(whereSql)) {
			sb.append(" where "+whereSql);
		}
		return sb.toString();
	}
	
	@SuppressWarnings("unchecked")
	private String getUpdateByEntitySql(Object parameterObject) {
		Map<String,Object> params = (Map<String, Object>) parameterObject;
		Object bean = params.get("param1");
		Object where = params.get("param2");
		String fieldString  = getUpdateField("bean",bean);
		if(StringUtils.isEmpty(fieldString)) {
			logger.error("更新"+this.table.getTableName()+"表时没有可更新的字段");
			return "";
		}
		StringBuffer sb = new StringBuffer();
		sb.append("update `"+table.getTableName()+"` set "+fieldString);
		String whereSql = getWhereSqlByEntity("where",where);
		if(StringUtils.isNotEmpty(whereSql)) {
			sb.append(" where "+whereSql);
		}
		return sb.toString();
	}
	
	private String getQueryObjectSql(SqlWrapper conditions) {
		String[] fieldNames = conditions.getFieldNames();
		if(fieldNames==null||fieldNames.length==0) {
			logger.error("查询"+this.table.getTableName()+"表时，select #字段 from。 #字段为空 ");
			return "";
		}
		StringBuffer sb = new StringBuffer();
		String fieldName = fieldNames[0];
		sb.append("select "+getSelectOneFiled(fieldName)+" from `"+this.table.getTableName()+"` ");
		String whereSql = sqlWrapperToSqlAsMysql("",conditions);
		if(StringUtils.isNotEmpty(whereSql)) {
			sb.append(" where "+whereSql);
		}
		return sb.toString();
	}
	
	@SuppressWarnings("unchecked")
	private String getQueryPageListSql(Object parameterObject) {
		Map<String,Object> params = (Map<String, Object>) parameterObject;
		SqlWrapper sqlWrapper = (SqlWrapper) params.get("param2");
		StringBuffer sb = new StringBuffer();
		sb.append("select "+getSelectFiled()+" from `"+this.table.getTableName()+"` ");
		String sql = sqlWrapperToSqlAsMysql("sqlWrapper",sqlWrapper);
		if(StringUtils.isNotEmpty(sql)) {
			sb.append(" where "+sql);
		}
		String order = this.getOrderSql(sqlWrapper.getSqlOrder());
		if(StringUtils.isNotEmpty(order)) {
			sb.append(" order by "+order);
		}
		return sb.toString();
	}
	
	private String getQueryBeanSql(SqlWrapper conditions) {
		StringBuffer sb = new StringBuffer();
		sb.append("select "+getSelectFiled()+" from `"+this.table.getTableName()+"` ");
		String whereSql = this.sqlWrapperToSqlAsMysql("",conditions);
		if(StringUtils.isNotEmpty(whereSql)) {
			sb.append(" where "+whereSql);
		}
		return sb.toString();
	}
	
	@SuppressWarnings("unchecked")
	private String getUpdateSql(Object parameterObject) {
		Map<String,Object> params = (Map<String, Object>) parameterObject;
		Object bean = params.get("param1");
		
		String fieldString  = getUpdateField("bean",bean);
		if(StringUtils.isEmpty(fieldString)) {
			logger.error("更新"+this.table.getTableName()+"表时没有可更新的字段");
			return "";
		}
		StringBuffer sb = new StringBuffer("update `"+table.getTableName()+"` set "+fieldString);
		SqlWrapper sqlWrapper = (SqlWrapper) params.get("param2");
		String whereSql = this.sqlWrapperToSqlAsMysql("sqlWrapper", sqlWrapper);
		if(StringUtils.isNotBlank(whereSql)) {
			sb.append(" where "+whereSql);
		}
		return sb.toString();
	} 
	
	
	private String getQueryListSql(SqlWrapper sqlWrapper) {
		StringBuffer sb = new StringBuffer();
		sb.append("select "+getSelectFiled()+" from `"+this.table.getTableName()+"` ");
		String sql = sqlWrapperToSqlAsMysql("",sqlWrapper);
		if(StringUtils.isNotEmpty(sql)) {
			sb.append(" where "+sql);
		}
		String order = this.getOrderSql(sqlWrapper.getSqlOrder());
		if(StringUtils.isNotEmpty(order)) {
			sb.append(" order by "+order);
		}
		return sb.toString();
	}

	private String getDeleteSql(SqlWrapper condition) {
		StringBuffer sb = new StringBuffer();
		sb.append("delete from `"+table.getTableName()+"` ");
		String whereSql = sqlWrapperToSqlAsMysql("", condition);
		if(StringUtils.isNotEmpty(whereSql)) {
			sb.append(" where "+whereSql);
		}
		return sb.toString();
	}
	
	private String getInsertSql(Object bean) {
		if(bean==null) {
			logger.error("insert"+this.table.getTableName()+"表时，entity为空 ");
			return "";
		}
		StringBuffer sb = new StringBuffer();
		StringBuffer fields = new StringBuffer();
		StringBuffer values = new StringBuffer();
		int index=0;
		for(String fieldName:table.getColumnMap().keySet()) {
			Object value = ClassUtils.getFieldValue(bean, fieldName); 
			if(value!=null) {
				Column c = table.getColumnInfo(fieldName);
				if(index==0) {
					fields.append("`"+c.getName()+"`");
					values.append("#{"+fieldName+"}");
				}else {
					fields.append(",`"+c.getName()+"`");
					values.append(",#{"+fieldName+"}");
				}
				index++;
			}
		}
		if(index>0) {
			sb.append("insert into `"+table.getTableName()+"`("+fields+")values("+values+")");
		}
		return sb.toString();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private String getBatchInsertSql(Object listObject) {
		Map<String,Object> map = (Map<String, Object>) listObject;
		
		List list   =  (List) map.get("param1");
		StringBuffer sb = new StringBuffer();
		StringBuffer fields = new StringBuffer();
		List<String> fieldNames  = new ArrayList<String>();
		int index=0;
		for(String fieldName:table.getColumnMap().keySet()) {
			Column c = table.getColumnInfo(fieldName);
			if(index==0) {
				fields.append("`"+c.getName()+"`");
			}else {
				fields.append(",`"+c.getName()+"`");
			}
			fieldNames.add(fieldName);
			index++;
		}
		sb.append("insert into `"+table.getTableName()+"`("+fields+")values");
		index=0;
		for(;index<list.size();index++) {
			StringBuffer valueSb = new StringBuffer("("); 
			for(int i=0;i<fieldNames.size();i++) {
				if(i==0) {
					valueSb.append("#{beanes["+index+"]."+fieldNames.get(i)+"}");
				}else {
					valueSb.append(",#{beanes["+index+"]."+fieldNames.get(i)+"}");
				}
			}
			if(index==(list.size()-1)) {
				valueSb.append(");");
			}else {
				valueSb.append("),");
			}
			sb.append(valueSb);
		}
		return sb.toString();
	}
	
	
}
