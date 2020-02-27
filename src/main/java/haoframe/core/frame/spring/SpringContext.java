package haoframe.core.frame.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


public class SpringContext implements ApplicationContextAware{

    private static ApplicationContext applicationContext = null;

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		applicationContext = context;
	}
	
	public static Object getBean(Class<?> clazz) {
		return applicationContext.getBean(clazz);
	} 
	
	public static Object getBean(String name) {
		return applicationContext.getBean(name);
	}
	
}
