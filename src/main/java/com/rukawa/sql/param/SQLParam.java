package com.rukawa.sql.param;

import lombok.Data;

import java.util.Collection;
import java.util.Map;

@Data
public class SQLParam {

    /**
     * 条件字段
     */
    private Collection<String> conditionFields;

    /**
     * 映射字段
     */
    private Collection<String> executionFields;

    /**
     * 全模糊查询字段
     */
    private Map<String, Object> similarMap;

    /**
     * 前模糊查询字段
     */
    private Map<String, Object> preSimilarMap;

    /**
     * 后模糊查询字段
     */
    private Map<String, Object> suffixSimilarMap;

    /**
     * 范围擦查询字段
     */
    private Collection<RangeParam> rangeParam;

    /**
     * 分页查询字段
     */
    private PageParam pageParam;

    /**
     * 排序查询字段
     */
    private Collection<OrderParam> orderParam;
}
