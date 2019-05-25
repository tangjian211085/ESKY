package com.puhui.lib.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.telephony.TelephonyManager;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 字符串操作工具包
 */
@SuppressLint("SimpleDateFormat")
public class StringUtils {

    private final static ThreadLocal<SimpleDateFormat> dateFormater = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    private final static ThreadLocal<SimpleDateFormat> dateFormater2 = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };

    /**
     * 获取打码后字符
     *
     * @param str
     * @return
     */
    public static String getMosaic(String str) {
        if (StringUtils.isEmptyOrNull(str) || str.length() <= 2) {
            return str;
        }
        return str.substring(0, 1) + "**" + str.substring(str.length() - 1);
    }

    /**
     * 判断是否包含Emoji表情
     *
     * @param str
     * @return
     */
    public static boolean containsEmoji(String str) {
        int len = str.length();
        for (int i = 0; i < len; i++) {
            if (isEmojiCharacter(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    private static boolean isEmojiCharacter(char codePoint) {
        return !((codePoint == 0x0) ||
                (codePoint == 0x9) ||
                (codePoint == 0xA) ||
                (codePoint == 0xD) ||
                ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) ||
                ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF)));
    }

    /**
     * 返回当前系统时间
     */
    public static String getDataTime(String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(new Date());
    }

    /**
     * 返回当前系统时间
     */
    public static String getDataTime() {
        return getDataTime("HH:mm");
    }

    /**
     * 毫秒值转换为mm:ss
     *
     * @param ms
     * @author kymjs
     */
    public static String timeFormat(int ms) {
        StringBuilder time = new StringBuilder();
        time.delete(0, time.length());
        ms /= 1000;
        int s = ms % 60;
        int min = ms / 60;
        if (min < 10) {
            time.append(0);
        }
        time.append(min).append(":");
        if (s < 10) {
            time.append(0);
        }
        time.append(s);
        return time.toString();
    }

    /**
     * 将字符串转位日期类型
     *
     * @return
     */
    public static Date toDate(String sdate) {
        try {
            return dateFormater.get().parse(sdate);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 判断给定字符串时间是否为今日
     *
     * @param sdate
     * @return boolean
     */
    public static boolean isToday(String sdate) {
        boolean b = false;
        Date time = toDate(sdate);
        Date today = new Date();
        if (time != null) {
            String nowDate = dateFormater2.get().format(today);
            String timeDate = dateFormater2.get().format(time);
            if (nowDate.equals(timeDate)) {
                b = true;
            }
        }
        return b;
    }

    /**
     * 判断给定字符串是否空白串 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
     */
    public static boolean isEmpty(String input) {
        return input == null || "".equals(input) || input.trim().equals("");
    }

    /***
     * 判断字符串是否null，或""，或"null"
     *
     * @param source
     * @return
     */
    public static boolean isEmptyOrNull(String source) {
        return source == null || "".equals(source) || "null".equals(source);
    }

    public static String[] split(String source, String subStr) {
        return source.split(subStr);
    }

    /**
     * 字符串转整数
     *
     * @param str
     * @param defValue
     * @return
     */
    public static int toInt(String str, int defValue) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
        }
        return defValue;
    }

    /**
     * String转long
     *
     * @param obj
     * @return 转换异常返回 0
     */
    public static long toLong(String obj) {
        try {
            return Long.parseLong(obj);
        } catch (Exception e) {
        }
        return 0;
    }

    /**
     * String转double
     *
     * @param obj
     * @return 转换异常返回 0
     */
    public static double toDouble(String obj) {
        try {
            return Double.parseDouble(obj);
        } catch (Exception e) {
        }
        return 0D;
    }

    /**
     * 字符串转布尔
     *
     * @param b
     * @return 转换异常返回 false
     */
    public static boolean toBool(String b) {
        try {
            return Boolean.parseBoolean(b);
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 判断一个字符串是不是数字
     */
    public static boolean isNumber(String str) {
        try {
            Integer.parseInt(str);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 获取AppKey
     */
    public static String getMetaValue(Context context, String metaKey) {
        Bundle metaData = null;
        String apiKey = null;
        if (context == null || metaKey == null) {
            return null;
        }
        try {
            ApplicationInfo ai =
                    context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (null != ai) {
                metaData = ai.metaData;
            }
            if (null != metaData) {
                apiKey = metaData.getString(metaKey);
            }
        } catch (NameNotFoundException e) {

        }
        return apiKey;
    }

    /**
     * 获取手机IMEI码
     */
    public static String getPhoneIMEI(Activity aty) {
        TelephonyManager tm = (TelephonyManager) aty.getSystemService(Activity.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }

    /**
     * MD5加密
     */
    public static String md5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

    /**
     * KJ加密
     */
    public static String KJencrypt(String str) {
        char[] cstr = str.toCharArray();
        StringBuilder hex = new StringBuilder();
        for (char c : cstr) {
            hex.append((char) (c + 5));
        }
        return hex.toString();
    }

    /**
     * KJ解密
     */
    public static String KJdecipher(String str) {
        char[] cstr = str.toCharArray();
        StringBuilder hex = new StringBuilder();
        for (char c : cstr) {
            hex.append((char) (c - 5));
        }
        return hex.toString();
    }

    /**
     * 数组转String，用逗号分隔
     *
     * @param values
     * @return
     */
    public static String arrayToString(String[] values) {
        if (null == values) return "";

        StringBuffer buffer = new StringBuffer(values.length);
        for (int i = 0; i < values.length; i++) {
            buffer.append(values[i]).append(",");
        }
        if (buffer.length() > 0) {
            return buffer.toString().substring(0, buffer.length() - 1);
        }
        return "";
    }

    /**
     * 将银行卡，或身份证，或手机号用空格分割
     *
     * @param source
     * @return
     */
    public static String withSpace(String source) {
        if (null != source) {
            source = source.replace(" ", "");
        }
        if (isEmpty(source) || source.length() > 19) {
            return source;
        }

        char[] items = source.toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < items.length; i++) {
            stringBuilder.append(items[i]);
            if ((i + 1) % 4 == 0) {
                stringBuilder.append(" ");
            }
        }

        return stringBuilder.toString().trim();
    }

    /**
     * 替换手机号中间4位为*
     *
     * @param phone
     * @return
     */
    public static String getFormatPhone(String phone) {
        return phone.replaceAll("(\\d{3})(\\d{4})", "$1****");
    }

    /**
     * 格式化银行卡号
     *
     * @param bankCardNumber 银行卡号
     * @return
     */
    public static String getFormatBankCard(String bankCardNumber) {
        if (isEmptyOrNull(bankCardNumber) || (bankCardNumber.length() != 16 && bankCardNumber.length() != 19)) {
            return bankCardNumber;
        }

        if (bankCardNumber.length() == 16) {
            return bankCardNumber.replaceAll(bankCardNumber.substring(4, 12), " **** **** ");
        } else {
            return bankCardNumber.replaceAll(bankCardNumber.substring(4, 16), " **** **** **** ");
        }
    }

    /**
     * 替换身份证中间10位为*
     *
     * @param idCardNumber 身份证号码
     * @return
     */
    public static String getFormatIdCard(String idCardNumber) {
        if (isEmptyOrNull(idCardNumber) || idCardNumber.length() < 18) {
            return idCardNumber;
        }
        return idCardNumber.replaceAll(idCardNumber.substring(4, 14), "**********");
    }

    /**
     * @param s the IP string
     * @return All possible valid IP addresses
     */
    public static boolean isValid(String s) {
        if (s.charAt(0) == '0') return s.equals("0");
        int num = Integer.parseInt(s);
        return num <= 255 && num > 0;
    }

    /**
     * 将数字字符串转换为IP地址
     *
     * @param s
     * @return
     */
    public static String restoreIpAddresses(String s) {
        // Write your code here
        //判断长度以及是否纯数字
        if (s.length() < 4 || s.length() > 12 || !s.matches("^\\d+$")) {
            return s;
        }

//        s = "3232258049";  //1995738560  fourth = 192;    3232258049  first = 192

        ArrayList<String> res = new ArrayList<String>();
        long sourceIp = Long.parseLong(s);
        long first = sourceIp / 16777216L;
        long second = (sourceIp - first * 16777216L) / 65536L;
        long third = (sourceIp - first * 16777216L - second * 65536L) / 256;
        long fourth = sourceIp - first * 16777216L - second * 65536L - third * 256;

        String ipAddress = "";
        if (first == 192) {
            ipAddress = first + "." + second + "." + third + "." + fourth;
        } else if (fourth == 192) {
            ipAddress = fourth + "." + third + "." + second + "." + first;
        } else {
            ipAddress = s;
        }

        return ipAddress;
    }

}