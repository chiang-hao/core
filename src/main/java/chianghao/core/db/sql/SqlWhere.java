package chianghao.core.db.sql;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlWhere {

	public enum SqlOperators {
		greater, greater_equals, less, less_equals, in, not_in, between, not_between, like, // "%?%"
		front_like, // "%?"
		behind_like, // "?%"
		equals, not_equals
	}

	private String front;
	private String behind;
	private SqlOperators operators;
	private Object[] values;
	private String column;

	public SqlWhere(String column, SqlOperators operators, Object value) {
		this(column, operators, new Object[] { value });
	}

	public SqlWhere(String column, SqlOperators operators, Object[] values) {
		this(null, column, operators, values, "and");
	}

	public SqlWhere(String column, SqlOperators operators, Object[] values, String behind) {
		this(null, column, operators, values, behind);
	}

	public SqlWhere(String front, String column, SqlOperators operators, Object[] values, String behind) {
		this.front = front;
		this.column = column;
		this.operators = operators;
		this.values = values;
		this.behind = behind;
	}

	public String getFront() {
		return front;
	}

	public void setFront(String front) {
		this.front = front;
	}

	public String getBehind() {
		return behind;
	}

	public void setBehind(String behind) {
		this.behind = behind;
	}

	public SqlOperators getOperators() {
		return operators;
	}

	public void setOperators(SqlOperators operators) {
		this.operators = operators;
	}

	public Object[] getValues() {
		return values;
	}

	public void setValues(Object[] values) {
		this.values = values;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	Logger log = LoggerFactory.getLogger(this.getClass());

	public Sql getSql() {
		StringBuffer sb = new StringBuffer();
		if (StringUtils.isNotEmpty(this.front)) {
			sb.append(" " + this.front);
		}
		if (StringUtils.isNotEmpty(this.column)) {
			sb.append(" " + this.column);
		}
		List<Object> args = new ArrayList<Object>();
		switch (this.operators) {
		case in:
			sb.append(" in( ");
			for (int j = 0; j < values.length; j++) {
				if (j == 0) {
					sb.append("?");
				} else {
					sb.append(",?");
				}
				args.add(this.values[j]);
			}
			sb.append(") ");
			break;
		case not_in:
			sb.append(" not in( ");
			for (int j = 0; j < values.length; j++) {
				if (j == 0) {
					sb.append("?");
				} else {
					sb.append(",?");
				}
				args.add(this.values[j]);
			}
			sb.append(") ");
			break;
		case between:
			sb.append(" between ? and ? ");
			args.add(this.values[0]);
			args.add(this.values[1]);
			break;
		case not_between:
			sb.append(" not between ? and ? ");
			args.add(this.values[0]);
			args.add(this.values[1]);
			break;
		case like:
			sb.append(" like concat('%',?,'%') ");
			args.add(this.values[0]);
			break;
		case front_like:
			sb.append(" like concat('%',?) ");
			args.add(this.values[0]);
			break;
		case behind_like:
			sb.append(" like concat(?,'%') ");
			args.add(this.values[0]);
			break;
		case equals:
			sb.append(" = ? ");
			args.add(this.values[0]);
			break;
		case not_equals:
			sb.append(" != ? ");
			args.add(this.values[0]);
			break;
		case greater:
			sb.append(" > ? ");
			args.add(this.values[0]);
			break;
		case greater_equals:
			sb.append(" >= ? ");
			args.add(this.values[0]);
			break;
		case less:
			sb.append(" < ? ");
			args.add(this.values[0]);
			break;
		case less_equals:
			sb.append(" <= ? ");
			args.add(this.values[0]);
			break;
		default:
			log.error("未找到对应的逻辑操作条件,你设置的sql信息如下{}", this);
			break;
		}
		if (StringUtils.isNotEmpty(this.behind)) {
			sb.append(" " + this.behind + " ");
		}
		return new Sql(sb.toString(), args);
	}

}
