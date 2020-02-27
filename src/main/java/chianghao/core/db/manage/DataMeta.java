package chianghao.core.db.manage;

import java.util.List;

import chianghao.core.db.model.Column;
import chianghao.core.db.model.Table;



public interface DataMeta {

	
	public List<String> createTableDDL(Table table);
	
	public List<String> alterTableDDL(Table table);
	
	public String getColumnSql(Column column);
	
	public List<String> getAlterColumnCommands(String tableName,Column oldAttribute,Column column);
	
	public List<String> getAddColumnCommands(String tableName,Column column);
	
	public List<String> getDelColumnCommands(String tableName,String columnName);
}
