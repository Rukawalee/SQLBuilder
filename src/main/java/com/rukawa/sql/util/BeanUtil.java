package com.rukawa.sql.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 对象转化
 */
public class BeanUtil {

    /**
     * 获取对象的方法
     *
     * @param classT 目标class
     * @param <T>    对象类型
     * @return 返回Collection
     */
    public static <T> Collection<String> getObjectMethods(Class classT) {
        if (BeanUtil.isEmpty(classT)) {
            return Collections.EMPTY_LIST;
        }
        List<String> classes = new ArrayList<>();
        Arrays.stream(classT.getDeclaredMethods())
                .map(Method::getName)
                .forEach(classes::add);
        return classes;
    }

    /**
     * 转换Class为集合
     *
     * @param classT             class类型
     * @param isEnableUnderScore 字段采用下划线命名
     * @param <T>                泛型支持
     * @return Collection<String>
     */
    public static <T> Collection<String> convertClassToCollection(Class<T> classT, boolean isEnableUnderScore) {
        if (isEmpty(classT)) {
            return Collections.EMPTY_LIST;
        }
        Field[] fields = classT.getDeclaredFields();
        return Arrays.stream(fields)
                .map(field -> isEnableUnderScore
                        ? CamelCaseUtil.mapCamelCaseToUnderScore(field.getName())
                        : field.getName())
                .collect(Collectors.toList());
    }

    /**
     * 转换对象为Map
     *
     * @param t                  泛型对象
     * @param isEnableUnderScore 字段采用下划线命名
     * @param <T>                泛型支持
     * @return Map<String, Object>
     */
    public static <T> Map<String, Object> convertObjectToMap(T t, boolean isEnableUnderScore) {
        if (isEmpty(t)) {
            return Collections.EMPTY_MAP;
        }
        Field[] fields = t.getClass().getDeclaredFields();
        Map<String, Object> map = new HashMap<String, Object>();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                String fieldName = isEnableUnderScore ? CamelCaseUtil.mapCamelCaseToUnderScore(field.getName()) : field.getName();
                map.put(fieldName, field.get(t));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    /**
     * 转换Map为Object
     *
     * @param map    属性键值对
     * @param classT 目标class
     * @param <T>    目标对象类型
     * @return T
     */
    public static <T> T convertMapToObject(Map<String, Object> map, Class<T> classT) {
        T t = null;
        try {
            t = classT.getDeclaredConstructor().newInstance();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                Field field = classT.getDeclaredField(entry.getKey());
                field.setAccessible(true);
                field.set(t, entry.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    /**
     * 转换Properties为Object
     *
     * @param properties 属性配置
     * @param classT     目标class
     * @param <T>        目标对象类型
     * @return T
     */
    public static <T> T convertPropertiesToObject(Properties properties, Class<T> classT) {
        T t = null;
        try {
            t = classT.getDeclaredConstructor().newInstance();
            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                Field field = classT.getDeclaredField(entry.getKey().toString());
                field.setAccessible(true);
                field.set(t, entry.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    /**
     * 判断对象是否为空
     *
     * @param t   泛型对象
     * @param <T> 泛型支持
     * @return boolean
     */
    public static <T> boolean isEmpty(T t) {
        return !Optional.ofNullable(t).isPresent();
    }
}
