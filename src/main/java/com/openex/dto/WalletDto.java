package com.openex.dto;

import com.openex.common.Currency;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Wallet DTO representing balances for a user's currency wallet.
 */
public record WalletDto(
        UUID userId,
        Currency currency,
        BigDecimal available,
        BigDecimal locked
) {
    public WalletDto {
        if (userId == null) {
            throw new IllegalArgumentException("userId is required");
        }
        if (currency == null) {
            throw new IllegalArgumentException("currency is required");
        }
        if (available == null || locked == null) {
            throw new IllegalArgumentException("available and locked are required");
        }
    }

    /**
     * Total balance = available + locked.
     */
    public BigDecimal total() {
        return available.add(locked);
    }
}
