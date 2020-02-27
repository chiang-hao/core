package chianghao.core.db.manage;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

import chianghao.core.db.annotation.Entity;
import chianghao.core.db.model.Table;
import chianghao.core.exception.HaoException;
import chianghao.core.utils.PackageUtil;


public class DatabaseTool {

	
	private static final Logger log = LoggerFactory.getLogger(DatabaseTool.class);

	/**
	 * 初始化db
	 * @param packages 包名。扫描包下被Entity注解的类
	 */
	public static void init(String...packages) {
		for(String p:packages) {
			Set<Class<?>> classes = PackageUtil.findAnnotationClass(p,Entity.class);
			for(Class<?> clazz:classes) {
				Table.putTable(new Table(clazz));
			}
//			Set<String> classes = PackageUtil.findAnnotationClass(p,Entity.class);
//			for(String className:classes) {
//				Class<?> clazz;
//				try {
//					clazz = Class.forName(className);
//					Table.putTable(new Table(clazz));
//				} catch (ClassNotFoundException e) {
//					e.printStackTrace();
//				}
//			}
		}
	}
	
	public static void createTable(String dataSourceName){
		Map<String,Table> eMap = Table.getTableMap();
		for(String key:eMap.keySet()) {
			Table table= eMap.get(key);
			log.info("====================操作表{}============================",table.getTableName());
			try {
				AbstractDataMeta dm = AbstractDataMeta.createDataMeta(DBType.mysql,dataSourceName);
				List<String> sqlCommands = dm.getDDLSql(table);
				Collections.sort(sqlCommands,new Comparator<String>() {
					@Override
					public int compare(String arg0, String arg1) {
						boolean isdrop1  = arg0.contains("DROP");
						boolean isdrop2  = arg1.contains("DROP");
						if(isdrop1&&isdrop2) {
							return 1;
						}else if(isdrop2&&!isdrop2) {
							return 0;
						}else if(!isdrop2&&isdrop2) {
							return -1;
						}else {
							return -1;
						}
					}
					
				});
				log.info(JSON.toJSONString(sqlCommands));
				try {
					dm.executeBatch(sqlCommands);
				} catch (HaoException e1) {
					e1.printStackTrace();
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
}
