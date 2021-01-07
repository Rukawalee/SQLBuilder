package com.rukawa.sql.interfaces;

import com.rukawa.sql.param.OrderParam;
import com.rukawa.sql.param.PageParam;
import com.rukawa.sql.param.RangeParam;
import com.rukawa.sql.param.SQLParam;

import java.util.Collection;
import java.util.Map;

public interface ISQLBuilder {

    StringBuilder buildInsertSQL(Collection<String> executionFields);

    StringBuilder buildDeleteSQL(Collection<String> conditionFields, String delimiter);

    StringBuilder buildDeleteSQLWithParam(SQLParam param);

    StringBuilder buildUpdateSQL(Collection<String> executionFields);

    StringBuilder buildUpdateSQLWithParam(SQLParam param);

    StringBuilder buildSelectSQL(Collection<String> executionFields);

    StringBuilder buildSelectSQLWithParam(SQLParam param);

    StringBuilder buildWhereSQL(Collection<String> conditionFields, String delimiter);

    StringBuilder buildWhereSQLWithParam(SQLParam param);

    StringBuilder buildSimilarSQL(Map<String, Object> similarMap, String delimiter);

    StringBuilder buildPreSimilarSQL(Map<String, Object> similarMap, String delimiter);

    StringBuilder buildSuffixSimilarSQL(Map<String, Object> similarMap, String delimiter);

    StringBuilder buildRangeSQL(Collection<RangeParam> rangeParams, String delimiter);

    StringBuilder buildOrderSQL(Collection<OrderParam> orderParams, String delimiter);

    StringBuilder buildLimitSQL(PageParam pageParam);
}
