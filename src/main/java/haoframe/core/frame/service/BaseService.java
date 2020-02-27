package haoframe.core.frame.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import haoframe.core.exception.HaoException;
import haoframe.core.mybatis.mapper.BaseMapper;
import haoframe.core.mybatis.plugins.Paging;
import haoframe.core.mybatis.sql.SqlOrder;
import haoframe.core.mybatis.sql.SqlWrapper;
import haoframe.core.mybatis.sql.db_enum.SqlOperators;
import haoframe.core.mybatis.sql.db_enum.SqlOrderType;
import haoframe.core.utils.ClassUtils;

public abstract class BaseService<M extends BaseMapper<T>,T> {

	@Autowired
	protected M mapper;
	//	///////////////////////////////////////////////写方法////////////////////////////////////////////////////////////////////////
	
	/**
	 * 根据主键code在数据库中是否存在
	 * if 主键 code 不存在
	 * 		insert 
	 * else
	 *      upate
	 * 
	 * @param bean
	 * @return bean主键
	 */
	@Transactional(rollbackFor=Exception.class)
	public boolean save(T bean) {
		Object code = ClassUtils.getFieldValue(bean, "code");
		if(code==null) {
			throw new HaoException("the code can not null");
		}
		if(codeIsExist(code)) {
			//update
			this.mapper.updateByCode(bean, code);
		}else {
			//insert
			this.mapper.insert(bean);
		}
		return true;
	}
	
	/**
	 * 删除
	 * @param code
	 */
	@Transactional(rollbackFor=Exception.class)
	public void delete(Object code) {
		mapper.deleteByCode(code);
	}
	
	/**
	 * 插入
	 * @param bean
	 */
	@Transactional(rollbackFor=Exception.class)
	public void insert(T bean) {
		Object code = ClassUtils.getFieldValue(bean, "code");
		if(code==null) {
			throw new HaoException("the code can not null");
		}
		mapper.insert(bean);
	}
	
	/**
	 * 批量插入
	 * @param beans
	 */
	@Transactional(rollbackFor=Exception.class)
	public void insertBatch(List<T> beans) {
		mapper.batchInsert(beans);
	}
	
	/**
	 * 更新
	 * @param bean
	 */
	@Transactional(rollbackFor=Exception.class)
	public void update(T bean) {
		Object code = ClassUtils.getFieldValue(bean, "code");
		if(code==null) {
			throw new HaoException("the code can not null");
		}
		mapper.updateByCode(bean, code);
	}
	
	
	///////////////////////////////////////////////////读方法////////////////////////////////////////////////////////////////////////
	
	/**
	 * 查询fieldName = value的数量
	 * @param fieldName
	 * @param value
	 * @return
	 */
	public int queryCount(String fieldName,Object value) {
		Integer count = (Integer) this.mapper.queryObject(new SqlWrapper().addFields("count(1)").addCondition(fieldName,value));
		return count;
	}
	
	/**
	 * 查新非此主键，fieldName=value的个数
	 * @param fieldName
	 * @param value
	 * @param code
	 * @return
	 */
	public int queryCount(String fieldName,Object value,Object code) {
		Integer count = (Integer) this.mapper.queryObject(new SqlWrapper().addFields("count(1)").addCondition(fieldName,value).addCondition("code",SqlOperators.not_equals,code));
		return count;
	}
	
	/**
	 * 查询主键的个数
	 * @param code
	 * @return
	 */
	public boolean codeIsExist(Object code) {
		Integer count = (Integer) this.mapper.queryObject(new SqlWrapper().addFields("count(1)").addCondition("code",code));
		return count>0?true:false;
	}
	
	/**
	 * 查询list
	 * @param where
	 * @return
	 */
	public List<T> query(T where) {
		return mapper.queryListByEntity(where, null);
	}
	/**
	 * 查询list
	 * @param where
	 * @return
	 */
	public List<T> query(T where,String orderFiledName,SqlOrderType orderType) {
		SqlOrder order  = new SqlOrder();
		order.add(orderFiledName, orderType);
		return mapper.queryListByEntity(where, order);
	}
	
	/**
	 * 查询分页list
	 * @param where
	 * @param paging
	 * @return
	 */
	public List<T> queryPage(T where,Paging paging){
		return mapper.queryPageListByEntity(paging, where, null);
	}
	
	/**
	 * 查询分页list
	 * @param where
	 * @param paging
	 * @return
	 */
	public List<T> queryPage(T where,Paging paging,String orderFiledName,SqlOrderType orderType){
		SqlOrder order  = new SqlOrder();
		order.add(orderFiledName, orderType);
		return mapper.queryPageListByEntity(paging, where, order);
	}
	
	/**
	 * 根据主键查询实体
	 * @param code
	 * @return
	 */
	public T queryOne(Object code) {
		return mapper.queryOneByCode(code);
	}
	
	/**
	 * 根据传入的实体查新
	 * @param where
	 * @return
	 */
	public T queryBean(T where) {
		return mapper.queryOneByEntity(where);
	}
	
}
