package haoframe.core.utils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * java 类对象工具
 * @author chianghao
 *
 */
public class ClassUtils {

	private static Logger LOG = LoggerFactory.getLogger(ClassUtils.class);
	
	
	public static void getFields(Class<?> tagClass, Map<String, Field> fieldMap) {
		if(tagClass.getName().equals("java.lang.Object")) {
			return;
		}
		getFields(tagClass.getSuperclass(),fieldMap);
		for(Field f:tagClass.getDeclaredFields()) {
			fieldMap.put(f.getName(),f);
		}
	}

	public static void setFieldValue(Object bean, String fieldName, Object value) {
		Map<String,Field> fields = new HashMap<String,Field>();
		getFields(bean.getClass(),fields);
		Field field = fields.get(fieldName);
		if(field==null) {
			return;
		}
		field.setAccessible(true);
		try {
			field.set(bean, value);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	public static Object getFieldValue(Object bean, Field field) {
		field.setAccessible(true);
		try {
			return field.get(bean);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public static Field getField(Class<?> clazz,String fieldName) {
		if(clazz.getName().equals("java.lang.Object")) {
			return null;
		}
		Field field = null;
		try {
			field = clazz.getDeclaredField(fieldName);
		} catch (Exception e) {
			//e.printStackTrace();
		}
		if(field==null) {
			return getField(clazz.getSuperclass(),fieldName);
		}else {
			return field;
		}
	}
	
	public static Object getFieldValue(Object bean, String fieldName) {
		Field field=getField(bean.getClass(),fieldName);
		if(field==null) {
			return null;
		}
		return getFieldValue(bean,field);
	}
	
	
	public static Map<String, Object> transBean2Map(Object obj) {  
        if(obj == null){  
            return new HashMap<String,Object>();  
        }          
        Map<String, Object> map = new HashMap<String, Object>();  
        try {  
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());  
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();  
            for (PropertyDescriptor property : propertyDescriptors) {  
                String key = property.getName();  
                // 过滤class属性  
                if (!"class".equals(key)) {  
                    // 得到property对应的getter方法  
                    Method getter = property.getReadMethod();  
                    Object value = getter.invoke(obj);  
                    map.put(key, value);  
                }  
            } 
        } catch (Exception e) {  
            LOG.error("transBean2Map error",e);
        }  
        return map;  
    }  
	
	public static Class<?> getSuperClassParameterizedType(Object obj){
		return getSuperClassParameterizedType(obj,0);
	}
	
	@SuppressWarnings("rawtypes")
	public static Class getSuperClassParameterizedType(Object obj,int index){
		Type type = obj.getClass().getGenericSuperclass();
		if(type instanceof ParameterizedType){
			ParameterizedType p = (ParameterizedType)type;
			Type types[] = p.getActualTypeArguments();
			if(index<0 || index>=types.length){
				throw new RuntimeException(obj.getClass().getName()+"的父类的参数化类型下标在0-"+(types.length-1)+"范围内");
			}
			if (!(types[index] instanceof Class)) {    
				return Object.class;    
			} 
			return (Class)types[index];
			
		}
		return null;
	}
	
	
//	@SuppressWarnings("unchecked")
//	public static <T> List<T> copyList(List<?> list,Class<?> tagClass){
//		if(list!=null&&list.size()>0) {
//			try {
//				List<T> tagList = new ArrayList<T>();
//				for(Object obj:list) {
//					T bean = (T) tagClass.newInstance();
//					BeanUtils.copyProperties(obj, bean);
//					tagList.add(bean);
//				}
//				return tagList;
//			} catch (InstantiationException e) {
//				e.printStackTrace();
//			} catch (IllegalAccessException e) {
//				e.printStackTrace();
//			}
//		}
//		return null;
//	}
	
//	static class A{
//		String name;
//
//		public A() {}
//		
//		public A(String name) {
//			this.name = name;
//        }
//		
//		public String getName() {
//			return name;
//		}
//
//		public void setName(String name) {
//			this.name = name;
//		}
//		
//	}
//	
//	static class B{
//		String name;
//
//		public String getName() {
//			return name;
//		}
//
//		public void setName(String name) {
//			this.name = name;
//		}
//		
//	}
//	
//	public static void main(String[] args) {
//		List<A>  list = new ArrayList<A>();
//		list.add(new A("AA"));
//		list.add(new A("BB"));
//		list.add(new A("CC"));
//		
//		List<B> blist = copyList(list,B.class);
//		for(B b:blist) {
//			System.out.println(b.getName());
//		}
//	}
}
