package haoframe.core.db.manage.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import haoframe.core.db.manage.AbstractDataMeta;
import haoframe.core.db.model.Column;
import haoframe.core.db.model.Table;
import haoframe.core.utils.StringUtils;

public class MysqlDataMeta extends AbstractDataMeta {

	
	Logger log = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * 获取删除字段的脚本
	 */
	@Override
	public List<String> getDelColumnCommands(String tableName, String columnName) {
		List<String> commands = new ArrayList<String>();
		commands.add(" ALTER TABLE " + tableName + " DROP " + columnName + ";");
		return commands;
	}

	@Override
	public List<String> createTableDDL(Table table) {
		StringBuffer sb = new StringBuffer(" create table `" + table.getTableName() + "` ( ");
		for (String key : table.getColumnMap().keySet()) {
			log.info("===========the column info is {}===={}======",key,table.getColumnMap().get(key));
			Column column = table.getColumnMap().get(key);
			String columnSql = getColumnSql(column);
			sb.append(columnSql + ",");
		}
		Set<String> colomnskeys = table.getPrimaryKeys();
		if (colomnskeys != null && colomnskeys.size() > 0) {
			String primarykeysql = "PRIMARY KEY(";
			for (String key : colomnskeys) {
				primarykeysql += key + ",";
			}
			if (primarykeysql.endsWith(",")) {
				primarykeysql = primarykeysql.substring(0, primarykeysql.lastIndexOf(","));
			}
			primarykeysql += ")";
			sb.append(primarykeysql);
		}

		String sql = sb.toString();
		if (sql.endsWith(",")) {
			sql = sql.substring(0, sql.lastIndexOf(","));
		}
		sql = sql + ");";
		return Arrays.asList(new String[] { sql });
	}

	@Override
	public List<String> alterTableDDL(Table table) {
		Table dbTable = this.getTableInfo(table.getTableName());
		List<String> sqlList = new ArrayList<String>();

		boolean isUpdatePrimary = false;
		Set<String> dbPrimaryKey    = new HashSet<String>();
		// 正向对比
		for (String fieldName : table.getColumnMap().keySet()) {
			Column column = table.getColumnMap().get(fieldName);
			if (dbTable.getColumnMap().containsKey(fieldName) && dbTable.getColumnMap().get(fieldName) != null) {
				String dbColumnName = StringUtils.firstCharLowercase(fieldName);
				Column dbColumn = dbTable.getColumnMap().get(dbColumnName);
				sqlList.addAll(getAlterColumnCommands(table.getTableName(), dbColumn, column));
				if(dbColumn.isPrimary()!=column.isPrimary()) {
					if(!isUpdatePrimary) {
						isUpdatePrimary = true;
					}
					if(dbColumn.isPrimary()) {
						dbPrimaryKey.add("`"+dbColumn.getName()+"`");
					}
				}else {
					if(dbColumn.isPrimary()) {
						dbPrimaryKey.add("`"+dbColumn.getName()+"`");
					}
				}
			} else {
				// 添加此字段
				sqlList.addAll(getAddColumnCommands(table.getTableName(), column));
				if(!isUpdatePrimary&&column.isPrimary()) {
					isUpdatePrimary = true;
				}
			}
		}
		// 反向比较删除字段
		for (String fieldName : dbTable.getColumnMap().keySet()) {
			if (!table.getColumnMap().containsKey(fieldName) || table.getColumnMap().get(fieldName) == null) {
				sqlList.addAll(getDelColumnCommands(table.getTableName(), dbTable.getColumnMap().get(fieldName).getName()));
			}
		}
		// 主键修改
		if(isUpdatePrimary) {
			if(dbPrimaryKey.size()>0) {
				sqlList.add("ALTER TABLE `" + table.getTableName() + "` DROP PRIMARY KEY ;");
			}
			if(table.getPrimaryKeys().size()>0) {
				StringBuffer sb = new StringBuffer();
				sb.append("ALTER TABLE `"+table.getTableName()+"` ADD PRIMARY KEY (");
				int index = 0;
				for (String key : table.getPrimaryKeys()) {
					if (index == 0) {
						sb.append("`" + key + "`");
					} else {
						sb.append(",`" + key + "`");
					}
					index++;
				}
				sb.append(");");
				sqlList.add(sb.toString());
			}
		}
		return sqlList;
	}

	@Override
	public String getColumnSql(Column column) {
		String dbtype = column.getType().getMysqlDBType();
		StringBuffer sb = new StringBuffer();
		sb.append(" `" + column.getName() + "` ");
		sb.append(" " + dbtype);
		if (column.getLength() == 0 && column.getPrecision() > 0) {
			sb.append("(10," + column.getPrecision() + ") ");
		}
		if (column.getLength() > 0 && column.getPrecision() == 0) {
			sb.append("(" + column.getLength() + ") ");
		}
		if (column.getLength() > 0 && column.getPrecision() > 0) {
			sb.append("(" + column.getLength() + "," + column.getPrecision() + ") ");
		}
		sb.append(column.isNull() == false ? " not null" : " null ");
		sb.append(column.getRemark() == null ? "" : " COMMENT '" + column.getRemark() + "'");
		return sb.toString();
	}

	@Override
	public List<String> getAlterColumnCommands(String tableName, Column dbColumn, Column column) {
		List<String> commands = new ArrayList<String>();
		// 比较名称是否一样
		if (!dbColumn.getName().equals(column.getName())) {
			commands.add("ALTER TABLE " + tableName + " RENAME COLUMN `" + dbColumn.getName() + "` TO `" + column.getName()+"`; ");
		}
		
		boolean isModify = false;
		// 比较类型
		if (dbColumn.getType().getCode() != column.getType().getCode()) {
			log.info("--------------------比较类型不一致{},{}--------------------",dbColumn.getType(),column.getType());
			commands.add("alter table " + tableName + " modify " + this.getColumnSql(column) + ";");
			isModify = true;
		}
		//比较长度
		if(!isModify&&dbColumn.getLength()!=column.getLength()) {
			log.info("--------------------比较长度不一致{},{}--------------------",dbColumn.getLength(),column.getLength());
			commands.add("alter table " + tableName + " modify " + this.getColumnSql(column) + ";");
			isModify = true;
		}
		//比较精度
		if(!isModify&&dbColumn.getPrecision()!=column.getPrecision()) {
			log.info("--------------------比较精度不一致{},{}--------------------",dbColumn.getPrecision(),column.getPrecision());
			commands.add("alter table " + tableName + " modify " + this.getColumnSql(column) + ";");
			isModify = true;
		}
		// 比较备注
		if (!isModify&&!dbColumn.getRemark().equals(column.getRemark())) {
			log.info("--------------------比较备注不一致{},{}--------------------",dbColumn.getRemark(),column.getRemark());
			commands.add("alter table " + tableName + " modify " + this.getColumnSql(column) + ";");
		}
		// 比较默认值
		String dbcolumndefaultvalue = dbColumn.getDefaultValue() == null ? "" : dbColumn.getDefaultValue();
		String columndefaultvalue = column.getDefaultValue() == null ? "" : column.getDefaultValue();
		if (!dbcolumndefaultvalue.equals(columndefaultvalue)) {
			if (!StringUtils.isEmpty(dbcolumndefaultvalue)) {
				commands.add("alter table " + tableName + " alter column `" + column.getName() + "` drop default;");
			}
			if (!StringUtils.isEmpty(columndefaultvalue)) {
				commands.add("alter table " + tableName + " alter column `" + column.getName() + "` set default '"+ columndefaultvalue + "';");
			}
		}
		return commands;
	}

	@Override
	public List<String> getAddColumnCommands(String tableName, Column column) {
		List<String> sqlList = new ArrayList<String>();
		sqlList.add("alter table " + tableName + " add  " + this.getColumnSql(column) + ";");
		return sqlList;
	}

}
