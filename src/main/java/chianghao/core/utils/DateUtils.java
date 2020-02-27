package chianghao.core.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日期处理单元
 */
public class DateUtils {
	static Logger LOG = LoggerFactory.getLogger(DateUtils.class);
	public static final int SECOND = 1000;
	public static final int MINUTE = SECOND * 60;
	public static final int HOUR = MINUTE * 60;
	public static final long DAY = (long) HOUR * 24;
	public static final long WEEK = DAY * 7;
	public static final long YEAR = DAY * 356;

	public static final String FULL_ST_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String FULL_J_FORMAT = "yyyy/MM/dd HH:mm:ss";
	public static final String CURRENWIT_ST_FORMAT = "yyyy-MM-dd HH:mm";
	public static final String CURRENWIT_J_FORMAT = "yyyy/MM/dd HH:mm";
	public static final String DATA_FORMAT = "yyyyMMddHHmmss";
	public static final String ST_CN_FORMAT = "yyyy年MM月dd日 HH点mm分ss秒";
	public static final String CN_FORMAT = "yy年MM月dd日HH:mm";
	public static final String MD_FORMAT = "mm月dd日";
	public static final String DAY_FORMAT = "yyyy-MM-dd";
	public static final String DAY_FORMAT1 = "yyyy/MM/dd";
	public static final String DAY_FORMAT2 = "yyyyMMdd";
	public static final String DAY_FORMAT3 = "yyyy.MM.dd";
	public static final String SHORT_DATE_FORMAT = "yy-MM-dd";
	public static final String YEAR_FORMAT = "yyyy";
	public static final String HOUR_MINUTE = "HH:mm";
	public static final String FULL_HS_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
	public static final String TIME_ZONE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssX";

	private DateUtils() {
	}

	/**
	 * 获取当前时间错
	 * @return
	 */
	public static long getNewTimeStamp() {
		return System.currentTimeMillis();
	}
	
	public static Date getNewDate() {
		return new Date();
	}
	
	private static Calendar cal = Calendar.getInstance();

	public static String getDate(long second) {
		SimpleDateFormat sdf = new SimpleDateFormat(FULL_ST_FORMAT);
		return sdf.format(new Date(second));
	}

	public static String getHourMinute(Date dateTime) {
		SimpleDateFormat format1 = new SimpleDateFormat(HOUR_MINUTE);
		String dt1 = format1.format(dateTime);
		return dt1;
	}

	public static String[] getLatestMonth() {
		SimpleDateFormat sdf = new SimpleDateFormat(DAY_FORMAT);
		Date now = new Date();
		String startDate = sdf.format(now);

		Calendar cal = Calendar.getInstance();
		cal.setTime(now);
		cal.add(Calendar.DATE, -30);

		String endDate = sdf.format(cal.getTime());

		String[] ret = new String[2];
		ret[0] = endDate;
		ret[1] = startDate;
		return ret;
	}

	public static String[] getMonth12() {
		SimpleDateFormat sdf = new SimpleDateFormat(DAY_FORMAT);
		Date now = new Date();
		String startDate = sdf.format(now);

		Calendar cal = Calendar.getInstance();
		cal.setTime(now);
		cal.add(Calendar.MONTH, -12);
		String endDate = sdf.format(cal.getTime());

		try {
			startDate = getMaxMonthDate(startDate);
			endDate = getMinMonthDate(endDate);
		} catch (ParseException e) {
			LOG.error("DateUtil.getMonth12=>", e);
		}

		String[] ret = new String[2];
		ret[0] = endDate;
		ret[1] = startDate;

		return ret;
	}

	public static String getMaxMonthDate(String date) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(DAY_FORMAT);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(sdf.parse(date));
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		return sdf.format(calendar.getTime());
	}

	/**
	 * 得到月初日期
	 * 
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static String getMinMonthDate(String date) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(DAY_FORMAT);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(sdf.parse(date));
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
		return sdf.format(calendar.getTime());
	}

	public static String getNow() {
		SimpleDateFormat sdf = new SimpleDateFormat(FULL_ST_FORMAT);
		return sdf.format(new Date());
	}

	public static String getNow2() {
		SimpleDateFormat sdf = new SimpleDateFormat(ST_CN_FORMAT);
		return sdf.format(new Date());
	}

	public static String getNow3() {
		SimpleDateFormat sdf = new SimpleDateFormat(FULL_HS_FORMAT);
		return sdf.format(new Date());
	}

	public static String getDayFormatNow() {
		SimpleDateFormat sdf = new SimpleDateFormat(DAY_FORMAT);
		return sdf.format(new Date());
	}

	public static String getDayFormatNow2() {
		SimpleDateFormat sdf = new SimpleDateFormat(DAY_FORMAT2);
		return sdf.format(new Date());
	}

	public static String getShortDate() {
		SimpleDateFormat sdf = new SimpleDateFormat(DAY_FORMAT);
		return sdf.format(new Date());
	}

	public static String getMDDate() {
		SimpleDateFormat sdf = new SimpleDateFormat(MD_FORMAT);
		return sdf.format(new Date());
	}

	/**
	 * 获取DAY_FORMAT格式的系统当前时间
	 * 
	 * @return
	 */
	public static String getDayFromatByDate(Date dateTime) {
		SimpleDateFormat format1 = new SimpleDateFormat(DAY_FORMAT);
		String dt1 = format1.format(dateTime);
		return dt1;
	}

	/**
	 * 获取"yyyy-MM-dd HH:mm:ss"格式的系统当前时间
	 * 
	 * @return
	 */
	public static String getDayFromatByDateTime(Date dateTime) {
		SimpleDateFormat format1 = new SimpleDateFormat(DATA_FORMAT);
		String dt1 = format1.format(dateTime);
		return dt1;
	}

	public static String getDayFromatByDate3(Date dateTime) {
		SimpleDateFormat format1 = new SimpleDateFormat(DAY_FORMAT3);
		String dt1 = format1.format(dateTime);
		return dt1;
	}

	public static Date string2Date(String date) {
		SimpleDateFormat formatter = new SimpleDateFormat(DAY_FORMAT);
		Date currentTime = null;
		try {
			currentTime = formatter.parse(date);
		} catch (Exception e) {
			LOG.error("DateUtil日期转换时参数异常：=>", e);

		}
		return currentTime;
	}

	public static Date string2Date2(String date) {
		SimpleDateFormat formatter = new SimpleDateFormat(DAY_FORMAT2);
		Date currentTime = null;
		try {
			currentTime = formatter.parse(date);
		} catch (Exception e) {
			LOG.error("DateUtil日期转换时参数异常：=>", e);
		}
		return currentTime;
	}

	public static String string2Date3(String date) {
		SimpleDateFormat sdf = new SimpleDateFormat(DATA_FORMAT);

		String result = null;
		try {
			result = sdf.format(sdf.parse(date));
		} catch (ParseException e) {
			LOG.error("DateUtil日期转换时参数异常：=>", e);
		}
		return result;
	}

	/**
	 * 将string时间转化成格式 ：如 2016-5-12 --> 2016-05-12
	 * 
	 * @param date
	 * @return
	 */
	public static String string2DayFormat(String dateStr) {
		SimpleDateFormat sdf = new SimpleDateFormat(DAY_FORMAT);

		String result = null;
		try {
			result = sdf.format(sdf.parse(dateStr));
		} catch (ParseException e) {
			LOG.error("DateUtil日期转换时参数异常：=>", e);
		}
		return result;
	}

	/**
	 * 获取"yyyyMMdd"格式的系统当前时间
	 * 
	 * @return
	 */
	public static String getDayFromatByDate2(Date dateTime) {
		SimpleDateFormat format1 = new SimpleDateFormat(DAY_FORMAT2);
		String dt1 = format1.format(dateTime);
		return dt1;
	}

	public static String getDateStrByDate(Date dateTime) {
		SimpleDateFormat format1 = new SimpleDateFormat(FULL_ST_FORMAT);
		String dt1 = format1.format(dateTime);
		return dt1;
	}

	public static String getDateStrByDate2(Date dateTime) {
		SimpleDateFormat format1 = new SimpleDateFormat(ST_CN_FORMAT);
		String dt1 = format1.format(dateTime);
		return dt1;
	}

	/**
	 * 获取指定格式时间
	 * 
	 * @return 返回时间类型 FULL_ST_FORMAT
	 */
	public static Date stringToDate(String time) {
		SimpleDateFormat formatter = new SimpleDateFormat(FULL_ST_FORMAT);
		Date currentTime = new Date();
		try {
			currentTime = formatter.parse(time);
		} catch (Exception e) {
			LOG.error("DateUtil日期转换时参数异常：=>", e);
		}
		return currentTime;
	}

	/**
	 * 取得日期后几年的日期,offset为年数偏移量
	 * 
	 * @param date
	 * @param offset
	 * @return
	 */
	public static Date getLaterYear(Date date, int offset) {
		cal.setTime(date);
		cal.add(Calendar.YEAR, offset);
		return cal.getTime();
	}

	/**
	 * 取得日期后几月的日期,offset为月数偏移量
	 * 
	 * @param date
	 * @param offset
	 * @return
	 */
	public static Date getLaterMonth(Date date, int offset) {
		cal.setTime(date);
		cal.add(Calendar.MONTH, offset);
		return cal.getTime();
	}

	/**
	 * 取得日期后几天的日期,offset为天数偏移量
	 * 
	 * @param date
	 * @param offset
	 * @return
	 */
	public static Date getLaterDate(Date date, int offset) {
		cal.setTime(date);
		cal.add(Calendar.DATE, offset);
		return cal.getTime();
	}

	public static void main(String[] args) {
		System.out.println(getLaterDate(new Date(), 3));
	}

	/**
	 * 取得几秒种前后的时间,offset为分种偏移量
	 * 
	 * @param date
	 * @param offset
	 * @return
	 */
	public static Date getLaterSeconds(Date date, int offset) {
		cal.setTime(date);
		cal.add(Calendar.SECOND, offset);
		return cal.getTime();
	}

	/**
	 * 取得几分种前后的时间,offset为分种偏移量
	 * 
	 * @param date
	 * @param offset
	 * @return
	 */
	public static Date getLaterTime(Date date, int offset) {
		cal.setTime(date);
		cal.add(Calendar.MINUTE, offset);
		return cal.getTime();
	}

	/**
	 * 求两个时间中间间隔的小时数
	 * 
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static long getQuotHour(Date start, Date end, boolean isAbs) {
		long quot = 0;
		try {
			quot = end.getTime() - start.getTime();
			quot = quot / 1000 / 60 / 60;
		} catch (Exception e) {
			LOG.error("DateUtil日期转换时参数异常：=>", e);

		}
		if (isAbs) {
			return Math.abs(quot);
		}
		return quot;
	}

	/**
	 * 求两个时间中间间隔的天数
	 * 
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static long getQuot(Date start, Date end) {
		long quot = 0;
		try {
			quot = end.getTime() - start.getTime();
			quot = quot / 1000 / 60 / 60 / 24;
		} catch (Exception e) {
			LOG.error("DateUtil日期转换时参数异常：=>", e);
		}
		return Math.abs(quot);
	}

	/**
	 * 求两个时间中间间隔的秒数
	 * 
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static long getQuotOfSecond(Date start, Date end) {
		long quot = 0;
		try {
			quot = end.getTime() - start.getTime();
			quot = quot / 1000;
		} catch (Exception e) {
			LOG.error("DateUtil日期转换时参数异常：=>", e);
		}
		return Math.abs(quot);
	}

	/**
	 * 获取当前日期是星期几<br>
	 * 
	 * @param dt
	 * @return 当前日期是星期几
	 */
	public static String getWeekOfDate(Date dt) {
		String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);
		int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (w < 0)
			w = 0;
		return weekDays[w];
	}

	/**
	 * 获取当前日期是星期几<br>
	 * 
	 * @param dt
	 * @return 当前日期是星期几
	 */
	public static int getWeekIntOfDate(Date dt) {
		int[] weekDays = { 7, 1, 2, 3, 4, 5, 6 };
		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);
		int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (w < 0)
			w = 0;
		return weekDays[w];
	}

	/**
	 * 求两个时间中间间隔的天数
	 * 
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static long getQuot(String time1, String time2) {
		long quot = 0;
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date1 = ft.parse(time1);
			Date date2 = ft.parse(time2);
			quot = date1.getTime() - date2.getTime();
			quot = quot / 1000 / 60 / 60 / 24;
		} catch (Exception e) {
			LOG.error("DateUtil日期转换时参数异常：=>", e);
		}
		return Math.abs(quot);
	}

	/**
	 * 求两个时间中间间隔的分钟
	 * 
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static long getQuotSeconds(String time1, String time2) {
		long quot = 0;
		SimpleDateFormat ft = new SimpleDateFormat(FULL_ST_FORMAT);
		try {
			Date date1 = ft.parse(time1);
			Date date2 = ft.parse(time2);
			quot = date1.getTime() - date2.getTime();
			quot = quot / 1000;
		} catch (Exception e) {
			LOG.error("DateUtil日期转换时参数异常：=>", e);
		}
		return Math.abs(quot);
	}

	public static String getTimeDifference(Date begin, Date end) throws Exception {
		long between = (end.getTime() - begin.getTime()) / 1000;// 除以1000是为了转换成秒
		String day1 = String.valueOf(between / (24 * 3600));
		String hour1 = String.valueOf(between % (24 * 3600) / 3600);
		String minute1 = String.valueOf(between % 3600 / 60);
		String second1 = String.valueOf(between % 60 / 60);
		if (Integer.parseInt(hour1) < 10)
			hour1 = "0" + hour1;
		if (Integer.parseInt(minute1) < 10)
			minute1 = "0" + minute1;
		if (Integer.parseInt(second1) < 10)
			second1 = "0" + second1;
		return day1 + "_" + hour1 + "_" + minute1 + "_" + second1;
	}

	/**
	 * 
	 * @Method: strToDatestr @Description: yyyyMMddHHmmss转yyyy-MM-dd
	 *          HH:mm:ss @param @param date @param @return @return String @throws
	 */
	public static String strToDatestr(String date) {
		SimpleDateFormat sdf = new SimpleDateFormat(DATA_FORMAT);
		SimpleDateFormat sdf2 = new SimpleDateFormat(FULL_ST_FORMAT);
		String result = null;
		try {
			result = sdf2.format(sdf.parse(date));
		} catch (ParseException e) {
			LOG.error("DateUtil日期转换时参数异常：=>", e);
		}
		return result;
	}

	// 获取上周开始和结束日
	public static String lastWeek() {

		Calendar calendar1 = Calendar.getInstance();
		Calendar calendar2 = Calendar.getInstance();
		int dayOfWeek = calendar1.get(Calendar.DAY_OF_WEEK) - 1;
		int offset1 = 1 - dayOfWeek;
		int offset2 = 7 - dayOfWeek;
		calendar1.add(Calendar.DATE, offset1 - 8);
		calendar2.add(Calendar.DATE, offset2 - 8);

		String beginDate = getDayFromatByDate(calendar1.getTime());
		String endDate = getDayFromatByDate(calendar2.getTime());
		return beginDate + "_" + endDate;
	}

	/**
	 * 
	 * @Method: getDateInterval @Description: 获取指定时间间隔 @param @param type
	 *          1:Calendar.YEAR ;2:Calendar.MONTH
	 *          6:Calendar.DAY_OF_YEAR @param @param num 时间间隔 @param @param format
	 *          日期格式，例yyyy-MM-dd @param @param sourceDate 源日期 @param @return
	 *          目标日期 @return String @throws
	 */
	public static String getDateInterval(int type, int num, String format, String sourceDate) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String reStr = "";
		try {
			Date dt = sdf.parse(sourceDate);
			Calendar rightNow = Calendar.getInstance();
			rightNow.setTime(dt);
			rightNow.add(type, num);// 日期减1年
			Date dt1 = rightNow.getTime();
			reStr = sdf.format(dt1);
		} catch (ParseException e) {
			LOG.error("DateUtil日期转换时参数异常：=>", e);
		}

		return reStr;
	}

	// GMT8时间戳转日期
	public static String stampGMT8ToDate(String unixDate) {
		SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		long unixLong = 0;
		String date;
		try {
			unixLong = Long.parseLong(unixDate) * 1000;
		} catch (Exception e) {
			LOG.error("String转换Long错误，请确认数据可以转换！", e);
		}

		date = fm.format(unixLong);

		return date;
	}

	/**
	 * 两个时间相差距离多少天多少小时多少分多少秒
	 * 
	 * @param str1 时间参数 1 格式：1990-01-01 12:00:00
	 * @param str2 时间参数 2 格式：2009-01-01 12:00:00
	 * @return long[] 返回值为：{天, 时, 分, 秒}
	 */
	public static long[] getDistanceTimes(String str1, String str2) {
		SimpleDateFormat df = new SimpleDateFormat(FULL_ST_FORMAT);
		Date one;
		Date two;
		long day = 0;
		long hour = 0;
		long min = 0;
		long sec = 0;
		try {
			one = df.parse(str1);
			two = df.parse(str2);
			long time1 = one.getTime();
			long time2 = two.getTime();
			long diff;
			if (time1 < time2) {
				diff = time2 - time1;
			} else {
				diff = time1 - time2;
			}
			day = diff / (24 * 60 * 60 * 1000);
			hour = diff / (60 * 60 * 1000) - day * 24;
			min = (diff / (60 * 1000)) - day * 24 * 60 - hour * 60;
			sec = diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60;
		} catch (ParseException e) {
			LOG.error("DateUtil日期转换时参数异常：=>", e);
		}
		long[] times = { day, hour, min, sec };
		return times;
	}

	/**
	 * 获取两个时间相隔天数 @Title: getDaysBetween @param @param startDate @param @param
	 * endDate @param @return @return Long @throws
	 */
	public static Long getDaysBetween(Date startDate, Date endDate) {
		Calendar fromCalendar = Calendar.getInstance();
		fromCalendar.setTime(startDate);
		fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
		fromCalendar.set(Calendar.MINUTE, 0);
		fromCalendar.set(Calendar.SECOND, 0);
		fromCalendar.set(Calendar.MILLISECOND, 0);

		Calendar toCalendar = Calendar.getInstance();
		toCalendar.setTime(endDate);
		toCalendar.set(Calendar.HOUR_OF_DAY, 0);
		toCalendar.set(Calendar.MINUTE, 0);
		toCalendar.set(Calendar.SECOND, 0);
		toCalendar.set(Calendar.MILLISECOND, 0);

		return (toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24);
	}

	public static long currentTimeSeconds() {
		long l = System.currentTimeMillis() / 1000;
		LOG.debug("currentTimeSeconds:" + l);
		return l;
	}

	public static Date dealDateFormat(String s) {
		SimpleDateFormat sd = new SimpleDateFormat(TIME_ZONE_FORMAT);
		Date date = new Date();
		try {
			date = sd.parse(s);
		} catch (ParseException e) {
			LOG.error("DateUtil转换{}时参数异常：=>{}", s, e.getMessage());
		}
		return date;
	}

}
