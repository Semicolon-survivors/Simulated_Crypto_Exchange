package com.openex.wallet;

import com.openex.common.Currency;

import java.math.BigDecimal;
import java.util.UUID;

public interface WalletService {
    void createWallet(UUID userId);

    boolean walletExists(UUID userId);

    BigDecimal getAvailable(UUID userId, Currency currency);

    BigDecimal getReserved(UUID userId, Currency currency);

    void deposit(UUID userId, Currency currency, BigDecimal amount);

    void withdraw(UUID userId, Currency currency, BigDecimal amount);

    void reserve(UUID userId, Currency currency, BigDecimal amount);

    void releaseReserve(UUID userId, Currency currency, BigDecimal amount);

    void consumeFromReserve(UUID userId, Currency currency, BigDecimal amount);
}
