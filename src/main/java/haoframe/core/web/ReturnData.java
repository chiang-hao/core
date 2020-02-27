package haoframe.core.web;

import java.io.Serializable;

import haoframe.core.exception.HaoException;

public class ReturnData<T> implements Serializable{
	
//	private final static Integer success_state = 1;
//	private final static Integer error_state = 0;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int     status;
	private Error   error;
	private T       data;
	
	public static <T> ReturnData<Object> getError(Throwable e) {
		ReturnData<Object> r = new ReturnData<Object>();
		r.setStatus(500);
		Error error = new Error();
		String eCode = "999999";
		String eMsg  = e.getMessage();
		if(e instanceof HaoException) {
			eCode = ((HaoException) e).getCode();
		}
		error.setCode(eCode);
		error.setMessage(eMsg);
		r.setError(error);
		return r;
	}
	
	public static <T> ReturnData<Object> getError(String eCode,String eMsg) {
		ReturnData<Object> r = new ReturnData<Object>();
		r.setStatus(500);
		Error error = new Error();
		error.setCode(eCode);
		error.setMessage(eMsg);
		r.setError(error);
		return r;
	}
	
	public static <T> ReturnData<Object> getSuccess() {
		ReturnData<Object> r = new ReturnData<Object>();
		r.setStatus(200);
		return r;
	}
	
	public static <T> ReturnData<T> getSuccess(T data) {
		ReturnData<T> r = new ReturnData<T>();
		r.setStatus(200);
		r.setData(data);
		return r;
	}
	

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Error getError() {
		return error;
	}

	public void setError(Error error) {
		this.error = error;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public static class Error  implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
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
	
	
}
