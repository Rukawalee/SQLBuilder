package com.rukawa.sql.util;

import java.util.Optional;

/**
 * 字符串校验
 */
public class StringUtil {

    public static boolean isEmpty(String str) {
        return !Optional.ofNullable(str).isPresent();
    }
}
