package com.lavector.crawlers.weibo.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class UrlUtils {

    public static String encodeStr(String keyword) {
        try {
            return URLEncoder.encode(keyword, "UTF-8")
                    .replaceAll("#","%23")
                    .replaceAll("\\+", "%2B")
                    .replaceAll(" ","%20")
                    .replaceAll("%21", "!")
                    .replaceAll("%27", "'")
                    .replaceAll("%28", "(")
                    .replaceAll("%29", ")")
                    .replaceAll("%7E", "~");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("encode string : " + keyword + " error", e);
        }
    }

}
