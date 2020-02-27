package haoframe.core.db.manage;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import haoframe.core.db.manage.impl.MysqlDataMeta;
import haoframe.core.db.model.Column;
import haoframe.core.db.model.ColumnType;
import haoframe.core.db.model.Table;
import haoframe.core.exception.HaoException;
import haoframe.core.frame.spring.SpringContext;
import haoframe.core.utils.ClassUtils;
import haoframe.core.utils.StringUtils;



/***
 * 
 * 
 * catalog - 类别名称；它必须与存储在数据库中的类别名称匹配；该参数为 "" 表示获取没有类别的那些描述；为 null
 * 则表示该类别名称不应该用于缩小搜索范围 
 * schemaPattern - 模式名称的模式；它必须与存储在数据库中的模式名称匹配；该参数为 ""
 * 表示获取没有模式的那些描述；为 null 则表示该模式名称不应该用于缩小搜索范围 
 * tableNamePattern -
 * 表名称模式；它必须与存储在数据库中的表名称匹配 
 * types - 要包括的表类型所组成的列表，必须取自从 getTableTypes()
 * 返回的表类型列表；null 表示返回所有类型
 * 
 * @author Administrator
 *
 */

public abstract class AbstractDataMeta implements DataMeta{
	
	private String dataSourceName;
	
	public String getDataSourceName() {
		return dataSourceName;
	}

	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}

	Logger log = LoggerFactory.getLogger("DefaultDataMeta");
	
	public Connection getConection() {
		DataSource dataSource = (DataSource) SpringContext.getBean(this.dataSourceName);
		try {
			Connection con = dataSource.getConnection();
			return con;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 批量执行
	 * @param sqlCommands
	 * @throws HaoException
	 */
	public void executeBatch(List<String> sqlCommands) throws HaoException{
		if(sqlCommands==null||sqlCommands.size()==0){
			return;
		}
		Connection conn = this.getConection();
		try{
			for(String sql:sqlCommands){
				sql = sql.trim();
				if(sql.endsWith(";")) {
					sql = sql.substring(0,sql.lastIndexOf(";"));
				}
				log.info("执行命令："+sql);
				Statement st  = conn.createStatement();
				st.execute(sql);
				if(st!=null){
					try {
						st.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			try {
				if (conn!=null&&!conn.isClosed()) {   
					 conn.rollback();   
				    conn.setAutoCommit(true);   
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}  
			throw new HaoException("DB_ERROR", e.getMessage());
		}finally {
			if(conn!=null){
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	
	/**
	 * 查询sql获取list<map>对象
	 * @param sql
	 * @return
	 */
	public List<Map<String,Object>> queryList(String sql){
		Connection conn = this.getConection();
		PreparedStatement ps = null;
		CachedRowSet crs =null;
		ResultSet rs = null;
		try{
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			RowSetFactory factory = RowSetProvider.newFactory();  
			crs = factory.createCachedRowSet(); 
			ResultSetMetaData rsmd = rs.getMetaData();
			int length = rsmd.getColumnCount();
			String[] headerNames = new String[length];
			for(int i=0;i<length;i++){
				String headerName = rsmd.getColumnName((i+1));
				String headerLabel = rsmd.getColumnLabel((i+1));
				String value = (headerLabel==null||headerLabel.equals(""))?headerName:headerLabel;
				headerNames[i] = value;
			}
			crs.populate(rs);
			List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
			while(crs.next()){
				Map<String,Object> map = new HashMap<String,Object>();
				for(int i=0;i<length;i++){
					Object value = crs.getObject((i+1));
					map.put(headerNames[i], value);
				}
				list.add(map);
			}
			return list;
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(crs!=null){
				try {
					crs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(ps!=null){
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(conn!=null){
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	/**
	 * 查询字符串
	 * @param sql
	 * @return
	 */
	public String queryString(String sql){
		Connection conn = this.getConection();
		PreparedStatement ps = null;
		CachedRowSet crs =null;
		ResultSet rs = null;
		try{
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			RowSetFactory factory = RowSetProvider.newFactory();  
			crs = factory.createCachedRowSet(); 
			crs.populate(rs);
			while(crs.next()){
				return (crs.getObject(1)).toString();
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(crs!=null){
				try {
					crs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(ps!=null){
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(conn!=null){
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return "";
	}
	
	/**
	 * 获取表信息
	 * 
	 * @param tableName
	 * @return
	 */
	public Table getTableInfo(String tableName) {
		Table table = new Table();
		Connection conn = this.getConection();
		try {
			DatabaseMetaData dm = conn.getMetaData();
			String[] types = { "TABLE" };
			ResultSet rs = dm.getTables(null, null, tableName, types);
			while (rs.next()) {
				ClassUtils.setFieldValue(table,"tableName", rs.getString("TABLE_NAME"));
			}
			rs.close();
			
			Set<String> keySet = new HashSet<String>();  
			ResultSet	primarykeyRs = null;
			try {
				primarykeyRs   = conn.getMetaData().getPrimaryKeys(conn.getCatalog(), "%", tableName);  
		        while (primarykeyRs.next())  {  
		           keySet.add( primarykeyRs.getString("COLUMN_NAME"));
		        }  
			}catch(Exception e) {
				e.printStackTrace();
			}finally {
				if(primarykeyRs!=null) {
					primarykeyRs.close();
				}
			}
	       
			ResultSet rscolumn = dm.getColumns(null, null, tableName, null);
			ResultSetMetaData rsmdcolumn = rscolumn.getMetaData();
			String[] headercolumn = new String[rsmdcolumn.getColumnCount()];
			for (int i = 0; i < rsmdcolumn.getColumnCount(); i++) {
				headercolumn[i] = rsmdcolumn.getColumnName((i + 1));
			}
			Map<String,Column> list = new HashMap<String,Column>();
			while (rscolumn.next()) {
				Column columnInfo = new Column();
				String name = rscolumn.getString("COLUMN_NAME");
				String fieldName = StringUtils.underlineToCamel(name, true);
				columnInfo.setName(name);
				columnInfo.setDefaultValue(rscolumn.getString("COLUMN_DEF"));
				int length = 0;
				if(!StringUtils.isEmpty(rscolumn.getString("COLUMN_SIZE"))) {
					length = Integer.valueOf(rscolumn.getString("COLUMN_SIZE"));
				}
				columnInfo.setLength(length);
				int precision = 0;
				if(!StringUtils.isEmpty(rscolumn.getString("DECIMAL_DIGITS"))) {
					precision = Integer.valueOf(rscolumn.getString("DECIMAL_DIGITS"));
				}
				columnInfo.setPrecision(precision);
				columnInfo.setRemark(rscolumn.getString("REMARKS"));
				String dbtype = rscolumn.getString("DATA_TYPE");
				columnInfo.setType(ColumnType.getByCode(dbtype));
				if(keySet.contains(name)) {
					columnInfo.setPrimary(true);
				}
				list.put(fieldName,columnInfo);
			}
			rscolumn.close();
			table.setColumnMap(list);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return table;
	}

	/**
	 * 判断表是否在数据库存在
	 * 
	 * @param tableName
	 * @return
	 */
	public boolean checkTableExist(String tableName) {
		Connection conn = this.getConection();
		try {
			DatabaseMetaData dm = conn.getMetaData();
			String[] types = { "TABLE" };
			ResultSet rs = dm.getTables(conn.getCatalog(), null, tableName, types);
			int rowCount = 0;
			while (rs.next()) {
				rowCount++;
			}
			if(rowCount==1){
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(conn!=null){
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}
	
	/**
	 * 获取操作数据库的DDL语句
	 * 
	 * @param EntityInfo
	 * @return
	 */
	public List<String> getDDLSql(Table entity) {
		if (entity == null || entity.getTableName() == null || entity.getTableName().equals("")
				|| entity.getColumnMap() == null || entity.getColumnMap().size() == 0) {
			return null;
		}
		if (!checkTableExist(entity.getTableName())) {
			return createTableDDL(entity);
		}else{
			return alterTableDDL(entity);
		}
	}
	/**
	 * 创建DataMeta
	 * 
	 * @param type
	 * @param host
	 * @param port
	 * @param dbName
	 * @param assess
	 * @param password
	 * @return
	 */
	public static AbstractDataMeta createDataMeta(DBType type,String dataSourceName) {
		AbstractDataMeta dataMeta = null;
		switch (type) {
		case mysql:
			dataMeta = new MysqlDataMeta();
			dataMeta.setDataSourceName(dataSourceName);
			break;
		default:
			break;
		}
		return dataMeta;
	}
	
	/***
	 * 检查列是否存在
	 * @param tableName
	 * @param columnName
	 * @return
	 */
	public boolean checkColumnExist(String tableName,String columnName){
		Connection conn =  this.getConection();
		ResultSet rs    =  null;
		try{
			if(conn!=null){
				DatabaseMetaData dm = conn.getMetaData();
				rs = dm.getColumns(null, null, tableName, columnName);
				int count = 0;
				while(rs.next()){
					count ++;
				}
				if(count==1){
					return true;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(conn!=null){
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}
	

}
