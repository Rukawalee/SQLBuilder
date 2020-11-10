package com.rukawa.sql.param;

import com.rukawa.sql.enumeration.RangeSymbol;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 范围参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RangeParam {

    // 字段名
    private String fieldName;

    // 下限
    private Object lower;

    // 上限
    private Object upper;

    // 范围
    private RangeSymbol rangeSymbol;
}
