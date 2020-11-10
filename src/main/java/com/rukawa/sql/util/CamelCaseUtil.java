package com.rukawa.sql.util;

/**
 * 驼峰规则工具
 */
public class CamelCaseUtil {

    /**
     * 映射下划线命名为驼峰命名
     *
     * @param underScore 下划线命名的字符串
     * @return
     */
    public static String mapUnderScoreToCamelCase(String underScore) {
        if (StringUtil.isEmpty(underScore)) {
            return null;
        }
        StringBuilder builder = new StringBuilder(underScore);
        for (int i = 1; i < builder.length(); i++) {
            char pre = builder.charAt(i - 1);
            char curr = builder.charAt(i);
            if (isUnderScore(pre, curr)) {
                builder.replace(i - 1, i + 1, String.valueOf((char) (curr - 32)));
            }
        }
        return builder.toString();
    }

    /**
     * 映射驼峰命名为下划线命名
     *
     * @param camelCase 驼峰命名的字符串
     * @return
     */
    public static String mapCamelCaseToUnderScore(String camelCase) {
        if (StringUtil.isEmpty(camelCase) || camelCase.indexOf("_") != -1) {
            return camelCase;
        }
        StringBuilder builder = new StringBuilder(camelCase);
        for (int i = 1; i < builder.length(); i++) {
            char pre = builder.charAt(i - 1);
            char curr = builder.charAt(i);
            if (isCamelCase(pre, curr)) {
                builder.replace(i, i + 1, "_" + (char) (curr + 32));
            }
        }
        return builder.toString();
    }

    /**
     * 判断下划线命名
     *
     * @param pre  前一个字符
     * @param curr 当前字符
     * @return
     */
    private static boolean isUnderScore(char pre, char curr) {
        return pre == '_' && curr >= 'a';
    }

    /**
     * 判断驼峰命名
     *
     * @param pre  前一个字符
     * @param curr 当前字符
     * @return
     */
    private static boolean isCamelCase(char pre, char curr) {
        return pre >= 'a' && curr <= 'Z';
    }
}
