package haoframe.core.frame.spring;

import java.beans.Introspector;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.util.ClassUtils;

/***
 * 自动生成bean名称的策略
 * @author Administrator
 */
public class DefaultAnnotationBeanNameGenerator  extends AnnotationBeanNameGenerator {

	@Override
	protected String buildDefaultBeanName(BeanDefinition definition) {
		String shortClassName = ClassUtils.getShortName(definition.getBeanClassName());
		String packageName=ClassUtils.getPackageName(definition.getBeanClassName());
		return Introspector.decapitalize(packageName+"."+shortClassName);
	}
	
	

}
