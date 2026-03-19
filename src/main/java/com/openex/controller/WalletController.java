package com.openex.controller;

import com.openex.common.Currency;
import com.openex.dto.WalletDto;
import com.openex.repository.WalletRepository;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for wallet-related operations.
 */
@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    private final ObjectProvider<WalletRepository> walletRepositoryProvider;

    public WalletController(ObjectProvider<WalletRepository> walletRepositoryProvider) {
        this.walletRepositoryProvider = walletRepositoryProvider;
    }

    /**
     * Get a wallet snapshot for a user and currency.
     */
    @GetMapping("/{userId}/{currency}")
    public ResponseEntity<WalletDto> getWallet(
            @PathVariable UUID userId,
            @PathVariable Currency currency
    ) {
        WalletRepository repo = walletRepositoryProvider.getIfAvailable();
        if (repo == null) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
        return repo.findByUserIdAndCurrency(userId, currency)
                .map(s -> ResponseEntity.ok(new WalletDto(s.userId(), s.currency(), s.available(), s.locked())))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * Upsert wallet balances (available and locked) for a user and currency.
     */
    @PutMapping("/{userId}/{currency}")
    public ResponseEntity<WalletDto> upsertBalances(
            @PathVariable UUID userId,
            @PathVariable Currency currency,
            @RequestBody WalletDto body
    ) {
        WalletRepository repo = walletRepositoryProvider.getIfAvailable();
        if (repo == null) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
        if (body == null || body.available() == null || body.locked() == null) {
            return ResponseEntity.badRequest().build();
        }
        if (body.userId() != null && !userId.equals(body.userId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        if (body.currency() != null && currency != body.currency()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        repo.updateBalances(userId, currency, body.available(), body.locked());
        return repo.findByUserIdAndCurrency(userId, currency)
                .map(s -> ResponseEntity.ok(new WalletDto(s.userId(), s.currency(), s.available(), s.locked())))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }
}
