package haoframe.core.mybatis.method;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;

import haoframe.core.db.model.Table;
import haoframe.core.mybatis.StringPool;

public class MethodInjectionTool {

	private final Log logger = LogFactory.getLog(MethodInjectionTool.class);

	Class<?> mapperClass;
	MapperBuilderAssistant builderAssistant;
	Configuration configuration;
	LanguageDriver languageDriver;
	Class<?> tableClass;
	Table table;

	public MethodInjectionTool(Class<?> type, MapperBuilderAssistant assistant) {
		this.mapperClass = type;
		this.builderAssistant = assistant;
		this.configuration = builderAssistant.getConfiguration();
		this.languageDriver = configuration.getDefaultScriptingLanguageInstance();
		try {
			tableClass = extractModelClass(mapperClass);
			table = Table.getTable(tableClass);
		} catch (Exception e) {
			logger.error("获取baseMapper的泛型class对象失败，或者获取泛型类定义的info失败");
		}

	}

	/**
	 * 提取泛型模型,多泛型的时候请将泛型T放在第一位
	 *
	 * @param mapperClass mapper 接口
	 * @return mapper 泛型
	 */
	protected Class<?> extractModelClass(Class<?> mapperClass) {
		Type[] types = mapperClass.getGenericInterfaces();
		ParameterizedType target = null;
		for (Type type : types) {
			if (type instanceof ParameterizedType) {
				Type[] typeArray = ((ParameterizedType) type).getActualTypeArguments();
				if (ArrayUtils.isNotEmpty(typeArray)) {
					for (Type t : typeArray) {
						if (t instanceof TypeVariable || t instanceof WildcardType) {
							break;
						} else {
							target = (ParameterizedType) type;
							break;
						}
					}
				}
				break;
			}
		}
		return target == null ? null : (Class<?>) target.getActualTypeArguments()[0];
	}

	public void injection() {
		if (tableClass != null) {
			if (table != null) {
				for (BaseMapperMethodDefinition m : BaseMapperMethodDefinition.values()) {
					addInsertMappedStatement(this.mapperClass,m);
				}
			} else {
				logger.error(tableClass.toString() + ", No effective table was found.");
			}
		}
	}

	protected MappedStatement addInsertMappedStatement(Class<?> mapperClass, BaseMapperMethodDefinition md) {
		KeyGenerator keyGenerator = new NoKeyGenerator();
		String keyProperty = null;
		String keyColumn = null;
		
		Class<?> returnType = md.getReturnType();
		switch(md) {
		case queryPageList:
		case queryList:
		case queryOne:
		case queryOneByEntity:
		case queryListByEntity:
		case queryPageListByEntity:
		case queryOneByCode:
			returnType=tableClass;
		default:
			break;
		}
		
		MybatisSqlSource sqlSource  = new MybatisSqlSource(this.table,this.mapperClass,md,configuration);
		return addMappedStatement(mapperClass, md.getMethodName(), sqlSource, md.getSqlCommonType(),
				md.getParameterType(), null, returnType, keyGenerator, keyProperty, keyColumn);
	}

	private boolean hasMappedStatement(String mappedStatement) {
		return configuration.hasStatement(mappedStatement, false);
	}

	protected MappedStatement addMappedStatement(Class<?> mapperClass, String id, SqlSource sqlSource,
			SqlCommandType sqlCommandType, Class<?> parameterType, String resultMap, Class<?> resultType,
			KeyGenerator keyGenerator, String keyProperty, String keyColumn) {
		String statementName = mapperClass.getName() + StringPool.DOT + id;
		if (hasMappedStatement(statementName)) {
			logger.warn(StringPool.LEFT_SQ_BRACKET + statementName
					+ "] Has been loaded by XML or SqlProvider or Mybatis's Annotation, so ignoring this injection for ["
					+ getClass() + StringPool.RIGHT_SQ_BRACKET);
			return null;
		}
		/* 缓存逻辑处理 */
		boolean isSelect = false;
		if (sqlCommandType == SqlCommandType.SELECT) {
			isSelect = true;
		}
		return builderAssistant.addMappedStatement(id, sqlSource, StatementType.PREPARED, sqlCommandType, null, null,
				null, parameterType, resultMap, resultType, null, !isSelect, isSelect, false, keyGenerator, keyProperty,
				keyColumn, configuration.getDatabaseId(), languageDriver, null);
	}

}
