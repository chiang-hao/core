package chianghao.core.web;

import java.util.HashMap;
import java.util.Map;

public class RequestContext {

	
	private static ThreadLocal<Map<String,Object>> info = new ThreadLocal<Map<String,Object>>();
	
	
	private static Map<String,Object> getInfo() {
		if(info.get()==null) {
			return new HashMap<String,Object>();
		}
		return info.get();
	}
	
	
	/**
	 * 设置用户编号
	 * @param userid
	 */
	public static void setUserId(String userid) {
		Map<String,Object> map = getInfo();
		map.put("UserId", userid);
		info.set(map);
	}
	
	/**
	 * 获取用户编号
	 * @return
	 */
	public static String getUserId() {
		Object userid = getInfo().get("UserId");
		return (userid==null?null:(String)userid);
	}
	
	
}
