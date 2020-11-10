package com.rukawa.sql.interfaces;

import com.rukawa.sql.exception.NoneExecutionException;
import com.rukawa.sql.param.OrderParam;
import com.rukawa.sql.param.PageParam;
import com.rukawa.sql.param.RangeParam;
import com.rukawa.sql.param.SQLParam;

import java.util.Collection;
import java.util.Map;

public interface ISQLBuilder {

    StringBuilder buildInsertSQL(Collection<String> executionFields) throws NoneExecutionException;

    StringBuilder buildDeleteSQL();

    StringBuilder buildDeleteSQLWithParam(SQLParam param);

    StringBuilder buildUpdateSQL(Collection<String> executionFields) throws NoneExecutionException;

    StringBuilder buildUpdateSQLWithParam(SQLParam param) throws NoneExecutionException;

    StringBuilder buildSelectSQL(Collection<String> executionFields) throws NoneExecutionException;

    StringBuilder buildSelectSQLWithParam(SQLParam param) throws NoneExecutionException;

    StringBuilder buildWhereSQL(Collection<String> conditionFields, String delimiter);

    StringBuilder buildWhereSQLWithParam(SQLParam param);

    StringBuilder buildSimilarSQL(Map<String, Object> similarMap, String delimiter);

    StringBuilder buildPreSimilarSQL(Map<String, Object> similarMap, String delimiter);

    StringBuilder buildSuffixSimilarSQL(Map<String, Object> similarMap, String delimiter);

    StringBuilder buildRangeSQL(Collection<RangeParam> rangeParams, String delimiter);

    StringBuilder buildOrderSQL(Collection<OrderParam> orderParams, String delimiter);

    StringBuilder buildLimitSQL(PageParam pageParam);
}
