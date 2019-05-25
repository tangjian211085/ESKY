package com.puhui.lib.utils.pinyin;

/**
 * Copyright: 人人普惠
 * Created by TangJian on 2017/4/5.
 * Description:
 * Modified By:
 */

public class PinYinUtils {

    /**
     * Chinese character -> Pinyin
     */
    public static String getPingYin(String inputString) {
        char[] input = inputString.trim().toCharArray();
        String output = "";
        for (int i = 0; i < input.length; i++) {
            output += toPinyin(input[i]);
        }
        return output.toLowerCase();
    }


    /**
     * return pinyin if c is chinese in uppercase, String.valueOf(c) otherwise.
     */
    public static String toPinyin(char c) {
        if (isChinese(c)) {
            if (c == PinYinData.CHAR_12295) {
                return PinYinData.PINYIN_12295;
            } else {
                return PinYinData.PINYIN_TABLE[getPinyinCode(c)];
            }
        } else {
            return String.valueOf(c);
        }
    }

    /**
     * return whether c is chinese
     */
    public static boolean isChinese(char c) {
        return (PinYinData.MIN_VALUE <= c && c <= PinYinData.MAX_VALUE
                && getPinyinCode(c) > 0)
                || PinYinData.CHAR_12295 == c;
    }

    private static int getPinyinCode(char c) {
        int offset = c - PinYinData.MIN_VALUE;
        if (0 <= offset && offset < PinYinData.PINYIN_CODE_1_OFFSET) {
            return decodeIndex(PinyinCode1.PINYIN_CODE_PADDING, PinyinCode1.PINYIN_CODE, offset);
        } else if (PinYinData.PINYIN_CODE_1_OFFSET <= offset
                && offset < PinYinData.PINYIN_CODE_2_OFFSET) {
            return decodeIndex(PinyinCode2.PINYIN_CODE_PADDING, PinyinCode2.PINYIN_CODE,
                    offset - PinYinData.PINYIN_CODE_1_OFFSET);
        } else {
            return decodeIndex(PinyinCode3.PINYIN_CODE_PADDING, PinyinCode3.PINYIN_CODE,
                    offset - PinYinData.PINYIN_CODE_2_OFFSET);
        }
    }

    private static short decodeIndex(byte[] paddings, byte[] indexes, int offset) {
        //CHECKSTYLE:OFF
        int index1 = offset / 8;
        int index2 = offset % 8;
        short realIndex;
        realIndex = (short) (indexes[offset] & 0xff);
        //CHECKSTYLE:ON
        if ((paddings[index1] & PinYinData.BIT_MASKS[index2]) != 0) {
            realIndex = (short) (realIndex | PinYinData.PADDING_MASK);
        }
        return realIndex;
    }

    

}
