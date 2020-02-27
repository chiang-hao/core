package chianghao.core.frame.spring;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;


/**
 * 系统的property的配置信息，通过spring 的配置加载
 * @author Administrator
 *
 */
public class ConfigProperty extends PropertyPlaceholderConfigurer {

	private static Map<String, String> ctxPropertiesMap;

	@Override
	protected void processProperties(ConfigurableListableBeanFactory beanFactory,Properties props)throws BeansException {
		super.processProperties(beanFactory, props);
		ctxPropertiesMap = new HashMap<String, String>();
		for (Object key : props.keySet()) {
			String keyStr = key.toString();
			String value = props.getProperty(keyStr);
			ctxPropertiesMap.put(keyStr, value);
		}
	}
	/***
	 * 获取property中key对应值
	 * @param name
	 * @return
	 */
	public static String getValue(String name) {
		return ctxPropertiesMap.get(name);
	}
}