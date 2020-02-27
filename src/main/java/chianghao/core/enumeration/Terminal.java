package chianghao.core.enumeration;

public enum Terminal {

	PC_WEB,
	IOS,
	ANDROID,
	WAP,
	WECHAT;
	
	public static Terminal getByName(String name) {
		for(Terminal ty:Terminal.values()) {
			if(ty.toString().equals(name)) {
				return ty;
			}
		}
		return null;
	}
	
}
