package com.rukawa.sql.factory;

import com.rukawa.sql.configuration.SQLBuilderConfiguration;
import com.rukawa.sql.interfaces.ISQLBuilder;
import com.rukawa.sql.interfaces.impl.MyISQLBuilder;
import com.rukawa.sql.proxy.SQLBuilderProxy;
import com.rukawa.sql.util.BeanUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Properties;

public class SQLBuilderFactory {

    private static ClassLoader classLoader = ISQLBuilder.class.getClassLoader();

    /**
     * 创建默认SQLBuilder
     *
     * @param tableName 数据表名
     * @return ISQLBuilder
     */
    public static ISQLBuilder createDefaultSQLBuilder(String tableName) {
        MyISQLBuilder mySQLBuilder = new MyISQLBuilder(tableName);
        SQLBuilderProxy sqlBuilderProxy = new SQLBuilderProxy(mySQLBuilder, null);
        return (ISQLBuilder) Proxy.newProxyInstance(classLoader, mySQLBuilder.getClass().getInterfaces(), sqlBuilderProxy);
    }

    public static ISQLBuilder createSQLBuilder(String fileName, String tableName) {
        File file = new File(fileName);
        if (file.exists()) {
            try (InputStream inputStream = new FileInputStream(file)) {
                Properties properties = new Properties();
                properties.load(inputStream);
                return createSQLBuilder(properties, tableName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return createDefaultSQLBuilder(tableName);
    }

    public static ISQLBuilder createSQLBuilder(Properties properties, String tableName) {
        SQLBuilderConfiguration sqlBuilderConfiguration = BeanUtil.convertPropertiesToObject(properties, SQLBuilderConfiguration.class);
        return createSQLBuilder(sqlBuilderConfiguration, tableName);
    }

    public static ISQLBuilder createSQLBuilder(Map<String, Object> propMap, String tableName) {
        SQLBuilderConfiguration sqlBuilderConfiguration = BeanUtil.convertMapToObject(propMap, SQLBuilderConfiguration.class);
        return createSQLBuilder(sqlBuilderConfiguration, tableName);
    }

    public static ISQLBuilder createSQLBuilder(SQLBuilderConfiguration configuration, String tableName) {
        try {
            Class sqlBuilderClass = configuration.getSqlBuilderClass();
            if (BeanUtil.isEmpty(sqlBuilderClass) && !BeanUtil.isEmpty(configuration.getSqlBuilderClassName())) {
                sqlBuilderClass = Class.forName(configuration.getSqlBuilderClassName());
            }
            if(!BeanUtil.isEmpty(sqlBuilderClass)) {
                Object sqlBuilderObj = sqlBuilderClass.getDeclaredConstructor(String.class).newInstance(tableName);
                SQLBuilderProxy sqlBuilderProxy = new SQLBuilderProxy(sqlBuilderObj, configuration);
                return (ISQLBuilder) Proxy.newProxyInstance(classLoader, sqlBuilderClass.getInterfaces(), sqlBuilderProxy);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return createDefaultSQLBuilder(tableName);
    }

    public static ISQLBuilder createSQLBuilder(Class sqlBuilderClass, Class sqlBuilderExecutorClass, boolean isCamelCase, String tableName) {
        SQLBuilderConfiguration sqlBuilderConfiguration = new SQLBuilderConfiguration();
        sqlBuilderConfiguration.setSqlBuilderClass(sqlBuilderClass);
        sqlBuilderConfiguration.setSqlBuilderExecutorClass(sqlBuilderExecutorClass);
        sqlBuilderConfiguration.setCamelCase(isCamelCase);
        return createSQLBuilder(sqlBuilderConfiguration, tableName);
    }

    public static ISQLBuilder createSQLBuilder(Class sqlBuilderClass, String sqlBuilderExecutorClassName, boolean isCamelCase, String tableName) {
        SQLBuilderConfiguration sqlBuilderConfiguration = new SQLBuilderConfiguration();
        sqlBuilderConfiguration.setSqlBuilderClass(sqlBuilderClass);
        sqlBuilderConfiguration.setSqlBuilderExecutorClassName(sqlBuilderExecutorClassName);
        sqlBuilderConfiguration.setCamelCase(isCamelCase);
        return createSQLBuilder(sqlBuilderConfiguration, tableName);
    }

    public static ISQLBuilder createSQLBuilder(String sqlBuilderClassName, Class sqlBuilderExecutorClass, boolean isCamelCase, String tableName) {
        SQLBuilderConfiguration sqlBuilderConfiguration = new SQLBuilderConfiguration();
        sqlBuilderConfiguration.setSqlBuilderClassName(sqlBuilderClassName);
        sqlBuilderConfiguration.setSqlBuilderExecutorClass(sqlBuilderExecutorClass);
        sqlBuilderConfiguration.setCamelCase(isCamelCase);
        return createSQLBuilder(sqlBuilderConfiguration, tableName);
    }

    public static ISQLBuilder createSQLBuilder(String sqlBuilderClassName, String sqlBuilderExecutorClassName, boolean isCamelCase, String tableName) {
        SQLBuilderConfiguration sqlBuilderConfiguration = new SQLBuilderConfiguration();
        sqlBuilderConfiguration.setSqlBuilderClassName(sqlBuilderClassName);
        sqlBuilderConfiguration.setSqlBuilderExecutorClassName(sqlBuilderExecutorClassName);
        sqlBuilderConfiguration.setCamelCase(isCamelCase);
        return createSQLBuilder(sqlBuilderConfiguration, tableName);
    }
}
