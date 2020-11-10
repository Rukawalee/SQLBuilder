package com.rukawa.sql.param;

import com.rukawa.sql.enumeration.OrderSymbol;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 排序参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderParam {

    // 排序字段
    private String fieldName;

    // 排序符号
    private OrderSymbol orderSymbol;
}
