package com.puhui.lib.utils;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


@SuppressLint("SimpleDateFormat")
public class TimeUtils {
    public static final SimpleDateFormat DATE_FORMAT_DATE = new SimpleDateFormat("yyyy-MM-dd");

    public static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

    public static final SimpleDateFormat DATE_FORMAT_DATE2 = new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);

    public static final SimpleDateFormat DATE_FORMAT_DATE3 = new SimpleDateFormat("yyyy.MM.dd", Locale.CHINA);

    public static final SimpleDateFormat DATE_FORMAT_DATE4 = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.CHINA);

//    public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
//    public static final SimpleDateFormat TIME_FORMAT2 = new SimpleDateFormat("HH:mm", Locale.CHINA);
//    public static final SimpleDateFormat MINUTE_TIME_FORMAT = new SimpleDateFormat("HH:mm", Locale.CHINA);
//    public static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("MM/dd HH:mm:ss", Locale.CHINA);
//    public static final SimpleDateFormat DATE_TIME_FORMAT2 = new SimpleDateFormat("MM/dd HH:mm", Locale.CHINA);

    public static String getTime(long timeMillis) {
        return getTime(timeMillis, DEFAULT_DATE_FORMAT);
    }

    /**
     * 长整型数字转日期, 返回字符串形式的日期
     *
     * @param simpleDateFormat date
     */
    public static String getTime(long timeMillis, SimpleDateFormat simpleDateFormat) {
        return simpleDateFormat.format(new Date(timeMillis));
    }

    /***
     * 根据时间戳获取月与号  如2017.05.17  得到  5.17
     */
    public static String getMonthAndDay(long timeMillis, SimpleDateFormat simpleDateFormat) {
        try {
            String date = simpleDateFormat.format(new Date(timeMillis));
            date = date.substring(date.indexOf(".") + 1);
            if (date.startsWith("0")) {
                date = date.substring(1);
            }
            return date;
        } catch (Exception e) {
            e.printStackTrace();
            return timeMillis + "";
        }
    }

    public static Date stringToDate(String date, SimpleDateFormat simpleDateFormat) {
        Date localObject = null;
        try {
            localObject = simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return localObject;
    }

    public static String stringToDateDetail(Date date) {
        String localObject = "";
        try {
            localObject = DATE_FORMAT_DATE.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return localObject;
    }

    /***
     * 将String格式的日期转化成long类型的
     */
    public static long string2LongTime2(String sourceTime) {
        if (!StringUtils.isEmpty(sourceTime))
            return stringToDateTime(sourceTime).getTime();
        return 0;
    }

    public static String stringToDateDetail2(Date date) {
        String localObject = "";
        try {
            localObject = DATE_FORMAT_DATE2.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return localObject;
    }

    public static String stringToDateTime(String date, SimpleDateFormat format) {
        try {
            return format.format(format.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
            return date;
        }
    }

    public static Date stringToDateTime(String date) {
        Date localObject = null;
        try {
            localObject = DEFAULT_DATE_FORMAT.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return localObject;
    }

    /**
     * @param mss 要转换的毫秒数
     * @return 该毫秒数转换为 * days * hours * minutes * seconds 后的格式
     */
    public static String formatDuring(long mss) {
        long days = mss / (1000 * 60 * 60 * 24);
        long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (mss % (1000 * 60)) / 1000;
        StringBuilder sb = new StringBuilder("");
        if (days > 0) {
            sb.append(days).append("天");
        }
        //		if (hours > 0)
        //		{
        sb.append(hours).append("小时");
        //		}
        if (minutes > 0) {
            sb.append(minutes).append("分");
        }
        sb.append(seconds).append("秒");
        return sb.toString();
    }

    /***
     * 获取当前日期的前几天或者后几天日期
     *
     * @param intervalDays 时间间隔，可为负数
     * @param dataFormat   yyyy.MM.dd 或者 yyyy-MM-dd 或 其他
     * @return
     */
    public static String getDateBeforeOrAfter(int intervalDays, String dataFormat) {
//        Date date = new Date();
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
//        DMLog.e(TimeUtils.class.getSimpleName(),
//                intervalDays + " days later is : " + df.format(new Date(date.getTime() + (long) intervalDays * 24 * 60 * 60 * 1000)));

        Calendar calendar = Calendar.getInstance();//使用默认时区和语言环境获得一个日历。
//        cal.add(Calendar.DAY_OF_MONTH, -1);//取当前日期的前一天.
        calendar.add(Calendar.DAY_OF_MONTH, intervalDays);
        SimpleDateFormat format;
        try {
            //通过格式化输出日期
            format = new SimpleDateFormat(dataFormat, Locale.CHINA);
            DMLog.e(TimeUtils.class.getSimpleName(), intervalDays + " days later is : " + format.format(calendar.getTime()));
        } catch (Exception e) {
            format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            e.printStackTrace();
        }
        return format.format(calendar.getTime());
    }

    /**
     * 获取当前日期的时间戳  年月日
     */
    public static long getYearMonthDayTime() {
        String yearMonthDay = getTime(System.currentTimeMillis(), DATE_FORMAT_DATE);
        return getYearMonthDayTime(yearMonthDay);
    }

    /**
     * 根据年月日获取它的时间戳
     * yearMonthDay 获取当前日期的时间戳  年-月-日   DATE_FORMAT_DATE
     */
    public static long getYearMonthDayTime(String yearMonthDay) {
        return getYearMonthDayTime(yearMonthDay, DATE_FORMAT_DATE);
    }

    /**
     * 根据年月日获取它的时间戳
     * yearMonthDay 获取当前日期的时间戳
     */
    public static long getYearMonthDayTime(String yearMonthDay, SimpleDateFormat dateFormat) {
        try {
            Date date = dateFormat.parse(yearMonthDay);
            return date.getTime();
        } catch (Exception e) {
            return System.currentTimeMillis();
        }
    }
}