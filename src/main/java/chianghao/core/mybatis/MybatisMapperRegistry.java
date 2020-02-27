package chianghao.core.mybatis;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.binding.BindingException;
import org.apache.ibatis.binding.MapperProxyFactory;
import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.session.SqlSession;

public class MybatisMapperRegistry extends MapperRegistry {

	private final Map<Class<?>, MapperProxyFactory<?>> knownMappers = new HashMap<>();
	private final MybatisConfiguration config;

	public MybatisMapperRegistry(MybatisConfiguration config) {
		super(config);
		this.config = config;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
		// TODO 这里换成 MybatisMapperProxyFactory 而不是 MapperProxyFactory
		final MapperProxyFactory<T> mapperProxyFactory = (MapperProxyFactory<T>) knownMappers.get(type);
		if (mapperProxyFactory == null) {
			throw new BindingException("Type " + type + " is not known to the MybatisPlusMapperRegistry.");
		}
		try {
			return mapperProxyFactory.newInstance(sqlSession);
		} catch (Exception e) {
			throw new BindingException("Error getting mapper instance. Cause: " + e, e);
		}
	}

	@Override
	public <T> boolean hasMapper(Class<T> type) {
		return knownMappers.containsKey(type);
	}

	@Override
	public <T> void addMapper(Class<T> type) {
		if (type.isInterface()) {
			if (hasMapper(type)) {
				//throw new BindingException("Type " + type + " is already known to the MapperRegistry.");
				return;
			}
			boolean loadCompleted = false;
			try {
				knownMappers.put(type, new MapperProxyFactory<>(type));
				// It's important that the type is added before the parser is run
				// otherwise the binding may automatically be attempted by the
				// mapper parser. If the type is already known, it won't try.
				// MapperAnnotationBuilder parser = new MapperAnnotationBuilder(config, type);
				MybatisMapperAnnotationBuilder parser = new MybatisMapperAnnotationBuilder(config, type);
				parser.parse();
				loadCompleted = true;
			} finally {
				if (!loadCompleted) {
					knownMappers.remove(type);
				}
			}
		}
		
	}

	/**
	 * 使用自己的 knownMappers
	 */
	@Override
	public Collection<Class<?>> getMappers() {
		return Collections.unmodifiableCollection(knownMappers.keySet());
	}

}
