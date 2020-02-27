package haoframe.core.mybatis.plugins;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class, Integer.class }) })
public class PaginationInterceptor implements Interceptor {

	Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public Object intercept(Invocation invocation) throws Exception  {
		StatementHandler statementHandler = realTarget(invocation.getTarget());
		MetaObject metaStatementHandler = SystemMetaObject.forObject(statementHandler);  
		MappedStatement mappedStatement=(MappedStatement) metaStatementHandler.getValue("delegate.mappedStatement");
		BoundSql boundSql = statementHandler.getBoundSql();
		Paging paging = isPageFuncton(mappedStatement,boundSql);
		if(paging!=null) {
			//查询total,替换分页语句
			Connection connection = (Connection)invocation.getArgs()[0];
			String originalSql = boundSql.getSql();
			int index = originalSql.indexOf("from");
	        // 将sql from坐标前的字符截断，加上 select count(1) coun 查询结果集条数的SQL
			String totalSql = "select count(1) coun " + originalSql.substring(index);
			DefaultParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement, boundSql.getParameterObject(), boundSql);
			PreparedStatement statement =null;
			ResultSet resultSet  = null;
			long total = 0L;
			try {
				statement =  connection.prepareStatement(totalSql);
				parameterHandler.setParameters(statement);
				
				resultSet    = statement.executeQuery();
				if (resultSet.next()) {
                    total = resultSet.getLong(1);
                }
			}catch(Exception e) {
				throw e;
			}finally {
				if(resultSet!=null) {
					try {
						resultSet.close();
					} catch (SQLException e) {
						e.printStackTrace();
						log.error("分页连接器===>关闭resultSet异常",e);
					}
				}
				if(statement!=null) {
					try {
						statement.close();
					} catch (SQLException e) {
						e.printStackTrace();
						log.error("分页连接器===>关闭statement异常",e);
					}
				}
			}
			if(total==0) {
				return null;
			}
			paging.setTotalRows(total);
			String sql = originalSql + " LIMIT "+paging.getOffset()+","+paging.getPageSize();
			metaStatementHandler.setValue("delegate.boundSql.sql", sql);
		}
		return invocation.proceed();
	}

	@Override
	public Object plugin(Object target) {
		 if (target instanceof StatementHandler) {
	            return Plugin.wrap(target, this);
	        }
	        return target;
	}

	@Override
	public void setProperties(Properties properties) {
		// TODO Auto-generated method stub

	}

	
	@SuppressWarnings({ "unchecked"})
	private Paging isPageFuncton(MappedStatement mappedStatement,BoundSql boundSql) {
		//非查询方法直接排除
		if (SqlCommandType.SELECT != mappedStatement.getSqlCommandType()
	            || StatementType.CALLABLE == mappedStatement.getStatementType()) {
	           return null;
	    }
		//条件 是query  有参数 page(Page)  
		Object paramObject  = boundSql.getParameterObject();
		if(!Map.class.isAssignableFrom(paramObject.getClass())) {
			return null;
		}
		
		Map<String,Object> paramObjectMap = (Map<String, Object>) paramObject;
		if(!paramObjectMap.containsKey("page")) {
			return null;
		}
		
		Object _page = paramObjectMap.get("page");
		if(!(_page instanceof Paging)) {
			return null;
		}
		return (Paging) _page;
	}
	
	
	@SuppressWarnings("unchecked")
	public <T> T realTarget(Object target) {
		if (Proxy.isProxyClass(target.getClass())) {
			MetaObject metaObject = SystemMetaObject.forObject(target);
			return realTarget(metaObject.getValue("h.target"));
		}
		return (T) target;
	}

}
