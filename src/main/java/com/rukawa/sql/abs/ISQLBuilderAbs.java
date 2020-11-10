package com.rukawa.sql.abs;

import com.rukawa.sql.interfaces.ISQLBuilder;

public abstract class ISQLBuilderAbs implements ISQLBuilder {

    private String tableName;

    public ISQLBuilderAbs(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
