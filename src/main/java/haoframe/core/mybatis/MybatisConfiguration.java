package haoframe.core.mybatis;

import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;

public class MybatisConfiguration extends Configuration {

	protected final MybatisMapperRegistry mapperRegistry = new MybatisMapperRegistry(this);

	
	/**
     * 初始化调用
     */
    public MybatisConfiguration() {
        super();
    }
	
	public MybatisConfiguration(Environment environment) {
	    this();
	    this.environment = environment;
    }
	
	public MapperRegistry getMapperRegistry() {
		return mapperRegistry;
	}

	public void addMappers(String packageName, Class<?> superType) {
		mapperRegistry.addMappers(packageName, superType);
	}

	public void addMappers(String packageName) {
		mapperRegistry.addMappers(packageName);
	}

	public <T> void addMapper(Class<T> type) {
		mapperRegistry.addMapper(type);
	}

	public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
		return mapperRegistry.getMapper(type, sqlSession);
	}

	public boolean hasMapper(Class<?> type) {
		return mapperRegistry.hasMapper(type);
	}

}
