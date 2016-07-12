package com.open.im.utils;

import android.text.format.Time;

/**
 * 获取当前时间的工具类
 * 
 * @author Administrator
 * 
 */
public class MyDateUtils {
//	/**
//	 * 获取当前时间 精确到秒
//	 * */
//	public static String getCurrentDateToSecond() {
//		Date currDate = new Date();
//		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(currDate);
//	}

	/**
	 * 当前年 不是今天
	 * @param when
	 * @return
	 */
	public static boolean isThisYear(long when) {
		Time time = new Time();
		time.set(when);

		int thenYear = time.year;
//		int thenMonth = time.month;
		int thenMonthDay = time.monthDay;

		time.set(System.currentTimeMillis());
		return (thenYear == time.year) && (thenMonthDay != time.monthDay);
	}
}
