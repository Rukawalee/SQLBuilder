package com.rukawa.sql.enumeration;

import com.rukawa.common.util.StringUtil;

import java.util.Arrays;
import java.util.Optional;

/**
 * 范围符号
 */
public enum RangeSymbol {

    LESS_THAN(" < "),
    LESS_THAN_OR_EQUAL(" <= "),
    GREATER_THAN(" > "),
    GREATER_THAN_OR_EQUAL(" >= "),
    BETWEEN_AND("");

    private String symbol;

    RangeSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public static RangeSymbol getRangeSymbol(String symbol) {
        if (StringUtil.isEmpty(symbol)) {
            return BETWEEN_AND;
        }
        Optional<RangeSymbol> rangeSymbolOptional = Arrays.stream(values())
                .filter(value -> value.symbol.trim().equals(symbol.trim()))
                .findFirst();
        return rangeSymbolOptional.isPresent()
                ? rangeSymbolOptional.get()
                : BETWEEN_AND;
    }
}
