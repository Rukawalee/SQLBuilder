package com.rukawa.sql.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SQLBuilderConfiguration {

    // sqlBuilder实现类
    private Class sqlBuilderClass;

    // sqlBuilder执行器类
    private Class sqlBuilderExecutorClass;

    // sqlBuilder实现类名
    private String sqlBuilderClassName;

    // sqlBuilder执行器类名
    private String sqlBuilderExecutorClassName;

    // 是否驼峰命名
    private boolean camelCase = false;
}
