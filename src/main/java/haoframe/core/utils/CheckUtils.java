package haoframe.core.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 检查工具类
 */
public class CheckUtils {

	static Logger logger = LoggerFactory.getLogger(CheckUtils.class);

	private CheckUtils() {
	}

	/**
	 * 校验集合是NULL还是空集合
	 * 
	 * @param list :需要校验的集合
	 * @return true:null或者空集合,false:有值的集合
	 */
	public static boolean NullOrEmpty(@SuppressWarnings("rawtypes") List list) {
		if (null != list && !list.isEmpty()) {
			return false;
		} else {
			return true;
		}
	}

	/***
	 * 校验字符串对象是null，是""，是值为"NULL"
	 * @param str :需要校验的字符串
	 * @return true:NULL或者""或值为"NULL",false:字符串
	 */
	public static boolean NullOrEmpty(String str) {
		if (null == str || "".equals(str) || "NULL".equalsIgnoreCase(str)) {
			return true;
		} else {
			return false;
		}
	}

	/***
	 * 校验对象数组是否是NULL
	 * 
	 * @param str :需要校验的对象
	 * @return true:NULL,false:对象不为空
	 */
	public static boolean NullOrEmpty(Object[] obj) {
		if (null != obj && obj.length > 0) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 检查字符串是否是浮点数
	 * 
	 * @param str :校验对象
	 * @return true:是,false:不是
	 */
	public static boolean isMoney(String str) {
		try {
			@SuppressWarnings("unused")
			Double s = new Double(str);

			return true;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		}
	}

	/**
	 * 检查字符串是否全是中文
	 * 
	 * @param str :校验对象
	 * @return true:是,false:不是
	 */
	public static boolean ischinese(String str) {
		Pattern p = Pattern.compile("[\u4E00-\u9FA5]");
		Matcher m = p.matcher(str);
		return m.matches();
	}

	/**
	 * 校验中英混合字符长度(中文字符长度*3)
	 * 
	 * @param str  :字符
	 * @param size :最大长度
	 * @return true:通过,false:未通过
	 */
	public static boolean eqlength(String str, long size) {
		long tl = 0;
		for (int itemp = 0; itemp < str.length(); itemp++) {
			if (ischinese(str.substring(itemp, itemp + 1))) {
				tl += 3;
			} else {
				tl += 1;
			}
		}
		if (tl > size) {
			return false;
		}
		return true;
	}

	/**
	 * 校验中英混合字符长度(中文字符长度*3)
	 * 
	 * @param str    :字符
	 * @param size   :最大长度
	 * @param isNull :是否校验为空
	 * @return true:通过,false:未通过
	 */
	public static boolean eqlength(String str, long size, boolean isNull) {
		long tl = 0;

		// 是否校验为空
		if (isNull && CheckUtils.NullOrEmpty(str)) {
			return false;
		}

		for (int itemp = 0; null != str && itemp < str.length(); itemp++) {
			if (ischinese(str.substring(itemp, itemp + 1))) {
				tl += 3;
			} else {
				tl += 1;
			}
		}
		if (tl > size) {
			return false;
		}
		return true;
	}

	/**
	 * 检查字符串是否全是数字
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNum(String str) {
		return str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
	}

	/**
	 * 校验字符串是否满足时间格式  hh:mm:ss
	 * 
	 * @param time
	 * @return
	 */
	public static boolean checkHHMMSS(String time) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
		try {
			dateFormat.parse(time);
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			return false;
		}
		return true;
	}

	/**
	 * 校验字符串是否满足时间格式  yyyy-MM-dd
	 * 
	 * @param time
	 * @return
	 */
	public static boolean checkYYYYMMDD(String time) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		try {
			dateFormat.parse(time);
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			return false;
		}
		return true;
	}

	/**
	 *校验字符串是否满足时间格式  HH:MM（精确）
	 * 
	 * @param time
	 * @return
	 */
	public static boolean checkTime(String time) {
		if (checkHHMMSS(time)) {
			String[] temp = time.split(":");
			if ((temp[0].length() == 2 || temp[0].length() == 1) && temp[1].length() == 2 && temp[2].length() == 2) {
				int h, m, s;
				try {
					h = Integer.parseInt(temp[0]);
					m = Integer.parseInt(temp[1]);
					s = Integer.parseInt(temp[2]);
				} catch (NumberFormatException e) {
					return false;
				}
				boolean isValidHour = h >= 0 && h <= 24;
				boolean isValidMin = m <= 60 && m >= 0;
				boolean isValidSec = s <= 60 && s >= 0;
				if (isValidHour && isValidMin && isValidSec) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 检查文本文件编码是否是UTF-8
	 * 
	 * @param file
	 * @return
	 */
	public static boolean checkTxT(File file) {

		try {
			java.io.InputStream ios = new java.io.FileInputStream(file);
			byte[] b = new byte[3];

			ios.read(b);

			ios.close();

			if (b[0] == -17 && b[1] == -69 && b[2] == -65) {
				return true;
			} else {
				return false;
			}

		} catch (Exception e) {

			e.printStackTrace();
			return false;
		}

	}
	
}
