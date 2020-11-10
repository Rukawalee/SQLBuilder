package com.rukawa.sql.enumeration;

import com.rukawa.sql.util.StringUtil;

import java.util.Arrays;
import java.util.Optional;

/**
 * 排序符号
 */
public enum OrderSymbol {

    ASC(" ASC"),
    DESC(" DESC"),
    DEFAULT(" ASC");

    private String symbol;

    OrderSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public static OrderSymbol getOrderSymbol(String symbol) {
        if (StringUtil.isEmpty(symbol)) {
            return DEFAULT;
        }
        Optional<OrderSymbol> orderSymbolOptional = Arrays.stream(values())
                .filter(value -> value.symbol.trim().equalsIgnoreCase(symbol.trim()))
                .findFirst();
        return orderSymbolOptional.isPresent()
                ? orderSymbolOptional.get()
                : DEFAULT;
    }
}
