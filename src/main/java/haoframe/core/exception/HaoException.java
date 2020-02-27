package haoframe.core.exception;


public class HaoException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String code;
	
	public HaoException(String code,String msg) {
		super(msg);
		this.code = code;
	}

	public HaoException(int code,String msg) {
		super(msg);
		this.code = code+"";
	}
	
	public HaoException(ErrorInfo message) {
		this(message.getCode(),message.getMessage());
	}
	
	public HaoException(String message) {
		this("999999",message);
	}
	public HaoException(ErrorInfo message,String detail) {
		this(message.getCode(),message.getMessage()+"["+detail+"]");
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	
	
}
