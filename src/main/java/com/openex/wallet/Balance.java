package com.openex.wallet;

import com.openex.common.AmountUtil;
import com.openex.common.Currency;

import java.math.BigDecimal;
import java.util.Objects;

class Balance {
    private final Currency currency;
    private BigDecimal available;
    private BigDecimal reserved;

    Balance(Currency currency) {
        this.currency = Objects.requireNonNull(currency);
        this.available = BigDecimal.ZERO.setScale(currency.getScale(), currency.getDefaultRounding());
        this.reserved = BigDecimal.ZERO.setScale(currency.getScale(), currency.getDefaultRounding());
    }

    Currency getCurrency() {
        return currency;
    }

    BigDecimal getAvailable() {
        return available;
    }

    BigDecimal getReserved() {
        return reserved;
    }

    void deposit(BigDecimal amount) {
        AmountUtil.requirePositive(amount, "amount");
        available = available.add(AmountUtil.scale(amount, currency));
    }

    void withdraw(BigDecimal amount) {
        AmountUtil.requirePositive(amount, "amount");
        BigDecimal a = AmountUtil.scale(amount, currency);
        if (available.compareTo(a) < 0) {
            throw new com.openex.common.exceptions.InsufficientFundsException("Insufficient available " + currency + " to withdraw");
        }
        available = available.subtract(a);
    }

    void reserve(BigDecimal amount) {
        AmountUtil.requirePositive(amount, "amount");
        BigDecimal a = AmountUtil.scale(amount, currency);
        if (available.compareTo(a) < 0) {
            throw new com.openex.common.exceptions.InsufficientFundsException("Insufficient available " + currency + " to reserve");
        }
        available = available.subtract(a);
        reserved = reserved.add(a);
    }

    void release(BigDecimal amount) {
        AmountUtil.requirePositive(amount, "amount");
        BigDecimal a = AmountUtil.scale(amount, currency);
        if (reserved.compareTo(a) < 0) {
            throw new IllegalStateException("Cannot release more than reserved in " + currency);
        }
        reserved = reserved.subtract(a);
        available = available.add(a);
    }

    void consumeFromReserve(BigDecimal amount) {
        AmountUtil.requirePositive(amount, "amount");
        BigDecimal a = AmountUtil.scale(amount, currency);
        if (reserved.compareTo(a) < 0) {
            throw new IllegalStateException("Cannot consume more than reserved in " + currency);
        }
        reserved = reserved.subtract(a);
        // consumed funds are not returned to available
    }
}
