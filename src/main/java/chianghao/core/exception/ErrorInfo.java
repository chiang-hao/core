package chianghao.core.exception;

public enum ErrorInfo {
	
	build_sql_error("build_sql_error","构建SQL语句错误"),
	unauthorized("unauthorized", "未认证"),
	unauthorization("unauthorization", "未授权"),
	missed_param("missed_param", "参数空"),
	find_more_url("find_more_url","匹配到多条地址")
	;

	private ErrorInfo(String code, String message) {
		this.code = code;
		this.message = message;
	}

	private String code;
	private String message;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}


