package com.rukawa.sql.util;

import java.util.Collection;
import java.util.Optional;

public class CollectionUtil {

    /**
     * 是否为空集合
     *
     * @param collection 集合类
     * @return boolean
     */
    public static boolean isEmpty(Collection collection) {
        return !(Optional.ofNullable(collection).isPresent() && !collection.isEmpty());
    }

    /**
     * 是否为非空集合
     *
     * @param collection 集合类
     * @return boolean
     */
    public static boolean isNotEmpty(Collection collection) {
        return !isEmpty(collection);
    }

    /**
     * a集合中是否含有b集合中的元素
     *
     * @param collectionA 集合a
     * @param collectionB 集合b
     * @return boolean
     */
    public static boolean retains(Collection collectionA, Collection collectionB) {
        for (Object a : collectionA) {
            for (Object b : collectionB) {
                if(a.equals(b)) {
                    return true;
                }
            }
        }
        return false;
    }
}
