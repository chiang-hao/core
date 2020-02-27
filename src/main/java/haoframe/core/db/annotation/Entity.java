package haoframe.core.db.annotation;



import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
@Target({ElementType.TYPE})   //用于字段，方法，参数
@Retention(RetentionPolicy.RUNTIME) //在运行时加载到Annotation到JVM中

/***
 * table标签
 * @author chianghao
 */
public @interface Entity{
	/***
	 * 表备注
	 * @return
	 */
	String title();
	
	/**
	    *      表名
	 * @return
	 */
	String table() default "";
}
