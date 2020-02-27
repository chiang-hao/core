package chianghao.core.mybatis;

import java.lang.reflect.Method;

import org.apache.ibatis.builder.annotation.MethodResolver;

public class MybatisMethodResolver extends MethodResolver {

	private final MybatisMapperAnnotationBuilder annotationBuilder;
	private final Method method;

	public MybatisMethodResolver(MybatisMapperAnnotationBuilder annotationBuilder, Method method) {
		super(annotationBuilder,method);
		this.annotationBuilder = annotationBuilder;
		this.method = method;
	}

	public void resolve() {
		annotationBuilder.parseStatement(method);
	}

}
