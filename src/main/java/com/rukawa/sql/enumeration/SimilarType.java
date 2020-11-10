package com.rukawa.sql.enumeration;

import com.rukawa.sql.util.BeanUtil;
import com.rukawa.sql.util.StringUtil;

import java.util.Arrays;
import java.util.Optional;

public enum SimilarType {

    PRE_SIMILAR("presimilar"),
    SUFFIX_SIMILAR("suffixsimilar"),
    SIMILAR("similar"),
    OTHER("");

    private String type;

    SimilarType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static SimilarType getInstance(String type) {
        if (BeanUtil.isEmpty(type)) {
            return OTHER;
        }
        Optional<SimilarType> similarTypeOptional = Arrays.stream(values())
                .filter(value -> type.contains(value.type.toLowerCase()))
                .findFirst();
        if(similarTypeOptional.isPresent()) {
            return similarTypeOptional.get();
        }
        return OTHER;
    }
}
