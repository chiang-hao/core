# mybatis 增强

概述：
此增强支持将curd注入到mybatis的mapper中。使用者定义的mapper无需添加对数据库的表的基本写操作。增强插件的BaseMapper提供的
insert,insertBatch,
update,updateByCode
delete,deleteByCode
queryOne,
queryList
queryPageList等基本操作


1、将定义的数据库实体初始化

2、配置spring
  spring配置mybatis 时将SqlSessionFactoryBean  替换成	MyBatisSqlSessionFactoryBean

3、配置mybatis的分页插件
  chianghao.core.mybatis.plugins.PaginationInterceptor
4、注意
     目前只支持mysql数据库

5、使用
	使用者的接口继承BaseMapper方法