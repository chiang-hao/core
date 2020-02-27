package haoframe.core.generator;

import java.io.File;
import java.io.FileWriter;

import haoframe.core.db.model.Column;
import haoframe.core.db.model.Table;
import haoframe.core.utils.StringUtils;

/**
 * 输出Java bean对象
 * @author chianghao
 *
 */
public class CodingDao {

	private String     filePath;
	private Table      tableInfo;
	private String     packageInfo;
	
	/**
	 * 
	 * @param filePath      写路径
	 * @param tableInfo     表信息
	 * @param packageInfo   包名
	 */
	public CodingDao(String filePath, Table tableInfo,String packageInfo) {
		this.filePath    = filePath;
		this.tableInfo   = tableInfo;
		this.packageInfo = packageInfo;
	}

	
	public String getPackageInfo() {
		return packageInfo;
	}
	public void setPackageInfo(String packageInfo) {
		this.packageInfo = packageInfo;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public Table getTableInfo() {
		return tableInfo;
	}
	public void setTableInfo(Table tableInfo) {
		this.tableInfo = tableInfo;
	}
	
	public void write() {
		File outWriteDir = new File(this.filePath);
		if(!outWriteDir.exists()) {
			outWriteDir.mkdirs();
		}
		
		File FileDaoDir = new File(outWriteDir,tableInfo.getTableName());
		if(!FileDaoDir.exists()) {
			FileDaoDir.mkdirs();
		}
		
		//拼xml
		StringBuffer xmlsb = new StringBuffer();
		xmlsb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
				"<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n"+
				"<mapper namespace=\""+packageInfo+"."+tableInfo.getTableName()+"."+StringUtils.underlineToCamel(tableInfo.getTableName(), false)+"Dao\" >\n"+
				"");
		xmlsb.append("<resultMap id=\""+tableInfo.getClazz().getSimpleName()+"Map\" type=\""+tableInfo.getClazz().getName()+"\" >\r\n" );
				  
	    for(String key:this.tableInfo.getColumnMap().keySet()) {
	    	    Column c= this.tableInfo.getColumnMap().get(key);
				xmlsb.append(" <result column=\""+c.getName()+"\" property=\""+c.getFieldName()+"\" />\r\n");
		}
		xmlsb.append(" </resultMap>\r\n");
		
		
		xmlsb.append(" </mapper>");
		//拼dao
		StringBuffer daosb = new StringBuffer();
		daosb.append("package "+packageInfo+"."+tableInfo.getTableName()+";\n");
		daosb.append("public interface "+tableInfo.getClazz().getSimpleName()+"Dao{ \n" );
		
		daosb.append("}" );
		
		try {
			//写xml
			File file =new File(FileDaoDir,StringUtils.underlineToCamel(tableInfo.getTableName(),false)+"DaoMapper.xml");
	        if(!file.exists()){
	        	file.createNewFile();
	        }
			FileWriter fileWritter = new FileWriter(file);
			fileWritter.write(xmlsb.toString());
			fileWritter.flush();
			fileWritter.close();
			
			//写dao
			File daoFile =new File(FileDaoDir,StringUtils.underlineToCamel(tableInfo.getTableName(),false)+"Dao.java");
			if(!daoFile.exists()){
				daoFile.createNewFile();
			}
			FileWriter daoFileWritter = new FileWriter(daoFile);
			daoFileWritter.write(daosb.toString());
			daoFileWritter.flush();
			daoFileWritter.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
