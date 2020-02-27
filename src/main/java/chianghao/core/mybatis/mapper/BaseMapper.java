package chianghao.core.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import chianghao.core.mybatis.plugins.Paging;
import chianghao.core.mybatis.sql.SqlOrder;
import chianghao.core.mybatis.sql.SqlWrapper;

public interface BaseMapper<T>  {

	//register start 基本方法
	public int insert(T bean);
	
	public int batchInsert(@Param("beanes") List<T> beanes);
	
	public int update(@Param("bean")T bean,@Param("sqlWrapper") SqlWrapper sqlWrapper);
	
	public int delete(SqlWrapper sqlWrapper);
	
	public T queryOne(SqlWrapper sqlWrapper);
	
	public List<T> queryList(SqlWrapper sqlWrapper);
	
	public List<T> queryPageList(@Param("page")Paging paging,@Param("sqlWrapper") SqlWrapper sqlWrapper);
	
	public Object queryObject(SqlWrapper sqlWrapper);
	
	//register end 基本方法
	
	//register start 基于实体操作
	public int updateByEntity(@Param("bean")T bean,@Param("where") T where);
	
	public int deleteByEntity(T where);
	
	public T queryOneByEntity(T where);
	
	public List<T> queryListByEntity(@Param("where") T where,@Param("order") SqlOrder order);
	
	public List<T> queryPageListByEntity(@Param("page")Paging paging,@Param("where") T where,@Param("order") SqlOrder order);
	
	public Object queryObjectByEntity(@Param("fieldName") String fieldName,@Param("where") T bean);
	//register end 基于实体操作
	
	//register start 基于主键CODE操作
	public int deleteByCode(Object code);
	
	public int updateByCode(@Param("bean") T bean,@Param("code") Object code);
	
	public T queryOneByCode(Object code);
	//register end 基于主键CODE操作
}
