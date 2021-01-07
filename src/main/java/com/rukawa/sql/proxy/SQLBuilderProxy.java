package com.rukawa.sql.proxy;

import com.rukawa.common.util.BeanUtil;
import com.rukawa.common.util.CamelCaseUtil;
import com.rukawa.common.util.CollectionUtil;
import com.rukawa.sql.configuration.SQLBuilderConfiguration;
import com.rukawa.sql.enumeration.SQLBuilderExecutorClassType;
import com.rukawa.sql.enumeration.SimilarType;
import com.rukawa.sql.exception.UniqueSimilarException;
import com.rukawa.sql.param.OrderParam;
import com.rukawa.sql.param.RangeParam;
import com.rukawa.sql.param.SQLParam;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SQLBuilderProxy implements InvocationHandler {

    private Object object;

    private SQLBuilderConfiguration sqlBuilderConfiguration;

    public SQLBuilderProxy(Object object, SQLBuilderConfiguration sqlBuilderConfiguration) {
        this.object = object;
        this.sqlBuilderConfiguration = sqlBuilderConfiguration;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (!filterObjectMethod(method.getName())) {
            if (!BeanUtil.isEmpty(args)) {
                if (args[0] instanceof Collection) {
                    args[0] = before((Collection) args[0]);
                }
                if (args[0] instanceof SQLParam) {
                    SQLParam sqlParam = (SQLParam) args[0];
                    beforeCheckSimilar(sqlParam);
                    beforeWithParam(sqlParam);
                    args[0] = sqlParam;
                }
            }
        }
        if (method.getName().contains("toString")) {
            return method.invoke(object, args);
        }
        StringBuilder sqlBuilder = (StringBuilder) method.invoke(object, args);
        SimilarType similarType = SimilarType.getInstance(method.getName().toLowerCase());
        if (!BeanUtil.isEmpty(args)) {
            if (args[0] instanceof SQLParam) {
                SQLParam sqlParam = (SQLParam) args[0];
                afterSimilarWithParam(sqlParam, sqlBuilder);
                afterWithParam(sqlParam, sqlBuilder);
            }
            if (!similarType.equals(SimilarType.OTHER)) {
                afterSimilar(args[0], sqlBuilder);
            } else {
                if (args[0] instanceof Collection) {
                    after((Collection) args[0], sqlBuilder);
                }
            }
        }
        String sqlBuilderStr = sqlBuilder.toString().trim();
        if (sqlBuilderStr.endsWith("AND") || sqlBuilderStr.endsWith("OR")) {
            sqlBuilderStr = sqlBuilderStr
                    .substring(0, sqlBuilderStr.lastIndexOf(" "));
        }
        return new StringBuilder(sqlBuilderStr.replaceAll("\\s+", " "));
    }

    private Object before(Collection fields) {
        if (!BeanUtil.isEmpty(sqlBuilderConfiguration) && sqlBuilderConfiguration.isCamelCase()) {
            return fields.stream()
                    .map(field -> {
                        if (field instanceof OrderParam) {
                            OrderParam orderParam = (OrderParam) field;
                            orderParam.setFieldName(CamelCaseUtil.mapCamelCaseToUnderScore(orderParam.getFieldName()));
                            return orderParam;
                        } else if (field instanceof RangeParam) {
                            RangeParam rangeParam = (RangeParam) field;
                            rangeParam.setFieldName(CamelCaseUtil.mapCamelCaseToUnderScore(rangeParam.getFieldName()));
                            return rangeParam;
                        } else {
                            return CamelCaseUtil.mapCamelCaseToUnderScore(field.toString());
                        }
                    })
                    .collect(Collectors.toList());
        }
        return null;
    }

    private void beforeWithParam(SQLParam sqlParam) {
        if (!CollectionUtil.isEmpty(sqlParam.getConditionFields())) {
            sqlParam.setConditionFields((Collection<String>) before(sqlParam.getConditionFields()));
        }
        if (!CollectionUtil.isEmpty(sqlParam.getExecutionFields())) {
            sqlParam.setExecutionFields((Collection<String>) before(sqlParam.getExecutionFields()));
        }
        if (!CollectionUtil.isEmpty(sqlParam.getOrderParam())) {
            sqlParam.setOrderParam((Collection<OrderParam>) before(sqlParam.getOrderParam()));
        }
        if (!CollectionUtil.isEmpty(sqlParam.getRangeParam())) {
            sqlParam.setRangeParam((Collection<RangeParam>) before(sqlParam.getRangeParam()));
        }
    }

    private void beforeCheckSimilar(SQLParam sqlParam) throws UniqueSimilarException {
        Set<String> fieldSet = new HashSet<>();
        if (!BeanUtil.isEmpty(sqlParam.getSimilarMap())) {
            fieldSet.addAll(sqlParam.getSimilarMap().keySet());
        }
        if (!BeanUtil.isEmpty(sqlParam.getPreSimilarMap())) {
            Set<String> preFieldSet = sqlParam.getPreSimilarMap().keySet();
            if (CollectionUtil.retains(fieldSet, preFieldSet)) {
                throw new UniqueSimilarException();
            }
            fieldSet.addAll(preFieldSet);
        }
        if (!BeanUtil.isEmpty(sqlParam.getSuffixSimilarMap())) {
            Set<String> suffixFieldSet = sqlParam.getSuffixSimilarMap().keySet();
            if (CollectionUtil.retains(fieldSet, suffixFieldSet)) {
                throw new UniqueSimilarException();
            }
        }
    }

    private boolean filterObjectMethod(String methodName) {
        Collection<String> objectMethods = BeanUtil.getObjectMethods(Object.class);
        return objectMethods.contains(methodName);
    }

    private void afterSimilar(Object fieldMap, Object sqlBuilder) {
        Map fieldMapClone = (Map) fieldMap;
        StringBuilder sqlBuilderClone = (StringBuilder) sqlBuilder;
        if (!CollectionUtil.isEmpty(fieldMapClone.keySet())) {
            for (Object key : fieldMapClone.keySet()) {
                if (!BeanUtil.isEmpty(sqlBuilderConfiguration)) {
                    if (sqlBuilderConfiguration.isCamelCase()) {
                        String fieldKey = key.toString();
                        int offset = sqlBuilderClone.indexOf(fieldKey);
                        int fieldKeyLen = fieldKey.length();
                        sqlBuilderClone.replace(offset, offset + fieldKeyLen, CamelCaseUtil.mapCamelCaseToUnderScore(key.toString()));
                    }
                    generateSQLBuilderByExecutor(sqlBuilderClone, key);
                }
            }
        }
    }

    private void afterSimilarWithParam(SQLParam sqlParam, StringBuilder sqlBuilder) {
        if (!BeanUtil.isEmpty(sqlParam.getSimilarMap())) {
            afterSimilar(sqlParam.getSimilarMap(), sqlBuilder);
        }
        if (!BeanUtil.isEmpty(sqlParam.getPreSimilarMap())) {
            afterSimilar(sqlParam.getPreSimilarMap(), sqlBuilder);
        }
        if (!BeanUtil.isEmpty(sqlParam.getSuffixSimilarMap())) {
            afterSimilar(sqlParam.getSuffixSimilarMap(), sqlBuilder);
        }
    }

    private void after(Collection fields, Object sqlBuilder) {
        if (!(CollectionUtil.isEmpty(fields) || BeanUtil.isEmpty(sqlBuilder) || BeanUtil.isEmpty(sqlBuilderConfiguration))) {
            StringBuilder sqlBuilderClone = (StringBuilder) sqlBuilder;
            fields.forEach(field -> {
                if (sqlBuilderConfiguration.isCamelCase()) {
                    field = CamelCaseUtil.mapUnderScoreToCamelCase(field.toString());
                }
                generateSQLBuilderByExecutor(sqlBuilderClone, field);
            });
        }
    }

    private void afterWithParam(SQLParam sqlParam, StringBuilder sqlBuilder) {
        if (!CollectionUtil.isEmpty(sqlParam.getConditionFields())) {
            after(sqlParam.getConditionFields(), sqlBuilder);
        }
    }

    private void generateSQLBuilderByExecutor(StringBuilder sqlBuilderClone, Object field) {
        switch (SQLBuilderExecutorClassType.getInstance(sqlBuilderConfiguration.getSqlBuilderExecutorClass(),
                sqlBuilderConfiguration.getSqlBuilderExecutorClassName())) {
            case JDBC_TEMPLATE:
                int offset = -1;
                int likeOffset = -1;
                int keyLen = 1;
                if ((likeOffset = sqlBuilderClone.indexOf("LIKE ?")) != -1) {
                    offset = sqlBuilderClone.indexOf("LIKE ?");
                    keyLen = "LIKE ?".length();
                } else {
                    offset = sqlBuilderClone.indexOf("?");
                }
                if (offset != -1) {
                    if (likeOffset == -1) {
                        sqlBuilderClone.replace(offset, offset + keyLen, ":" + field);
                    } else {
                        sqlBuilderClone.replace(offset, offset + keyLen, "LIKE :" + field);
                    }
                }
                break;
            default:
                break;
        }
    }
}
