package com.rukawa.sql.enumeration;

import com.rukawa.common.util.BeanUtil;
import com.rukawa.sql.executor.JdbcTemplate;

import java.util.Arrays;
import java.util.Optional;

public enum SQLBuilderExecutorClassType {
    JDBC_TEMPLATE(JdbcTemplate.class, "org.springframework.jdbc.core.JdbcTemplate"),
    JDBC(null, null);

    private Class clazz;

    private String className;

    SQLBuilderExecutorClassType(Class clazz, String className) {
        this.clazz = clazz;
        this.className = className;
    }

    public Class getClazz() {
        return clazz;
    }

    public String getClassName() {
        return className;
    }

    public static SQLBuilderExecutorClassType getInstance(Class clazz, String className) {
        if (!(BeanUtil.isEmpty(clazz) && BeanUtil.isEmpty(className))) {
            Optional<SQLBuilderExecutorClassType> sqlBuilderExecutorOptional = Arrays.stream(values())
                    .filter(value -> value.clazz.equals(clazz) || value.className.toLowerCase().equals(className.toLowerCase()))
                    .findFirst();
            if (sqlBuilderExecutorOptional.isPresent()) {
                return sqlBuilderExecutorOptional.get();
            }
        }
        return JDBC;
    }
}
