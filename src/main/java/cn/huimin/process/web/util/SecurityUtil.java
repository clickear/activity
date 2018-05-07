package cn.huimin.process.web.util;

import org.apache.commons.codec.binary.Base64;

import java.security.MessageDigest;

/**
 * SecurityUtil
 * Created by pygman on 2016/6/20.
 */
public class SecurityUtil {
    private static final char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};

    public static String Base64Encode(String str){
        return new String(Base64.encodeBase64(str.getBytes()));
    }

    public static String Base64Decode(String str){
        return new String(Base64.decodeBase64(str.getBytes()));
    }

    public static String MD5(String str){

        try {
            byte[] btInput = str.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char chars[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                chars[k++] = hexDigits[byte0 >>> 4 & 0xf];
                chars[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(chars);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }
}
