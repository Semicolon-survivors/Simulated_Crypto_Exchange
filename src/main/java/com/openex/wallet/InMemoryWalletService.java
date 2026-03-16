package com.openex.wallet;

import com.openex.common.AmountUtil;
import com.openex.common.Currency;
import com.openex.common.exceptions.WalletNotFoundException;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

public class InMemoryWalletService implements WalletService {
    private final ConcurrentMap<UUID, Wallet> wallets = new ConcurrentHashMap<>();

    @Override
    public void createWallet(UUID userId) {
        Objects.requireNonNull(userId, "userId");
        wallets.computeIfAbsent(userId, Wallet::new);
    }

    @Override
    public boolean walletExists(UUID userId) {
        return wallets.containsKey(userId);
    }

    private Wallet get(UUID userId) {
        Wallet w = wallets.get(userId);
        if (w == null) throw new WalletNotFoundException(userId);
        return w;
    }

    @Override
    public BigDecimal getAvailable(UUID userId, Currency currency) {
        Wallet w = get(userId);
        ReentrantLock lock = w.lock();
        lock.lock();
        try {
            return w.getBalance(currency).getAvailable();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public BigDecimal getReserved(UUID userId, Currency currency) {
        Wallet w = get(userId);
        ReentrantLock lock = w.lock();
        lock.lock();
        try {
            return w.getBalance(currency).getReserved();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void deposit(UUID userId, Currency currency, BigDecimal amount) {
        Objects.requireNonNull(amount, "amount");
        Wallet w = get(userId);
        ReentrantLock lock = w.lock();
        lock.lock();
        try {
            w.getBalance(currency).deposit(AmountUtil.scale(amount, currency));
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void withdraw(UUID userId, Currency currency, BigDecimal amount) {
        Objects.requireNonNull(amount, "amount");
        Wallet w = get(userId);
        ReentrantLock lock = w.lock();
        lock.lock();
        try {
            w.getBalance(currency).withdraw(AmountUtil.scale(amount, currency));
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void reserve(UUID userId, Currency currency, BigDecimal amount) {
        Objects.requireNonNull(amount, "amount");
        Wallet w = get(userId);
        ReentrantLock lock = w.lock();
        lock.lock();
        try {
            w.getBalance(currency).reserve(AmountUtil.scale(amount, currency));
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void releaseReserve(UUID userId, Currency currency, BigDecimal amount) {
        Objects.requireNonNull(amount, "amount");
        Wallet w = get(userId);
        ReentrantLock lock = w.lock();
        lock.lock();
        try {
            w.getBalance(currency).release(AmountUtil.scale(amount, currency));
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void consumeFromReserve(UUID userId, Currency currency, BigDecimal amount) {
        Objects.requireNonNull(amount, "amount");
        Wallet w = get(userId);
        ReentrantLock lock = w.lock();
        lock.lock();
        try {
            w.getBalance(currency).consumeFromReserve(AmountUtil.scale(amount, currency));
        } finally {
            lock.unlock();
        }
    }
}
