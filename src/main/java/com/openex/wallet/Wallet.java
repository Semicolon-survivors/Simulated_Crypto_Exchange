package com.openex.wallet;

import com.openex.common.Currency;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

class Wallet {
    private final UUID userId;
    private final Map<Currency, Balance> balances;
    private final ReentrantLock lock = new ReentrantLock(true);

    Wallet(UUID userId) {
        this.userId = Objects.requireNonNull(userId, "userId");
        this.balances = new EnumMap<>(Currency.class);
        // initialize supported currencies
        for (Currency c : Currency.values()) {
            this.balances.put(c, new Balance(c));
        }
    }

    UUID getUserId() {
        return userId;
    }

    Balance getBalance(Currency currency) {
        return balances.get(currency);
    }

    ReentrantLock lock() {
        return lock;
    }
}
