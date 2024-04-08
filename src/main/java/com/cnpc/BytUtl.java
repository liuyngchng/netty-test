package com.cnpc;


import com.google.common.base.Strings;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;

/**
 * byte util
 * @author whoami
 * @date 2022-06-10
 */
public class BytUtl {

    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * 将字节数组转换为整数, according to Big Endian
     * @param b A byte array
     * @return A int value
     */
    public static int getInt(byte[] b) {
        if (b.length < 4) {
            return -1;
        }
        try {
            return (b[0] << 24) | ((b[1] << 16) | (b[2] << 8) | b[3] & 0xff);
        } catch (Exception ex) {
            return -2;
        }
    }


    public static int getIntFrom3Bytes(byte[] b) {
        if (b.length != 3) {
            return -1;
        }
        try {
            return ((b[0] & 0xff) << 16) | ((b[1] & 0xff) << 8) | (b[2] & 0xff);
        } catch (Exception ex) {
            return -2;
        }
    }

    public static String getIPv4(String hex) {
        if (hex.length() < 8) {
            return "";
        }
        final StringBuffer sb = new StringBuffer(hex.length());
        for (int i = 0; i < hex.length(); i=i+2) {
            final int a = Integer.parseInt(hex.substring(i, i + 2), 16);
            if (sb.length() > 0) {
                sb.append(".");
            }
            sb.append(a);
        }
        return sb.toString();
    }

    public static String getIPv6(String hex) {
        if (hex.length() != 32) {
            return "illegal_IPv6_" + hex;
        }
        final StringBuffer sb = new StringBuffer(hex.length());
        for (int i = 0; i < hex.length(); i += 4) {
            final short a = (short)Integer.parseInt(hex.substring(i, i + 4), 16);
            if (sb.length() > 0) {
                sb.append(":");
            }
            sb.append(BytUtl.getHex(BytUtl.getBytes(a)).toLowerCase());
        }
        return sb.toString();
    }

    public static String getFormatTime(byte[] b) {
        if (null ==  b || b.length !=6) {
            return "";
        }
        final StringBuffer sb = new StringBuffer(b.length * 2);
        sb.append("20");
        for (int i = 0; i < b.length; i ++) {
            if (sb.length() != 2) {
                if (i < 3) {
                    sb.append("-");
                } else if (i == 3) {
                    sb.append(" ");
                } else {
                    sb.append(":");
                }
            }
            sb.append(BytUtl.getHex(b[i]));
        }
        return sb.toString();
    }

    /**
     * 将一个字节数组转换为无符号的16进制数字文本0X00 ～ 0XFF
     * 顺序为“byte[0].toHex byte[1].toHex byte[2].toHex”
     * @param b
     * @return
     */
    public static String getHex(byte[] b) {
        if (null == b) {
            return "";
        }
        final StringBuffer sb = new StringBuffer(b.length * 2);
        for (int i = 0; i < b.length; i ++) {
            sb.append(getHex(b[i]));
        }
        return sb.toString();
    }

    /**
     * 将一个字节转换为无符号的16进制数字文本0X00 ～ 0XFF
     * @param b a byte from -128 ~ +127
     * @return a hexadecimal literal.
     */
    public static String getHex(byte b) {
        if (b == 0)
            return "00";
        final int a = b & 0xFF;       // remove the sign of byte
        final StringBuffer sb = new StringBuffer(2);
        final int highBit = a / 16;
        int lowBit = a % 16;
        sb.append(BytUtl.int2char(highBit));
        sb.append(BytUtl.int2char(lowBit));
        return sb.toString();
    }

    public static String getHex(short s) {
        return BytUtl.getHex(BytUtl.getBytes(s));
    }

    public static String getBin(byte[] b) {
        if (b == null) {
            return "0";
        }
        final StringBuffer sb = new StringBuffer(8);
        for (int j = 0; j < b.length; j ++) {
            for (int i = 0; i < 8; i++)
                sb.append(((b[j] << i) & 0x80) >> 7);
        }
        return sb.toString();
    }

    /**
     * 将一个字节转换为2进制的文本输出0b00000000 ～ 0b11111111
     * @param b b a byte from -128 ~ +127
     * @return a binary literal
     */
    public static String getBin(byte b) {
        if (b == 0) {
            return "00000000";
        }
        final StringBuffer sb = new StringBuffer(8);
        for (int i = 0; i < 8; i ++)
            sb.append(((b << i) & 0x80) >> 7);
        return sb.toString();
    }

    /**
     * 将 hex 字符串解析为byte
     * @param hex a hex string
     * @return a byte
     */
    public static byte getByte(String hex) {
        if (null == hex || "" == hex) {
            return 0;
        }
        return (byte) Integer.parseInt(hex, 16);
    }

    public static int getIntFromBin(String bin) {
        int a = 0;
        for (int i = 0; i < bin.length(); i++) {
            int b = Integer.parseInt(bin.charAt(bin.length() - 1 - i) + "");
            a += b * Math.pow(2, i);
        }
        return a;
    }

    /**
     * 将 hex 字符串解析为 byte 数组
     * @param s a hex string
     * @return a byte array
     */
    public static byte[] getBytes(String s) {
        if (null == s || "" == s) {
            return null;
        }
        try {
            return BytUtl.getBytes(s, s.length() / 2 + s.length() % 2);
        } catch (Exception ex) {
            LOGGER.error("BytUtil.getBytes_err_{}", s ,ex);
            return null;
        }
    }

    /**
     * 将 byteHex 单字节拷贝 size 份，形成字节流
     * @param byteHex 单字节 hex字符串
     * @param size
     * @return
     */
    public static byte[] dupBytes(String byteHex, int size) {
        if (byteHex.length() > 2) {
            byteHex = byteHex.substring(0, 2);
        }
        final byte[] bytes = new byte[size];
        for (int i = 0; i < size; i ++) {
            bytes[i] = BytUtl.getByte(byteHex);
        }
        return bytes;
    }

    /**
     *
     * @param s
     * @param size
     * @param appendSuffix 在末尾补充0
     * @return
     */
    public static byte[] getBytes(String s, int size, boolean appendSuffix) {
        if (appendSuffix) {
            return getBytes(s, size);
        } else {
            byte[] t = getBytes(s, size);
            byte[] tmp = new byte[t.length];
            for (int i = 0; i < t.length; i ++) {
                tmp[i] = t[t.length -i -1];
            }
            return tmp;
        }
    }

    /**
     * 获取指定长度的byte, 并解析 hex 字符串进行初始化,
     * 按照大端序，当字符串长度不够时，在末尾补0
     * @param s a hex string
     * @param size byte size
     * @return a byte array
     */
    public static byte[] getBytes(String s, int size) {
        final byte[] bytes = new byte[size];
        if (null == s || "" == s) {
            return bytes;
        }
        final String str;
        if (s.length() % 2 == 0) {
            str = s;
        } else {
            str = "0" + s;
        }
        final int byteSize = str.length() / 2 + str.length() % 2 > size ?
            size : str.length() / 2 + str.length() % 2;
        for (int i = 0; i < byteSize; i ++) {
            bytes[i] = BytUtl.getByte(str.substring(i * 2, i * 2 + 2));
        }
        return bytes;
    }

    public static byte[] getBytes(short x) {
        byte high = (byte) (0x00FF & (x >> 8));
        byte low = (byte) (0x00FF & x);
        byte[] bytes = new byte[2];
        bytes[0] = high;
        bytes[1] = low;
        return bytes;
    }

    public static byte[] getBytes(int a) {
        return new byte[]{
            (byte) ((a >> 24) & 0xFF),
            (byte) ((a >> 16) & 0xFF),
            (byte) ((a >> 8) & 0xFF),
            (byte) (a & 0xFF)
        };
    }

    public static byte[] getCRCByte2(byte[] bytes) {
        int crc = BytUtl.getCRC(bytes);
        return BytUtl.subByte(BytUtl.getBytes(crc), 2, 2);
    }

    public static byte[] subByte(byte[] b, int off, int length) {
        byte[] b1 = new byte[length];
        System.arraycopy(b, off, b1, 0, length);
        return b1;
    }

    public static int bytes2Int(byte[] b) {
        return b[3] & 0xFF |
            (b[2] & 0xFF) << 8 |
            (b[1] & 0xFF) << 16 |
            (b[0] & 0xFF) << 24;
    }

    private static int getCRC(byte[] bytes) {
        int crc = 0x00; // initial value
        int polynomial = 0x1021;
        for (int i = 0; i < bytes.length; i++) {
            byte b = bytes[i];
            for (int j = 0; j < 8; j++) {
                boolean bit = ((b >> (7 - j) & 1) == 1);
                boolean c15 = ((crc >> 15 & 1) == 1);
                crc <<= 1;
                if (c15 ^ bit)
                    crc ^= polynomial;
            }
        }
        crc &= 0xffff;
        return crc;
    }

    /**
     * convert int to char
     * @param i a number < 15
     * @return a char
     */
    private static char int2char(int i) {
        return (char)(i + BytUtl.getCharOffset(i));
    }

    /**
     * 数值的ASCII码偏移量
     * @param i a number < 15
     * @return
     */
    private static int getCharOffset(int i) {
        if (i < 10) {
            return 0x30;
        } else {
            return 0x37;
        }
    }

    /**
     * byte 与 int 的相互转换
     *
     * @param b
     * @return a int number
     */
    public static int byte2Int(byte b) {
        return b & 0xFF;
    }


    /**
     * Hex字符串转十进制
     *
     * @param inHex 待转换的Hex字符串
     * @return 转换后的十进制
     */
    public static String hex2Tens(String inHex) {
        return Long.parseLong(inHex, 16)+"";
    }



    /**
     * @函数功能: 10进制串转为BCD码
     * @输入参数: 10进制串
     * @输出结果: BCD码
     */
    public static byte[] str2Bcd(String asc) {
        int len = asc.length();
        int mod = len % 2;

        if (mod != 0) {
            asc = "0" + asc;
            len = asc.length();
        }

        if (len >= 2) {
            len = len / 2;
        }

        byte bbt[] = new byte[len];
        byte[] abt = asc.getBytes();
        int j, k;

        for (int p = 0; p < asc.length() / 2; p++) {
            if ((abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {
                j = abt[2 * p] - '0';
            } else if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
                j = abt[2 * p] - 'a' + 0x0a;
            } else {
                j = abt[2 * p] - 'A' + 0x0a;
            }
            if ((abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {
                k = abt[2 * p + 1] - '0';
            } else if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
                k = abt[2 * p + 1] - 'a' + 0x0a;
            } else {
                k = abt[2 * p + 1] - 'A' + 0x0a;
            }
            int a = (j << 4) + k;
            byte b = (byte) a;
            bbt[p] = b;
        }
        return bbt;
    }

    public static byte[] longToBytes(long a) {
        return new byte[]{
            (byte) ((a >> 56) & 0xFF),
            (byte) ((a >> 48) & 0xFF),
            (byte) ((a >> 40) & 0xFF),
            (byte) ((a >> 32) & 0xFF),
            (byte) ((a >> 24) & 0xFF),
            (byte) ((a >> 16) & 0xFF),
            (byte) ((a >> 8) & 0xFF),
            (byte) (a & 0xFF)
        };
    }

    /**
     * 将字节数组转为long
     * 如果input为null,或offset指定的剩余数组长度不足8字节则抛出异常
     * @param input
     * @param offset 起始偏移量
     * @param littleEndian 输入数组是否小端模式
     * @return
     */
        public static long getLong(byte[] input, int offset, boolean littleEndian){
        if (null == input || input.length < 8) {
            LOGGER.error("byte array is null or length little than 8");
            return -1;
        }
        long value = 0;
        for (int count = 0; count < 8; ++count) {
            int shift = (littleEndian ? count :(7-count)) << 3;
            value |= ((long)0xff << shift) & ((long)input[offset+count] << shift);
        }
        return value;
    }

    public static byte[] intToBytes(int a) {
        return new byte[] {
            (byte) ((a >> 24) & 0xFF),
            (byte) ((a >> 16) & 0xFF),
            (byte) ((a >> 8) & 0xFF),
            (byte) (a & 0xFF)
        };
    }

    public static short getShort(byte[] a) {
        return (short) ((short) (a[0] << 8) + (a[1] > 0 ? a[1] : a[1] & 0x00ff));
    }



    public static double parseDouble(byte[] b) {
        final int EXP_LEN = 11;
        final int sign = (0b10000000 & b[0]) == 0 ? 1 : -1;
        final int exponent = (0b011111110000 & (b[0] << 4)) + 0b00001111 & (b[1] >> 4);
        final int pow = (int)(exponent - (Math.pow(2, EXP_LEN -1) -1));
        double mantissa = 1;
        mantissa += (0b00001111 &b[1]) / Math.pow(2, 4);
        for (int i = 2; i < 8; i++) {
            mantissa += (0xff & b[i]) / Math.pow(2, (i- 1)* 8 + 4);
        }
        System.out.println(String.format("sign=%d, exponent=%d, pow=%d, mantissa=%1.20f", sign, exponent, pow, mantissa));
        return sign * mantissa * Math.pow(2, pow);
    }

    public static double getDouble(byte[] bin) {
        if (bin.length != (Double.SIZE/8)) {
            LOGGER.error(
                "BytUtl.getDouble_byte_array_length_err_{}_not_eq_{}",
                bin.length, Double.SIZE/8
            );
            return 0.0d;
        }
        return BytUtl.getDouble(BytUtl.getHex(bin));
    }

    public static double getDouble(String hex) {
        try {
            return Double.longBitsToDouble(Long.parseLong(hex, 16));
        } catch (Exception ex) {
            LOGGER.error("Double_longBitsToDouble_err_{}_err_", hex, ex);
            return 0.0d;
        }
    }

    public static float getFloat(String hex) {
        if (hex.length() != (Float.SIZE/8 *2)) {
            LOGGER.error(
                "BytUtl_getFloat_length_err_{}_not_eq_{}",
                hex.length(), Float.SIZE/8 *2
            );
            return 0.0f;
        }
        try {
            return Float.intBitsToFloat(Integer.parseInt(hex, 16));
        } catch (Exception ex) {
            LOGGER.error(
                "BytUtl_Float_intBitsToFloat_err_{}_err_{}",
                hex, ex
            );
            return 0.0f;
        }
    }

    public static float getFloat(byte[] bytes) {
        if (bytes.length != (Float.SIZE/8)) {
            LOGGER.error(
                "BytUtl.getFloat_length_err_{}_not_eq_{}",
                bytes.length, Float.SIZE/8
            );
            return 0.0f;
        }
        return BytUtl.getFloat(BytUtl.getHex(bytes));
    }

    /**
     * 将多个 byte 合并成一个数组
     * @param bytes
     * @return
     */
    public static byte[] mergeBytes(byte[]... bytes) {
        int l = 0;
        for (int i  = 0; i < bytes.length; i++) {
            l += bytes[i].length;
        }
        final byte[] tmp = new byte[l];
        int p = 0;
        for (int i = 0; i < bytes.length; i++) {
            for (int j = 0; j < bytes[i].length; j++) {
                tmp[p++] = bytes[i][j];
            }
        }
        return tmp;
    }

    public static String formatFlt(Double num, int scale) {
        final BigDecimal bd = new BigDecimal(num);
        final BigDecimal round = bd.setScale(scale, BigDecimal.ROUND_HALF_UP);
        return round.toString();
//        final DecimalFormat df = new DecimalFormat("#.000");
//        final String res = df.format(num);
//        if (".000".equals(res)) {
//            return "0.000";
//        } else {
//            return res;
//        }
    }

    public static String getStr(byte[] bytes) {
        final StringBuilder sb = new StringBuilder(bytes.length);
        for (int i = 0; i < bytes.length; i++) {
            if(bytes[i] >= 32 && bytes[i] <= 126) {
                sb.append((char)bytes[i]);
            } else {
                continue;
            }
        }
        return sb.toString();
    }
}
