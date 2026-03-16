package com.openex.common;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class AmountUtil {
    private AmountUtil() {}

    public static BigDecimal scale(BigDecimal value, Currency currency) {
        Objects.requireNonNull(value, "value");
        return value.setScale(currency.getScale(), currency.getDefaultRounding());
    }

    public static BigDecimal scaleUp(BigDecimal value, Currency currency) {
        Objects.requireNonNull(value, "value");
        return value.setScale(currency.getScale(), RoundingMode.UP);
    }

    public static void requirePositive(BigDecimal value, String name) {
        Objects.requireNonNull(value, name);
        if (value.signum() <= 0) {
            throw new IllegalArgumentException(name + " must be > 0");
        }
    }

    public static boolean gte(BigDecimal a, BigDecimal b) {
        return a.compareTo(b) >= 0;
    }
}
