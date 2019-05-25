package com.puhui.lib.utils;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 格式转换
 *
 * @author tangjian
 */
public class FormatUtil {

    /**
     * 格式化金额到千分位（取整）
     *
     * @param amount
     * @return
     */
    public static String numKbPointFormatFix(double amount) {
        DecimalFormat df = new DecimalFormat("##,####,###,###");
        amount = round(amount);
        return df.format(amount);
    }

    /**
     * 格式化金额到千分位
     *
     * @param amount
     * @return
     */
    public static String numKbPointFormat(double amount) {
        DecimalFormat df = new DecimalFormat("##,####,###,##0.00");
        amount = round(amount);
        return df.format(amount);
    }

    public static double round(double amount) {
        return (Math.round(amount * 100.0D) / 100.0D);
    }

    /**
     * 日期格式转换
     *
     * @param src
     * @param format
     * @return
     */
    public static String formatLongDate(String src, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.CHINA);
        Long dateTime = Long.valueOf(src);
        return sdf.format(new Date(dateTime));
    }

    /**
     * 转换成中国货币格式
     *
     * @param d
     * @return
     */
    public static String getCurrency(double d) {
        //使用本地默认格式输出货币值
        return getCurrency(d, Locale.CHINA);
    }

    /**
     * 转换成某地区货币格式
     *
     * @param d
     * @param locale
     * @return
     */
    public static String getCurrency(double d, Locale locale) {
        //使用本地默认格式输出货币值
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(locale);
        String result = currencyFormat.format(d);
        if (result.startsWith("-￥")) {
            //如果是负值java 默认格式 为-￥1000
            result = result.replace("-￥", "￥-");
        }
        return result;
    }

    /**
     * 转换成中国百分数格式
     *
     * @param d
     * @return
     */
    public static String getPercent(double d) {
        return getPercent(d, Locale.CHINA);
    }

    /**
     * 转换成某地百分数格式
     *
     * @param d
     * @param locale
     * @return
     */
    public static String getPercent(double d, Locale locale) {
        //使用本地默认格式输出货币值
        NumberFormat currencyFormat = NumberFormat.getPercentInstance(locale);
        //保留4位小数
        currencyFormat.setMaximumFractionDigits(4);
        return currencyFormat.format(d);
    }

    /**
     * 转换成形如23.12%的百分数格式
     *
     * @param d
     * @return
     */
    public static String getDMPercent(double d) {
        double temp = d * 100;
        return String.format("%.2f", temp) + "%";
    }

    /**
     * 转换成形如23.12%的百分数格式
     *
     * @param s
     * @return
     */
    public static String getDMPercent(String s) {
        double temp = StringUtils.toDouble(s) * 100;
        return String.format("%.2f", temp) + "%";
    }

    /**
     * 获取两位小数点的double
     */
    public static double get2Double(double a) {
        DecimalFormat df = new DecimalFormat("0.00");
        return new BigDecimal(df.format(a)).doubleValue();
    }

    /**
     * 获取两位小数点的double
     */
    public static double get3Double(double a) {
        DecimalFormat df = new DecimalFormat("0.000");
        return new BigDecimal(df.format(a)).doubleValue();
    }

    /**
     * 获取两位小数点的String
     */
    public static String get2String(double a) {
        DecimalFormat df = new DecimalFormat("#0.00");
        return df.format(a);
    }

    /**
     * 获取三位小数点的String
     */
    public static String get3String(double a) {
        DecimalFormat df = new DecimalFormat("#0.000");
        return df.format(a);
    }

    /**
     * 获取一位小数点的String
     */
    public static String getOneString(double a) {
        DecimalFormat df = new DecimalFormat("#0.0");
        return df.format(a);
    }

    /**
     * 四舍五入（保留两位小数）
     */
    public static double getRound(double a) {
        BigDecimal b = new BigDecimal(a);
        return b.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * 保留2位小数
     *
     * @return
     */
    public static String formatStr2(String src) {
        if (TextUtils.isEmpty(src) || src.equals("null")) {
            return "0.00";
        } else {
            BigDecimal b = new BigDecimal(src);
            double temp = b.setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue();
            DecimalFormat df = new DecimalFormat("#0.00");
            return df.format(temp);
        }
    }

    /**
     * 保留3位小数
     *
     * @return
     */
    public static String formatStr3(String src) {
        if (TextUtils.isEmpty(src) || src.equals("null")) {
            return "0.000";
        } else {
            BigDecimal b = new BigDecimal(src);
            double temp = b.setScale(3, BigDecimal.ROUND_HALF_DOWN).doubleValue();
            DecimalFormat df = new DecimalFormat("#0.000");
            return df.format(temp);
        }
    }

    /**
     * 字符串保留后两位小数, 并去掉逗号
     *
     * @param source
     * @return
     */
    public static String getString(String source) {
        if (source == null || "".equals(source)) {
            source = "0.00";
        }
        source = source.replace(",", "");
        source = source.replace(" ", "");
        BigDecimal b = new BigDecimal(Double.valueOf(source));
        double temp = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        DecimalFormat df = new DecimalFormat("#0.00");
        return df.format(temp);
    }

    /**
     * 检查用户名
     */
    public static boolean validateUserName(String userName) {
        return matches("^[A-Za-z][A-Za-z0-9_]{5,17}$", userName);
    }

    /**
     * 检测姓名的输入,至少两个中文汉字，最多十个中文汉字
     * //•● ·
     */
    public static boolean validateName(String name) {
        if (null != name) {
            if (name.contains("•") || name.contains("·")) {
                name = name.replace("•", "").replace("·", "");
            }
            return matches("^[\u4e00-\u9fa5]{2,15}$", name);
        }
        return false;
    }

    /**
     * 限制输入的内容（登录密码）是由字母+数字组成
     */
    public static boolean limitInputContent(String pwd) {
        String rule = "[0-9A-Za-z]{1,16}";  //字母+数字
        return matches(rule, pwd);
    }

    /**
     * 检查登录密码（6-16位的字母+数字组成）
     * （区分大小写）
     */
    public static boolean validateLoginPwd(String pwd) {
        String rule = "[0-9A-Za-z]{6,16}";  //字母+数字
//        String rule = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$";  //字母+数字 不能纯数字或字母
//        String rule = "^[\\w\\W]{4,16}$";  //任意字符
        return matches(rule, pwd);
    }

    /**
     * 检查交易密码（8-16位任意字符）
     * 交易密码由8-16位的字母+数字组成
     */
    public static boolean validateDealPwd(String pwd) {
//        return matches("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{4,16}$", pwd);
        return matches("^[\\w\\W]{4,16}$", pwd);
    }

    /**
     * 检验数字输入框大于零且不能为"-"或"+"
     */
    public static boolean validateEditParams(String editStr) {
        return matches("^(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$", editStr);
    }

    /**
     * 检测真实姓名格式
     */
    public static boolean validateRealName(String name) {
//        return matches("^[\\u4e00-\\u9fa5]*$", name);
        return matches("^[\\.．▪•●·\u4e00-\u9fa5]{2,15}$", name);
    }

    /**
     * 检测邮箱验证格式
     */
    public static boolean validateEmail(String email) {
        String EMAIL_MATCH_RULE =
                "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2," +
                        "4}|[0-9]{1,3})(\\]?)$";

        return matches(EMAIL_MATCH_RULE, email);
    }

    /**
     * 验证手机号码
     *
     * @param s
     * @return
     */
    public static boolean isMobileNumber(String s) {
        if (null == s) {
            return false;
        }

        return s.startsWith("1") && s.length() == 11;  //以1开头，长度为11即可

//        Pattern pattern = Pattern.compile("(1[34578][0-9])(\\d{4})(\\d{4})$");
//        if (pattern.matcher(s).matches()) {
//            return true;
//        }
//        return false;
    }

    public static boolean validateMoney(String money) {
        return matches("^([0-9]+|[0-9]{1,3}(,[0-9]{3})*)(.[0-9]{1,2})?$", money);
    }

    /***
     * 校验十八位身份证号码
     *
     * @param idCard
     * @return
     */
    public static boolean checkIdCard(String idCard) {
        if (StringUtils.isEmptyOrNull(idCard) || idCard.length() != 18) {
            return false;
        }
        /*
         * 身份证15位编码规则：dddddd yymmdd xx p
		 * dddddd：6位地区编码
		 * yymmdd: 出生年(两位年)月日，如：910215
		 * xx: 顺序编码，系统产生，无法确定
		 * p: 性别，奇数为男，偶数为女
		 *
		 * 身份证18位编码规则：dddddd yyyymmdd xxx y
		 * dddddd：6位地区编码
		 * yyyymmdd: 出生年(四位年)月日，如：19910215
		 * xxx：顺序编码，系统产生，无法确定，奇数为男，偶数为女
		 * y: 校验码，该位数值可通过前17位计算获得
		 *
		 * 前17位号码加权因子为 Wi = [ 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 ]
		 * 所有号码加权因子为 Wi = [ 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2, 1 ]
		 * 验证位 Y = [ 1, 0, 10, 9, 8, 7, 6, 5, 4, 3, 2 ]
		 * 如果验证码恰好是10，为了保证身份证是十八位，那么第十八位将用X来代替
		 * 校验位计算公式：Y_P = mod( ∑(Ai×Wi),11 )
		 * i为身份证号码1...17 位; Y_P为校验码Y所在校验码数组位置
		 */

        try {
            //将前17位加权因子保存在数组里
            int[] idCardWi = new int[]{7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
            //这是除以11后，可能产生的11位余数、验证码，也保存成数组
            int[] idCardY = new int[]{1, 0, 10, 9, 8, 7, 6, 5, 4, 3, 2};
            int idCardWiSum = 0; //用来保存前17位各自乖以加权因子后的总和
            for (int i = 0; i < 17; i++) {
                idCardWiSum += Integer.parseInt(idCard.substring(i, i + 1)) * idCardWi[i];
            }
            int idCardMode = idCardWiSum % 11; //计算出校验码所在数组的位置
            String idCardLast = idCard.substring(17);//得到最后一位身份证号码
            //如果等于2，则说明校验码是10，身份证号码最后一位应该是X
            if (idCardMode == 2) {
                return "X".equals(idCardLast) || "x".equals(idCardLast);
            } else {
                //用计算出的验证码与最后一位身份证号码匹配，如果一致，说明通过，否则是无效的身份证号码
                return idCardLast.equals(idCardY[idCardMode] + "");
            }
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 正则表达式检查
     *
     * @param regex
     * @param src
     * @return
     */
    public static boolean matches(String regex, String src) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(src);
        return matcher.matches();
    }

    /***
     * 检测银行卡卡号是否正确
     *
     * @param cardId
     * @return
     */
    public static boolean checkBankCard(String cardId) {
        cardId = cardId.replace(" ", "");
        if (cardId.length() != 19 && cardId.length() != 16) {
            return false;
        }
        int oddsum = 0; //奇数求和
        int evensum = 0; //偶数求和
        int allsum = 0;
        int cardNoLength = cardId.length();
        int lastNum = Integer.parseInt(cardId.substring(cardId.length() - 1, cardId.length()));
        cardId = cardId.substring(0, cardId.length() - 1);
        for (int i = cardNoLength - 1; i >= 1; i--) {
            String ch = cardId.substring(i - 1, i);
            int tmpVal = Integer.parseInt(ch);
            if (cardNoLength % 2 == 1) {
                if ((i % 2) == 0) {
                    tmpVal *= 2;
                    if (tmpVal >= 10) {
                        tmpVal -= 9;
                    }
                    evensum += tmpVal;
                } else {
                    oddsum += tmpVal;
                }
            } else {
                if ((i % 2) == 1) {
                    tmpVal *= 2;
                    if (tmpVal >= 10) {
                        tmpVal -= 9;
                    }
                    evensum += tmpVal;
                } else {
                    oddsum += tmpVal;
                }
            }
        }

        allsum = oddsum + evensum;
        allsum += lastNum;
        return (allsum % 10) == 0;
    }

    /**
     * 清除html标签
     *
     * @param inputString
     * @return
     */
    public static String Html2Text(String inputString) {
        String htmlStr = inputString; //含html标签的字符串
        String textStr = "";
        Pattern p_script;
        Matcher m_script;
        Pattern p_style;
        Matcher m_style;
        Pattern p_html;
        Matcher m_html;

        try {
            String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>";
            //定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script> }
            String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>";
            //定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style> }
            String regEx_html = "<[^>]+>"; //定义HTML标签的正则表达式

            p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
            m_script = p_script.matcher(htmlStr);
            htmlStr = m_script.replaceAll(""); //过滤script标签

            p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
            m_style = p_style.matcher(htmlStr);
            htmlStr = m_style.replaceAll(""); //过滤style标签

            p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
            m_html = p_html.matcher(htmlStr);
            htmlStr = m_html.replaceAll(""); //过滤html标签

            textStr = htmlStr;
        } catch (Exception e) {
        }
        return textStr;//返回文本字符串
    }

    /**
     * 验证URL是否合法，必须带http\https才认为合法，URL可带端口
     *
     * @param url
     * @return
     */
    public static boolean validateUrl(String url) {
        if (StringUtils.isEmpty(url)) {
            return false;
        }
        String regEx = "^(http|https|ftp)\\://([a-zA-Z0-9\\.\\-]+(\\:[a-zA-"
                + "Z0-9\\.&%\\$\\-]+)*@)?((25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{"
                + "2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}"
                + "[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|"
                + "[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-"
                + "4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])|([a-zA-Z0"
                + "-9\\-]+\\.)*[a-zA-Z0-9\\-]+\\.[a-zA-Z]{2,4})(\\:[0-9]+)?(/"
                + "[^/][a-zA-Z0-9\\.\\,\\?\\'\\\\/\\+&%\\$\\=~_\\-@]*)*$";
        return matches(regEx, url);
    }

    /**
     * 判断输入是否是汉字
     *
     * @param c
     * @return
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS;
    }

    /**
     * 检测字符串是否为全中文
     *
     * @param name
     * @return
     */
    public static boolean checkNameChinese(String name) {
        boolean res = true;
        char[] cTemp = name.toCharArray();
        for (int i = 0; i < cTemp.length; i++) {
            if (!isChinese(cTemp[i])) {
                res = false;
                break;
            }
        }
        return res;
    }

    /**
     * 检测是否有emoji表情
     *
     * @param source
     * @return
     */
    public static boolean containsEmoji(String source) {
        for (int i = 0; i < source.length(); i++) {
            if (isEmojiCharacter(String.valueOf(source.charAt(i)))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否是Emoji
     *
     * @param codePoint 比较的单个字符
     * @return
     */
    public static boolean isEmojiCharacter(String codePoint) {

        String regEx = "[^\\u0020-\\u007E\\u00A0-\\u00BE\\u2E80-\\uA4CF\\uF900-\\uFAFF\\uFE30-" +
                "\\uFE4F\\uFF00-\\uFFEF\\u0080-\\u009F\\u2000-\\u201f\r\n]";

        return matches(regEx, String.valueOf(codePoint));
    }

}