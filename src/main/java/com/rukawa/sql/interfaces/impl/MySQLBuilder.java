package com.rukawa.sql.interfaces.impl;

import com.rukawa.common.util.BeanUtil;
import com.rukawa.common.util.CollectionUtil;
import com.rukawa.sql.abs.ISQLBuilderAbs;
import com.rukawa.sql.enumeration.RangeSymbol;
import com.rukawa.sql.exception.NoneAttributeException;
import com.rukawa.sql.exception.NoneExecutionException;
import com.rukawa.sql.interfaces.ISQLBuilder;
import com.rukawa.sql.param.OrderParam;
import com.rukawa.sql.param.PageParam;
import com.rukawa.sql.param.RangeParam;
import com.rukawa.sql.param.SQLParam;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MySQLBuilder extends ISQLBuilderAbs implements ISQLBuilder {

    public MySQLBuilder(String tableName) {
        super(tableName);
    }

    @Override
    public StringBuilder buildInsertSQL(Collection<String> executionFields) {
        StringBuilder sqlBuilder = new StringBuilder();
        if (!CollectionUtil.isEmpty(executionFields)) {
            sqlBuilder.append("INSERT INTO ")
                    .append(this.getTableName())
                    .append(" (")
                    .append(String.join(", ", executionFields))
                    .append(") VALUES (")
                    .append(executionFields.stream()
                            .map(executionField -> "?")
                            .collect(Collectors.joining(", ")))
                    .append(")");
        } else {
            throw new NoneExecutionException();
        }
        return sqlBuilder;
    }

    @Override
    public StringBuilder buildDeleteSQL(Collection<String> conditionFields, String delimiter) {
        StringBuilder sqlBuilder = new StringBuilder("DELETE FROM ");
        sqlBuilder.append(this.getTableName());
        if (!CollectionUtil.isEmpty(conditionFields)) {
            sqlBuilder.append(" WHERE ")
                    .append(conditionFields.stream()
                            .map(conditionField -> conditionField + " = ?")
                            .collect(Collectors.joining(" " + delimiter + " ")));
        }
        return sqlBuilder;
    }

    @Override
    public StringBuilder buildDeleteSQLWithParam(SQLParam param) {
        StringBuilder sqlBuilder = new StringBuilder();
        if (!BeanUtil.isEmpty(param)) {
            sqlBuilder.append(buildDeleteSQL(null, null))
                    .append(buildWhereSQLWithParam(param));
        }
        return sqlBuilder;
    }

    @Override
    public StringBuilder buildUpdateSQL(Collection<String> executionFields) {
        StringBuilder sqlBuilder = new StringBuilder();
        if (!CollectionUtil.isEmpty(executionFields)) {
            sqlBuilder.append("UPDATE ")
                    .append(this.getTableName())
                    .append(" SET ")
                    .append(executionFields.stream()
                            .map(executionField -> executionField + " = ?")
                            .collect(Collectors.joining(", ")));
        } else {
            throw new NoneExecutionException();
        }
        return sqlBuilder;
    }

    @Override
    public StringBuilder buildUpdateSQLWithParam(SQLParam param) {
        StringBuilder sqlBuilder = new StringBuilder();
        if (!BeanUtil.isEmpty(param)) {
            sqlBuilder.append(buildUpdateSQL(param.getExecutionFields()))
                    .append(buildWhereSQLWithParam(param));
        }
        return sqlBuilder;
    }

    @Override
    public StringBuilder buildSelectSQL(Collection<String> executionFields) {
        StringBuilder sqlBuilder = new StringBuilder();
        if (!CollectionUtil.isEmpty(executionFields)) {
            sqlBuilder.append("SELECT ")
                    .append(String.join(", ", executionFields))
                    .append(" FROM ")
                    .append(this.getTableName());
        } else {
            throw new NoneExecutionException();
        }
        return sqlBuilder;
    }

    @Override
    public StringBuilder buildSelectSQLWithParam(SQLParam param) {
        StringBuilder sqlBuilder = new StringBuilder();
        if (!BeanUtil.isEmpty(param)) {
            sqlBuilder.append(buildSelectSQL(param.getExecutionFields()))
                    .append(buildWhereSQLWithParam(param));
        }
        return sqlBuilder;
    }

    @Override
    public StringBuilder buildWhereSQL(Collection<String> conditionFields, String delimiter) {
        StringBuilder sqlBuilder = new StringBuilder();
        if (CollectionUtil.isEmpty(conditionFields)) {
            return sqlBuilder;
        }
        return sqlBuilder.append(" WHERE ")
                .append(conditionFields.stream()
                        .map(conditionField -> conditionField + " = ?")
                        .collect(Collectors.joining(" " + delimiter + " ")));
    }

    @Override
    public StringBuilder buildWhereSQLWithParam(SQLParam param) {
        StringBuilder sqlBuilder = new StringBuilder();
        if (!BeanUtil.isEmpty(param)) {
            if (!BeanUtil.isEmpty(param.getConditionFields())) {
                sqlBuilder.append(buildWhereSQL(param.getConditionFields(), "AND"))
                        .append(" AND ");
            }
            if (sqlBuilder.indexOf("WHERE") == -1
                    && (!CollectionUtil.isEmpty(param.getRangeParam())
                    || !BeanUtil.isEmpty(param.getSimilarMap())
                    || !BeanUtil.isEmpty(param.getPreSimilarMap())
                    || !BeanUtil.isEmpty(param.getSuffixSimilarMap()))) {
                sqlBuilder.append(" WHERE ");
            }
            if (!CollectionUtil.isEmpty(param.getRangeParam())) {
                sqlBuilder.append(buildRangeSQL(param.getRangeParam(), "AND"))
                        .append(" AND ");
            }
            if (!BeanUtil.isEmpty(param.getSimilarMap())) {
                sqlBuilder.append(buildSimilarSQL(param.getSimilarMap(), "OR"))
                        .append(" AND ");
            }
            if (!BeanUtil.isEmpty(param.getPreSimilarMap())) {
                sqlBuilder.append(buildPreSimilarSQL(param.getPreSimilarMap(), "OR"))
                        .append(" AND ");
            }
            if (!BeanUtil.isEmpty(param.getSuffixSimilarMap())) {
                sqlBuilder.append(buildSuffixSimilarSQL(param.getSuffixSimilarMap(), "OR"));
            }
            if (sqlBuilder.toString().trim().endsWith("AND")) {
                sqlBuilder = new StringBuilder(sqlBuilder.substring(0, sqlBuilder.lastIndexOf("AND")));
            }
            if (!CollectionUtil.isEmpty(param.getOrderParam())) {
                sqlBuilder.append(buildOrderSQL(param.getOrderParam(), ", "));
            }
            if (!BeanUtil.isEmpty(param.getPageParam())) {
                sqlBuilder.append(buildLimitSQL(param.getPageParam()));
            }
        }
        return sqlBuilder;
    }

    @Override
    public StringBuilder buildSimilarSQL(Map<String, Object> similarMap, String delimiter) {
        StringBuilder sqlBuilder = new StringBuilder();
        Set<String> fields = similarMap.keySet();
        if (CollectionUtil.isEmpty(fields)) {
            return sqlBuilder;
        }
        Map<String, Object> similarMapClone = new HashMap<>(similarMap);
        similarMapClone.entrySet().forEach(entry -> {
            if (!entry.getValue().toString().contains("%")) {
                similarMap.put(entry.getKey(), "%" + entry.getValue() + "%");
            }
        });
        return sqlBuilder.append(" (")
                .append(fields.stream()
                        .map(conditionField -> conditionField + " LIKE ?")
                        .collect(Collectors.joining(" " + delimiter + " ")))
                .append(") ");
    }

    @Override
    public StringBuilder buildPreSimilarSQL(Map<String, Object> similarMap, String delimiter) {
        Map<String, Object> similarMapClone = new HashMap<>(similarMap);
        similarMapClone.entrySet().forEach(entry -> {
            similarMap.put(entry.getKey(), "%" + entry.getValue());
        });
        return buildSimilarSQL(similarMap, delimiter);
    }

    @Override
    public StringBuilder buildSuffixSimilarSQL(Map<String, Object> similarMap, String delimiter) {
        Map<String, Object> similarMapClone = new HashMap<>(similarMap);
        similarMapClone.entrySet().forEach(entry -> {
            similarMap.put(entry.getKey(), entry.getValue() + "%");
        });
        return buildSimilarSQL(similarMap, delimiter);
    }

    @Override
    public StringBuilder buildRangeSQL(Collection<RangeParam> rangeParams, String delimiter) {
        StringBuilder sqlBuilder = new StringBuilder();
        if (!CollectionUtil.isEmpty(rangeParams)) {
            sqlBuilder.append(rangeParams.stream()
                    .map(rangeParam -> rangeParam.getFieldName() +
                            (rangeParam.getRangeSymbol().equals(RangeSymbol.BETWEEN_AND)
                                    ? " BETWEEN " + rangeParam.getLower() + " AND " + rangeParam.getUpper()
                                    : rangeParam.getRangeSymbol().getSymbol() +
                                    (BeanUtil.isEmpty(rangeParam.getLower()) ? rangeParam.getUpper() : rangeParam.getLower())))
                    .collect(Collectors.joining(" " + delimiter + " ")));
        }
        return sqlBuilder;
    }

    @Override
    public StringBuilder buildOrderSQL(Collection<OrderParam> orderParams, String delimiter) {
        StringBuilder sqlBuilder = new StringBuilder();
        if (!CollectionUtil.isEmpty(orderParams)) {
            if (orderParams.stream().anyMatch(BeanUtil::isAttributesEmpty)) {
                throw new NoneAttributeException();
            }
            sqlBuilder.append(" ORDER BY ")
                    .append(orderParams.stream()
                            .map(orderParam -> orderParam.getFieldName() + orderParam.getOrderSymbol().getSymbol())
                            .collect(Collectors.joining(delimiter + " ")));
        }
        return sqlBuilder;
    }

    @Override
    public StringBuilder buildLimitSQL(PageParam pageParam) {
        StringBuilder sqlBuilder = new StringBuilder();
        if (!BeanUtil.isEmpty(pageParam)) {
            if (BeanUtil.isAttributesEmpty(pageParam)) {
                throw new NoneAttributeException();
            }
            int offset = (pageParam.getCurrent() == 1 ? 1 : pageParam.getCurrent() - 1) * pageParam.getPageSize();
            sqlBuilder.append(" LIMIT ")
                    .append(offset)
                    .append(", ")
                    .append(pageParam.getPageSize());
        }
        return sqlBuilder;
    }
}
