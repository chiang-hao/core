package chianghao.core.generator.clazz;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import chianghao.core.utils.StringUtils;

/**
 *   动态创建类 创建和动态加载类到jvm中
 * @author Administrator
 *
 */
public class GeneratorClass {

	/**
	 * 属性类型枚举
	 * @author Administrator
	 *
	 */
	public enum PropertyDataType{
		INT("int","java.lang.Integer","Integer"),
		STRING("string","java.lang.String","String"),
		DOUBULE("double","java.lang.Double","Double"),
		DECIMAL("decimal","java.math.BigDecimal","BigDecimal");
		
		private PropertyDataType(String name,String className,String simpleClassName) {
			this.className       = className;
			this.name            = name;
			this.simpleClassName = simpleClassName;
		}
		private String name;
		private String className;
		private String simpleClassName;
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getClassName() {
			return className;
		}
		public void setClassName(String className) {
			this.className = className;
		}
		public String getSimpleClassName() {
			return simpleClassName;
		}
		public void setSimpleClassName(String simpleClassName) {
			this.simpleClassName = simpleClassName;
		}
		
	}
		
	/**
	 * 属性模型
	 * @author Administrator
	 *
	 */
	public class PropertyModel{
		private String propertyName;
		private PropertyDataType dataType;
		public String getPropertyName() {
			return propertyName;
		}
		public void setPropertyName(String propertyName) {
			this.propertyName = propertyName;
		}
		public PropertyDataType getDataType() {
			return dataType;
		}
		public void setDataType(PropertyDataType dataType) {
			this.dataType = dataType;
		}
	}
	
	public GeneratorClass() {
		propertys = new ArrayList<PropertyModel>();
	}
	private List<PropertyModel> propertys;
	
	public void addProperty(String name,PropertyDataType type) {
		PropertyModel p = new PropertyModel();
		p.setDataType(type);
		p.setPropertyName(name);
		propertys.add(p);
	}
	
	/**
	 * 动态编译和类加载
	 * @param packageName
	 * @param className
	 * @return
	 */
	@SuppressWarnings("resource")
	public Class<?> create(String packageName,String className) {
		//遍历元素知道需要引用哪些类
		Set<String> importClassNameSet = new HashSet<String>();	
		for(PropertyModel pModel:propertys) {
			importClassNameSet.add(pModel.getDataType().getClassName());
		}
		importClassNameSet.add("java.io.Serializable");
		
		
		StringBuffer classContent = new StringBuffer();
		//写package
		classContent.append("package "+packageName+";");
		classContent.append("\n");
		//写引入类
		for(String importClassName:importClassNameSet) {
			classContent.append("import "+importClassName+";");
			classContent.append("\n");
		}
		classContent.append("\n");
		//写类头
		classContent.append("public class "+className+" implements Serializable {");
		classContent.append("\n");
		
		//实现序列化
		classContent.append("private static final long serialVersionUID = 1L;");
		classContent.append("\n");
		//写元素及set/get方法
		for(PropertyModel property:propertys) {
			classContent.append("private "+property.getDataType().getSimpleClassName()+" "+property.getPropertyName()+";");
			String getMethodName = "get"+StringUtils.firstCharUpperCase(property.getPropertyName());
			String setMethodName = "set"+StringUtils.firstCharUpperCase(property.getPropertyName());
			classContent.append("\n");
			//set 方法
			classContent.append("public void "+setMethodName+"("+property.getDataType().getSimpleClassName()+" "+property.getPropertyName()+"){");
			classContent.append("\n");
			classContent.append("this."+property.getPropertyName()+"="+property.getPropertyName()+";");
			classContent.append("\n");
			classContent.append("}");
			classContent.append("\n");
			//get 方法
			classContent.append("public "+property.getDataType().getSimpleClassName()+" "+getMethodName+"(){");
			classContent.append("\n");
			classContent.append("return this."+property.getPropertyName()+";");
			classContent.append("\n");
			classContent.append("}");
			classContent.append("\n");
		}
		classContent.append("}");
		
		String UserHome = System.getProperty("user.home");
		File userHomeFile = new File(UserHome);
		File workSpaceFile = new File(userHomeFile,".classloadworkspace");
		if(!workSpaceFile.exists()) {
			workSpaceFile.mkdirs();
		}
		
		File javafiledir = new File(workSpaceFile,"java");
		if(!javafiledir.exists()) {
			javafiledir.mkdirs();
		}	
		
//		System.out.println(System.getProperty("user.dir"));
//		System.out.println(System.getProperty("java.home"));
//		String filePath = System.getProperty("java.class.path");
//		System.out.println(filePath);
//		String pathSplit = System.getProperty("path.separator");//得到当前操作系统的分隔符，windows下是";",linux下是":"
//		String classesPath="";
//		if(filePath.contains(pathSplit)){
//			classesPath = filePath.substring(0,filePath.indexOf(pathSplit));
//		}else if (filePath.endsWith(".jar")) {//截取路径中的jar包名,可执行jar包运行的结果里包含".jar"
//			classesPath = filePath.substring(0, filePath.lastIndexOf(File.separator) + 1);
//		}
//		String classesPath=Thread.currentThread().getContextClassLoader().getResource("").getPath().substring(1);
//		System.out.println(classesPath);
		
		//不同打包方式的classpath不同，这里统一往java home 的classes下面写入。缺陷是为做到类隔离。
		String classesPath=System.getProperty("java.home")+File.separator+"classes";
		System.out.println("class动态编译路径为"+classesPath);
		File   classesfiledir = new File(classesPath);
		if(!classesfiledir.exists()) {
			classesfiledir.mkdirs();
		}
		////////////////////////////////////////////////////////////////////////////
		////////////////////////动态编译依赖于tools.jar。如果javahome中没有此jar/////
		////////////////////////拷贝到Java home lib中去/////////////////////////////                                                
		////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////
		try {
			//将文件写入到目录
			File javafile = new File(javafiledir,className+".java");
			if(!javafile.exists()){
				 javafile.createNewFile();
			}
			FileWriter fileWritter = new FileWriter(javafile);
			fileWritter.write(classContent.toString());
			fileWritter.flush();
			fileWritter.close();
			
			//获取动态编译器
			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			compiler.run(null, null, null, "-d",classesfiledir.getPath(),javafile.getPath());
		}catch(Throwable e) {
			e.printStackTrace();
			return null;
		}
		try {
			URLClassLoader urlClassLoader=new URLClassLoader(new URL[]{classesfiledir.toURI().toURL()});
			Class<?> tagClass = urlClassLoader.loadClass(packageName+"."+className);
		    return tagClass;
//			GenClassLoader genClassLoader = new GenClassLoader(classesfiledir.getPath());
//			Class<?> tagClass = genClassLoader.loadClass(packageName+"."+className);
//			System.out.println(tagClass.getClassLoader());
//			return tagClass;
		}catch(Throwable e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void main(String[] args) {
		GeneratorClass  generatorClass = new GeneratorClass();
		generatorClass.addProperty("name",PropertyDataType.STRING);
		generatorClass.addProperty("count",PropertyDataType.DECIMAL);
		Class<?> clazz = generatorClass.create("com.chianghao.generator","UserInfo");
		for(Field f:clazz.getDeclaredFields()) {
			System.out.println(f.getName());
		}
	}
	
}
