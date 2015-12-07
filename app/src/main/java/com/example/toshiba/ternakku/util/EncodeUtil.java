package com.example.toshiba.ternakku.util;


import org.apache.commons.httpclient.util.URIUtil;

public class EncodeUtil {
	public static String encode(String input) {
        StringBuilder resultStr = new StringBuilder();
        for (char ch : input.toCharArray()) {
            if (isUnSafe(ch)) {
                //resultStr.append('%');
                ///resultStr.append(toHex(ch / 16));
                //resultStr.append(toHex(ch % 16));
            	try {
            		resultStr.append(URIUtil.encodeAll(String.valueOf(ch), "UTF-8"));
            	} catch (Exception e) { e.printStackTrace(); }
            } else {
                resultStr.append(ch);
            }
        }
        return resultStr.toString();
    }
/*
    private static char toHex(int ch) {
        return (char) (ch < 10 ? '0' + ch : 'A' + ch - 10);
    }
*/
    private static boolean isUnSafe(char ch) {
        if (ch > 128 || ch < 0)
            return true;
        return " !*'();:@&=+$,/?%#[]'".indexOf(ch) >= 0;
    }
}