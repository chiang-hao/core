package chianghao.core.db.dao;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.SqlSessionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import chianghao.core.db.model.Table;
import chianghao.core.db.sql.Page;
import chianghao.core.db.sql.ResultMapTool;
import chianghao.core.db.sql.Sql;
import chianghao.core.db.sql.SqlGenTool;
import chianghao.core.exception.ErrorInfo;
import chianghao.core.exception.HaoException;
import chianghao.core.utils.ClassUtils;


public class BaseDao<T> {
	
	@Autowired
	SqlSessionTemplate  sqlSessionTemplate;
	
	private Class<?> clazz = this.getParameterizedType();
	
	private static final Logger LOG = LoggerFactory.getLogger(BaseDao.class);
	
	@SuppressWarnings("rawtypes")
	private Class getParameterizedType() {
		return ClassUtils.getSuperClassParameterizedType(this);
	}

	/**
          * 获取原生SqlSession,需要手动关闭SqlSession
          * 对应 {@code BaseDaoImpl.closeNativeSqlSession}
     * @return
     */
    protected SqlSession getNativeSqlSession() {
        return SqlSessionUtils.getSqlSession(sqlSessionTemplate.getSqlSessionFactory(),
                sqlSessionTemplate.getExecutorType(),sqlSessionTemplate.getPersistenceExceptionTranslator());
    }
    /**
          * 关闭原生SqlSession
          * 对应 getNativeSqlSession
     * @return
     */
    protected void closeNativeSqlSession(SqlSession sqlSession){
        SqlSessionUtils.closeSqlSession(sqlSession, sqlSessionTemplate.getSqlSessionFactory());
    }
	
	public void insert(T bean) {
		if(bean==null) {
			throw new HaoException(ErrorInfo.build_sql_error, "构建inster sql 对象不能为null");
		}
		Table table = Table.getTable(this.clazz);
		if(table==null) {
			throw new HaoException(ErrorInfo.build_sql_error, "构建inster sql 收件查询到注解的表信息");
		}
		Sql sql = SqlGenTool.getInsterSql(table,bean);
		if(sql==null) {
			return;
		}
		execute(sql.getSql(),sql.getArgs());
	}
	
	
	public void insertBatch(List<T> list) {
		if(list==null||list.size()==0) {
			throw new HaoException(ErrorInfo.build_sql_error, "构建inster sql 对象不能为null");
		}
		Table table = Table.getTable(this.clazz);
		if(table==null) {
			throw new HaoException(ErrorInfo.build_sql_error, "构建inster sql 收件查询到注解的表信息");
		}
		Sql sql = SqlGenTool.getInsterBatchSql(table,list);
		if(sql==null) {
			return;
		}
		execute(sql.getSql(),sql.getArgs());
	}
	
	public void update(T bean,T where) {
		if(bean==null) {
			throw new HaoException(ErrorInfo.build_sql_error, "构建inster sql 对象不能为null");
		}
		if(where==null) {
			throw new HaoException(ErrorInfo.build_sql_error, "构建inster sql 对象不能为null");
		}
		Table table = Table.getTable(this.clazz);
		if(table==null) {
			throw new HaoException(ErrorInfo.build_sql_error, "构建inster sql 收件查询到注解的表信息");
		}
		Sql sql = SqlGenTool.getUpdateSql(table,bean,where);
		if(sql==null) {
			return;
		}
		execute(sql.getSql(),sql.getArgs());
	}

	public void updateByCode(T bean,Object code) {
		if(bean==null) {
			throw new HaoException(ErrorInfo.build_sql_error, "构建inster sql 对象不能为null");
		}
		Table table = Table.getTable(this.clazz);
		if(table==null) {
			throw new HaoException(ErrorInfo.build_sql_error, "构建inster sql 收件查询到注解的表信息");
		}
		Sql sql = SqlGenTool.getUpdateByCodeSql(table,bean,code);
		if(sql==null) {
			return;
		}
		execute(sql.getSql(),sql.getArgs());
	}
	
	public void delete(T bean) {
		if(bean==null) {
			throw new HaoException(ErrorInfo.build_sql_error, "构建inster sql 对象不能为null");
		}
		Table table = Table.getTable(this.clazz);
		if(table==null) {
			throw new HaoException(ErrorInfo.build_sql_error, "构建inster sql 收件查询到注解的表信息");
		}
		Sql sql = SqlGenTool.getDeleteSql(table,bean);
		if(sql==null) {
			return;
		}
		execute(sql.getSql(),sql.getArgs());
	}
	
	public void delByCode(Object code) {
		Table table = Table.getTable(this.clazz);
		Sql sql = SqlGenTool.getDelByCodeSql(table,code);
		if(sql==null) {
			return;
		}
		execute(sql.getSql(),sql.getArgs());
	}
	
	public void execute(String sql,List<Object> args) {
		PreparedStatement ps = null;
		LOG.info("---------the sql is {}---------" , sql);
		
		SqlSession sqlSession = getNativeSqlSession();
		Connection conn = sqlSession.getConnection();
		try {
			ps = conn.prepareStatement(sql);
			if(args!=null&&args.size()>0) {
				int i=1;
				for(Object value:args) {
					LOG.info("==>" + value);
					if ((value instanceof Date)) {
						ps.setTimestamp(i, new Timestamp(((Date) value).getTime()));
					}else if ((value instanceof Blob)) {
						ps.setBlob(i, (Blob) value);
					}else if ((value instanceof Clob)) {
						ps.setClob(i, (Clob) value);
					}else if ((value instanceof BigDecimal)) {
						ps.setBigDecimal(i, (BigDecimal) value);
					}else if ((value instanceof BigInteger)) {
						ps.setBigDecimal(i, new BigDecimal((BigInteger) value));
					}else if ((value instanceof Boolean)) {
						ps.setInt(i, ((Boolean) value).booleanValue() ? 1 : 0);
					}else {
						ps.setObject(i, value);
					}
					i++;
				}
			}
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			throw new HaoException("Mapper_Result_Error", e.getMessage());
		}finally {
			if(ps!=null) {
				try {
					ps.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(sqlSession != null){
                closeNativeSqlSession(sqlSession);
            }
		}
	}
	
	
	
	public List<Object[]> queryListArray(String sql,List<Object> args){
		PreparedStatement ps = null;
		ResultSet rs = null;
		LOG.info("---------the sql is {}---------" , sql);
		SqlSession sqlSession = getNativeSqlSession();
		Connection conn = sqlSession.getConnection();
		try {
			ps = conn.prepareStatement(sql);
			if(args!=null&&args.size()>0) {
				int i=1;
				for(Object value:args) {
					LOG.info("==>" + value);
					if ((value instanceof Date)) {
						ps.setTimestamp(i, new Timestamp(((Date) value).getTime()));
					}else if ((value instanceof Blob)) {
						ps.setBlob(i, (Blob) value);
					}else if ((value instanceof Clob)) {
						ps.setClob(i, (Clob) value);
					}else if ((value instanceof BigDecimal)) {
						ps.setBigDecimal(i, (BigDecimal) value);
					}else if ((value instanceof BigInteger)) {
						ps.setBigDecimal(i, new BigDecimal((BigInteger) value));
					}else if ((value instanceof Boolean)) {
						ps.setInt(i, ((Boolean) value).booleanValue() ? 1 : 0);
					}else {
						ps.setObject(i, value);
					}
					i++;
				}
			}
			rs = ps.executeQuery();
			List<Object[]> list = ResultMapTool.getListArray(rs);
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			throw new HaoException("Mapper_Result_Error", e.getMessage());
		}finally {
			if(ps!=null) {
				try {
					ps.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(rs!=null) {
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(sqlSession != null){
                closeNativeSqlSession(sqlSession);
            }
		}
	}
	
	public List<T> query(String sql,List<Object> args){
		PreparedStatement ps = null;
		ResultSet rs = null;
		LOG.info("---------the sql is {}---------" , sql);
		SqlSession sqlSession = getNativeSqlSession();
		Connection conn = sqlSession.getConnection();
		try {
			ps = conn.prepareStatement(sql);
			if(args!=null&&args.size()>0) {
				int i=1;
				for(Object value:args) {
					LOG.info("==>" + value);
					if ((value instanceof Date)) {
						ps.setTimestamp(i, new Timestamp(((Date) value).getTime()));
					}else if ((value instanceof Blob)) {
						ps.setBlob(i, (Blob) value);
					}else if ((value instanceof Clob)) {
						ps.setClob(i, (Clob) value);
					}else if ((value instanceof BigDecimal)) {
						ps.setBigDecimal(i, (BigDecimal) value);
					}else if ((value instanceof BigInteger)) {
						ps.setBigDecimal(i, new BigDecimal((BigInteger) value));
					}else if ((value instanceof Boolean)) {
						ps.setInt(i, ((Boolean) value).booleanValue() ? 1 : 0);
					}else {
						ps.setObject(i, value);
					}
					i++;
				}
			}
			rs = ps.executeQuery();
			List<T> list = ResultMapTool.getList(rs, clazz);
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			throw new HaoException("Mapper_Result_Error", e.getMessage());
		}finally {
			if(ps!=null) {
				try {
					ps.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(rs!=null) {
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(sqlSession != null){
                closeNativeSqlSession(sqlSession);
            }
		}
	}
	
	
	private List<T> query(Sql sql){
		return query(sql.getSql(),sql.getArgs());
	}
	
	public T queryBean(T bean) {
		Table table = Table.getTable(this.clazz);
		Sql sql = SqlGenTool.getSelectSql(table,bean);
		List<T> list = query(sql);
		if(list==null||list.size()==0){
			return null;
		}
		if(list.size()==1) {
			return list.get(0);
		}
		throw new HaoException("sql_result_more_size", "查询出多条数据");
	}
	
	public int queryCount(T bean) {
		Table table = Table.getTable(this.clazz);
		Sql sql = SqlGenTool.getSelectSql(table,bean);
		String countSql = this.getCountSql(sql.getSql());
		return this.queryInt(countSql, sql.getArgs());
	}
	
	public List<T> queryAll(){
		Table table = Table.getTable(this.clazz);
		Sql sql = SqlGenTool.getSelectSql(table);
		return query(sql);
	}
	
	
	public int queryInt(String sql,List<Object> args) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		LOG.info("---------the sql is {}---------",sql);
		
		
		SqlSession sqlSession = getNativeSqlSession();
		Connection conn = sqlSession.getConnection();
		try {
			ps = conn.prepareStatement(sql);
			if(args!=null&&args.size()>0) {
				int i=1;
				for(Object value:args) {
					LOG.info("==>" + value);
					if ((value instanceof Date)) {
						ps.setTimestamp(i, new Timestamp(((Date) value).getTime()));
					}else if ((value instanceof Blob)) {
						ps.setBlob(i, (Blob) value);
					}else if ((value instanceof Clob)) {
						ps.setClob(i, (Clob) value);
					}else if ((value instanceof BigDecimal)) {
						ps.setBigDecimal(i, (BigDecimal) value);
					}else if ((value instanceof BigInteger)) {
						ps.setBigDecimal(i, new BigDecimal((BigInteger) value));
					}else if ((value instanceof Boolean)) {
						ps.setInt(i, ((Boolean) value).booleanValue() ? 0 : 1);
					}else {
						ps.setObject(i, value);
					}
					i++;
				}
			}
			rs = ps.executeQuery();
			int end = 0;
			if (rs.next()) {
				end = rs.getInt(1);
			}
			return end;
		} catch (Exception e) {
			e.printStackTrace();
			throw new HaoException("Mapper_Result_Error", e.getMessage());
		}finally {
			if(ps!=null) {
				try {
					ps.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(rs!=null) {
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(sqlSession != null){
                closeNativeSqlSession(sqlSession);
            }
		}
	}
	
	public List<T> queryList(T bean){
		Table table = Table.getTable(this.clazz);
		Sql sql = SqlGenTool.getSelectSql(table,bean);
		return query(sql);
	}

	public void queryPageList(Page<T> page){
		queryPageList(page,null);
	}
	
	public T queryByCode(Object code) {
		Table table = Table.getTable(this.clazz);
		Sql sql = SqlGenTool.getSelectByCodeSql(table,code);
		List<T> list = query(sql);
		if(list==null||list.size()==0){
			return null;
		}
		if(list.size()==1) {
			return list.get(0);
		}
		throw new HaoException("sql_result_more_size", "查询出多条数据");
	}
	
	/**
	 * 分页查询
	 * @param page
	 * @param bean
	 * @return
	 */
	public void queryPageList(Page<T> page,T bean){
		Table table = Table.getTable(this.clazz);
		Sql sql = SqlGenTool.getSelectSql(table,bean);
		String countSql = this.getCountSql(sql.getSql());
		int totalRecord =  queryInt(countSql,sql.getArgs());
		page.setTotalRows(totalRecord);
		String pageSql = getPageSql(page,sql.getSql());
		List<T> list = query(pageSql,sql.getArgs());
		page.setItems(list);
	}
	
	
	/**
	 * 分页查询
	 * @param page
	 * @param sql
	 * @param args
	 */
	public void queryPageList(Page<T> page,String sql,List<Object> args) {
		String countSql = this.getCountSql(sql);
		int totalRecord =  queryInt(countSql,args);
		page.setTotalRows(totalRecord);
		String pageSql = getPageSql(page,sql);
		List<T> list = query(pageSql,args);
		page.setItems(list);
	}
	
	
	private String getPageSql(Page<T> page, String sql) {
		StringBuilder sqlBuffer = new StringBuilder(sql);
		Map<String, String> order = page.getOrder();
		if (order != null && !order.isEmpty()) {
			sqlBuffer.append(" order by ");
			Set<String> keys = order.keySet();
			int i = 0;
			for (String key : keys) {
				if (i != keys.size() - 1) {
					sqlBuffer.append(key + " " + order.get(key) + ", ");
				} else {
					sqlBuffer.append(key + " " + order.get(key) + " ");
				}
				i++;
			}
		}
		sqlBuffer.append(" limit ").append(page.getOffset()).append(",").append(page.getPageSize());
		return sqlBuffer.toString();
	}
	
	
	private String getCountSql(String sql) {
		// 转换为小写
		sql = sql.toLowerCase();
		// 首先去掉排序：
		int currPos = sql.toLowerCase().lastIndexOf("order by ");
		if (currPos > 0) {
			sql = sql.substring(0, currPos);
		}
		// 如果有Group by或者distinct，就采用原有做法
		currPos = sql.lastIndexOf("group by") > 0 ? 1 : sql.lastIndexOf("distinct");
		if (currPos > 0) {
			return "SELECT COUNT(*) FROM (" + sql + ") TEMP_COUNT_TABLE";
		}
		String countStr = "select count(*) ";

		// 把select子句换成select count()子句
		// 1查找from位置
		int fromPos = sql.indexOf("from");

		return countStr += sql.substring(fromPos);
	}
	
}
