package chianghao.core.mybatis.method;

import org.apache.ibatis.mapping.SqlCommandType;

/***
 * Base
 * @author chianghao
 *
 */
public enum BaseMapperMethodDefinition{
	
	queryObject("queryObject",SqlCommandType.SELECT,null,Object.class),
	queryPageList("queryPageList",SqlCommandType.SELECT,null,null),
	queryList("queryList",SqlCommandType.SELECT,null,null),
	queryOne("queryOne",SqlCommandType.SELECT,null,null),
	delete("delete",SqlCommandType.DELETE,null,Integer.class),
	update("update",SqlCommandType.UPDATE,null,Integer.class),
	insert("insert",SqlCommandType.INSERT,null,Integer.class),
	batchInsert("batchInsert",SqlCommandType.INSERT,null,Integer.class),
	
	
	updateByEntity("updateByEntity",SqlCommandType.UPDATE,null,Integer.class),
	deleteByEntity("deleteByEntity",SqlCommandType.DELETE,null,Integer.class),
	queryOneByEntity("queryOneByEntity",SqlCommandType.SELECT,null,null),
	queryListByEntity("queryListByEntity",SqlCommandType.SELECT,null,null),
	queryPageListByEntity("queryPageListByEntity",SqlCommandType.SELECT,null,null),
	queryObjectByEntity("queryObjectByEntity",SqlCommandType.SELECT,null,Object.class),
	
	
	
	deleteByCode("deleteByCode",SqlCommandType.DELETE,null,Integer.class),
	updateByCode("updateByCode",SqlCommandType.UPDATE,null,Integer.class),
	queryOneByCode("queryOneByCode",SqlCommandType.SELECT,null,null),
	;

	
	private BaseMapperMethodDefinition(String name,SqlCommandType type,Class<?> parameterType,Class<?> returnType) {
		this.methodName = name;
		this.sqlCommonType=type;
		this.parameterType = parameterType;
		this.returnType = returnType;
	}
	
	private String methodName;
	
	private SqlCommandType sqlCommonType;
	
	private  Class<?> parameterType;
	
	private Class<?> returnType;
	
	
	public Class<?> getReturnType() {
		return returnType;
	}
	public void setReturnType(Class<?> returnType) {
		this.returnType = returnType;
	}
	public Class<?> getParameterType() {
		return parameterType;
	}
	public void setParameterType(Class<?> parameterType) {
		this.parameterType = parameterType;
	}
	public SqlCommandType getSqlCommonType() {
		return sqlCommonType;
	}
	public void setSqlCommonType(SqlCommandType sqlCommonType) {
		this.sqlCommonType = sqlCommonType;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	
	public static boolean isBaseMappMethod(String name) {
		for(BaseMapperMethodDefinition m:BaseMapperMethodDefinition.values()) {
			if(m.getMethodName().equals(name)) {
				return true;
			}
		}
		return false;
	}
	
}
