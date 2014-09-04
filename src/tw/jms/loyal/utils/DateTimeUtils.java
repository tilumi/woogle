package tw.jms.loyal.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DateTimeUtils {

	public static String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
	public static String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static String COMPACT_DATE_TIME_FORMAT = "yyyyMMddHHmmss";

	public static SimpleDateFormat defaultDateFormat = new SimpleDateFormat(
			DEFAULT_DATE_FORMAT);

	public static long MINUTE_IN_MILLIS = 1000 * 60;

	public static long HOUR_IN_MILLIS = MINUTE_IN_MILLIS * 60;

	public static long DAY_IN_MILLIS = HOUR_IN_MILLIS * 24;

	public synchronized static String getDate(long millis) {
		return defaultDateFormat.format(new Date(millis));
	}

	public synchronized static String getDateTime(long millis) {
		return defaultDateTimeFormat.format(new Date(millis));
	}

	public static long getMillisFromDate(String dateString, String dateFormat) {
		try {
			dateString = removeTime(dateString);
			SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
			return sdf.parse(dateString).getTime();
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public static long getIntervalInDays(String startDate, String endDate) {
		long startDateInMillis = DateTimeUtils.getMillisFromDate(startDate);
		long endDateInMillis = DateTimeUtils.getMillisFromDate(endDate);
		return ((endDateInMillis - startDateInMillis) / DAY_IN_MILLIS) + 1;
	}


	private static SimpleDateFormat sdf = new SimpleDateFormat(
			DEFAULT_DATE_FORMAT);
	private static Map<String, Long> millisFromDateCache = new HashMap<String, Long>();

	public synchronized static long getMillisFromDate(String dateString) {
		try {
			if (dateString.length() != 10) {
				dateString = removeTime(dateString);
			}
			Long result = millisFromDateCache.get(dateString);
			if (result == null) {
				result = sdf.parse(dateString).getTime();
				millisFromDateCache.put(dateString, result);
			}
			return result;
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public static long getMillisFromDateTime(String dateTimeString) {
		try {
			return new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT).parse(
					dateTimeString).getTime();
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static long getMillisFromCompactDateTime(String dateTimeString) {
		try {
			return new SimpleDateFormat(COMPACT_DATE_TIME_FORMAT).parse(
					dateTimeString).getTime();
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public static long getMillisFromDateTime(String dateString,
			String timeString) {
		return getMillisFromDateTime(dateString + " " + timeString);
	}

	public static String formatDateTime(String dateTime, boolean hours,
			boolean minutes, boolean seconds, boolean millis) {
		long dateTimeInMillis = getMillisFromDateTime(dateTime);
		long formattedDateTimeInMillis = formatDateTime(dateTimeInMillis,
				hours, minutes, seconds, millis);
		return getDateTime(formattedDateTimeInMillis);
	}

	public static String getHourFromDateTime(String dateTimeString) {
		String result = null;
		try {
			int start = dateTimeString.indexOf(" ") + 1;
			int end = dateTimeString.indexOf(":");
			return dateTimeString.substring(start, end);
		} catch (Exception e) {
			// ignore errors
		}
		return result;
	}

	public static Integer getHourFromDateTimeAsInt(String dateTimeString) {
		long millis = getMillisFromDateTime(dateTimeString);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millis);
		return calendar.get(Calendar.HOUR_OF_DAY);
	}

	public static Integer getMinsFromDateTimeAsInt(String dateTimeString) {
		long millis = getMillisFromDateTime(dateTimeString);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millis);
		return calendar.get(Calendar.MINUTE);
	}

	public static String getMinsFromDateTime(long timestamp) {
		String dateTimeString = defaultDateTimeFormat.format(timestamp);
		try {
			String minutes = StringUtils.getNthToken(dateTimeString, 2, ":");
			return minutes;
		} catch (Exception e) {
			// ignore errors
		}
		return null;
	}

	public static int getDayFromDate(String date) {
		date = removeTime(date);
		return Integer.parseInt(date.substring(8, 10));
	}

	public static long formatDateTime(long timestamp, boolean hours,
			boolean minutes, boolean seconds, boolean millis) {
		Calendar cal = Calendar.getInstance(); // this is locale-specific
		cal.setTime(new Date(timestamp));
		if (!hours)
			cal.set(Calendar.HOUR_OF_DAY, 0);
		if (!minutes)
			cal.set(Calendar.MINUTE, 0);
		if (!seconds)
			cal.set(Calendar.SECOND, 0);
		if (!millis)
			cal.set(Calendar.MILLISECOND, 0);
		return cal.getTimeInMillis();
	}

	private static SimpleDateFormat defaultDateTimeFormat = new SimpleDateFormat(
			DEFAULT_DATE_TIME_FORMAT);

	public static String getDateTime_UTC8(Long timestamp) {
		if ((timestamp + "").length() > 10) {
			timestamp /= 1000;
		}
		long convertedTs = 28800 + timestamp; // plus 8 hours, utc
		return defaultDateTimeFormat.format(new Date(convertedTs * 1000));
	}

	public static long getMillis_UTC8(Long timestamp) {
		if ((timestamp + "").length() > 10) {
			timestamp /= 1000;
		}
		long convertedTs = 28800 + timestamp; // plus 8 hours, utc
		return convertedTs;
	}

	public static String removeTime(String dateStr) {
		int space = dateStr.indexOf(" ");
		if (space >= 0) {
			return dateStr.substring(0, space);
		}
		return dateStr;
	}

	public static int getDifferenceInDays(String dateStr1, String dateStr2) {
		if (dateStr1.length() > 10) {
			dateStr1 = removeTime(dateStr1);
		}
		if (dateStr2.length() > 10) {
			dateStr2 = removeTime(dateStr2);
		}
		long date1 = getMillisFromDate(dateStr1);
		long date2 = getMillisFromDate(dateStr2);
		return (int) ((date2 - date1) / DAY_IN_MILLIS);
	}

	public static String getRelativeDate(String date, int shiftDays) {
		return getDate(getMillisFromDate(date) + DAY_IN_MILLIS * shiftDays);
	}
	
	public static String getRelativeMonth(String date, int shiftMonths) {
		Calendar cal = Calendar.getInstance(); // this is locale-specific
		cal.setTimeInMillis(DateTimeUtils.getMillisFromDate(date));
		cal.add(Calendar.MONTH, shiftMonths);
		return DateTimeUtils.getDate(cal.getTimeInMillis());
	}

	public static String getRelativeDateTime(String dateTime, int shiftDays) {
		return getDateTime(getMillisFromDateTime(dateTime) + DAY_IN_MILLIS
				* shiftDays);
	}

	static Map<String, Integer> mapping = new HashMap<String, Integer>();
	static {
		mapping.put("Sun", 1);
		mapping.put("Mon", 2);
		mapping.put("Tue", 3);
		mapping.put("Wed", 4);
		mapping.put("Thu", 5);
		mapping.put("Fri", 6);
		mapping.put("Sat", 7);
	}

	private static SimpleDateFormat dayNumOfWeekDf = new SimpleDateFormat(
			"EEE", Locale.ENGLISH);

	public static int getDayNumOfWeek(String date) {
		int result = 0;
		try {
			date = removeTime(date);
			long millis = getMillisFromDate(date);
			Date d = new Date(millis);
			result = mapping.get(dayNumOfWeekDf.format(d));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return result;
	}

	public static String getFirstDateInMonth(String txDate) {
		return txDate.substring(0, 8) + "01";
	}

	public static String getPreviousDate(String txDate) {
		long millis = DateTimeUtils.getMillisFromDate(txDate);
		millis -= DateTimeUtils.DAY_IN_MILLIS;
		return DateTimeUtils.getDate(millis);
	}

	public static String getLastDateInMonth(String txDate) {
		String result = "";
		String firstDateInMonth = getFirstDateInMonth(txDate);
		long tmpMillis = DateTimeUtils.getMillisFromDate(firstDateInMonth);
		tmpMillis += 27 * DateTimeUtils.DAY_IN_MILLIS;
		String tmpDateTime = null;
		while (DateTimeUtils.isSameMonth(txDate,
				(tmpDateTime = DateTimeUtils.getDate(tmpMillis)))) {
			result = tmpDateTime;
			tmpMillis += DateTimeUtils.DAY_IN_MILLIS;
		}
		return result;
	}

	public static String getFirstDateInWeek(String txDate) {
		int dayNum = getDayNumOfWeek(txDate);
		String result = DateTimeUtils.getRelativeDate(txDate, 1 - dayNum);
		return result;
	}

	public static boolean isToday(String targetDate, String txDate) {
		if (targetDate.length() != 10) {
			targetDate = targetDate.substring(0, 10);
		}
		if (txDate.length() != 10) {
			txDate = txDate.substring(0, 10);
		}
		return targetDate.equals(txDate);
	}

	public static boolean isYesterday(String targetDate, String txDate) {
		int dif = getDifferenceInDays(targetDate, txDate);
		if (dif == 1) {
			return true;
		}
		return false;
	}

	public static boolean isPast3Days(String targetDate, String txDate) {
		int dif = getDifferenceInDays(targetDate, txDate);
		if (dif >= 1 && dif <= 3) {
			return true;
		}
		return false;
	}

	public static boolean isPast7Days(String targetDate, String txDate) {
		int dif = getDifferenceInDays(targetDate, txDate);
		if (dif >= 1 && dif <= 7) {
			return true;
		}
		return false;
	}

	public static boolean isPast30Days(String targetDate, String txDate) {
		int dif = getDifferenceInDays(targetDate, txDate);
		if (dif >= 1 && dif <= 30) {
			return true;
		}
		return false;
	}

	public static boolean isSameMonth(String targetDate, String txDate) {
		String dateStr1 = removeTime(targetDate);
		String dateStr2 = removeTime(txDate);
		dateStr1 = dateStr1.substring(0, 7);
		dateStr2 = dateStr2.substring(0, 7);
		return dateStr1.equals(dateStr2);
	}

	public static long getNearsetTimeThatMinDivisibleByN(long timestamp, int i) {
		try {
			String minsStr = getCeilingMinsFromDateTime(timestamp);
			int mins = Integer.parseInt(minsStr);
			while (mins % i != 0) {
				mins++;
			}
			Calendar cal = Calendar.getInstance(); // this is locale-specific
			cal.setTimeInMillis(timestamp);
			cal.set(Calendar.MINUTE, mins);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			return cal.getTimeInMillis();
		} catch (Exception e) {

		}
		return 0;
	}

	private static String getCeilingMinsFromDateTime(long timestamp) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timestamp);
		int seconds = calendar.get(Calendar.SECOND);
		int milliseconds = calendar.get(Calendar.MILLISECOND);
		int minutes = calendar.get(Calendar.MINUTE);
		if (milliseconds > 0 || seconds > 0) {
			minutes++;
		}
		return minutes + "";
	}

	public static String getDateFromDateTime(String dateTime) {
		return dateTime.substring(0, dateTime.indexOf(" "));
	}

	public static String getStartOfDay(String dateTime) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(getMillisFromDateTime(dateTime));
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return DateTimeUtils.getDateTime(c.getTimeInMillis());
	}

	public static String getEndOfDay(String dateTime) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(getMillisFromDateTime(dateTime));
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MILLISECOND, 999);
		return DateTimeUtils.getDateTime(c.getTimeInMillis());
	}

	
	public static long getNextHour(long timestamp) {
		timestamp += HOUR_IN_MILLIS;
		return timestamp;
	}

	public static int compareDate(String date1, String date2) {
		date1 = date1.substring(0, 10);
		date2 = date2.substring(0, 10);
		return date1.compareTo(date2);
	}

	public static long getHourIndex(long timestamp) {
		return (timestamp / (3600 * 1000));
	}

	public static long getDayIndex(long timestamp) {
		return (timestamp / (24 * 3600 * 1000));
	}
}
