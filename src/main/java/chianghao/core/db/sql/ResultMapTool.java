package chianghao.core.db.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chianghao.core.db.model.Column;
import chianghao.core.db.model.Table;
import chianghao.core.exception.HaoException;
import chianghao.core.utils.ClassUtils;


/**
 * 结果映射工具
 * @author chianghao
 *
 */
public class ResultMapTool {

	static Logger log = LoggerFactory.getLogger(ResultMapTool.class);
	
	@SuppressWarnings("unchecked")
	public static <T> List<T> getList(ResultSet rs, Class<?> clazz) {
		Table table = Table.getTable(clazz);
		LinkedList<T> list = new LinkedList<T>();
		try {
			while (rs.next()) {
				T bean = (T) clazz.newInstance();
				for(String fieldName:table.getColumnMap().keySet()) {
					Column column = table.getColumnInfo(fieldName);
					try {
						Object value  = column.getValue(rs);
						ClassUtils.setFieldValue(bean, fieldName, value);
					}catch(Exception e1) {
						log.error("数据查询字段映射出错！fieldName {}",column.getName());
					}
				}
				list.addLast(bean);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			throw new HaoException("Mapper_Result_Error",e.getMessage());
		}
	}

	public static List<Object[]> getListArray(ResultSet rs) {
		try {
			int count = rs.getMetaData().getColumnCount();
			List<Object[]> list = new ArrayList<Object[]>();
			while (rs.next()) {
				Object[] row = new Object[count];
				for(int i=1;i<=count;i++) {
					row[(i-1)] =  rs.getObject(i);
				}
				list.add(row);
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new HaoException("Mapper_Result_Error",e.getMessage());
		}
	}

}
