package com.openex.common;

import java.math.RoundingMode;

/**
 * Supported currencies with standard scales and rounding modes.
 */
public enum Currency {
    BTC(8, RoundingMode.DOWN), // Satoshis precision
    USD(2, RoundingMode.HALF_UP); // Fiat precision

    private final int scale;
    private final RoundingMode defaultRounding;

    Currency(int scale, RoundingMode defaultRounding) {
        this.scale = scale;
        this.defaultRounding = defaultRounding;
    }

    public int getScale() {
        return scale;
    }

    public RoundingMode getDefaultRounding() {
        return defaultRounding;
    }
}
