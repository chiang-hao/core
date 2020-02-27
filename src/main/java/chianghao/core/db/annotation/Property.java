package chianghao.core.db.annotation;


/***
 * 默认就是数据库字典的
 */
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
@Target({ElementType.FIELD})   //用于字段，方法，参数
@Retention(RetentionPolicy.RUNTIME) //在运行时加载到Annotation到JVM中

/***
 * 表字段属性
 * @author chianghao
 */
public @interface Property{
	/***
                   * 成员标题
     * @return
     */
	String title() ;
	
	//数据库字段
	String columnName() default "";
	
	/***
	 * 备注
	 * @return
	 */
	String remark() default "";
    /***
	 * 成员长度
	 * @return
	 */
	int length() default 0;
	/***
	 * 成员精度
	 * @return
	 */
	int precision() default 0;
	/***
	 * 数据库字段是否主键
	 * @return
	 */
	boolean primary() default false;
	
	/**
	 *是否允许为空，默认不允许
	 * @return
	 */
	boolean isNull() default false;
	
	/**
	 * 默认值
	 * @return
	 */
	String defaultValue() default "";
	
}
