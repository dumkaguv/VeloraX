package me.didk.velorax.wallet.service;

import me.didk.common.exception.ConflictException;
import me.didk.velorax.wallet.domain.WalletBalanceEntity;
import me.didk.velorax.wallet.repository.WalletBalanceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class WalletService {
    public static final String ENTRY_CREDIT_AVAILABLE = "CREDIT_AVAILABLE";
    public static final String ENTRY_DEBIT_AVAILABLE = "DEBIT_AVAILABLE";
    public static final String ENTRY_CREDIT_LOCKED = "CREDIT_LOCKED";
    public static final String ENTRY_DEBIT_LOCKED = "DEBIT_LOCKED";
    public static final String ENTRY_CREDIT_TRADE_SETTLEMENT = "CREDIT_TRADE_SETTLEMENT";
    public static final String ENTRY_CREDIT_WITHDRAWAL_SETTLEMENT = "CREDIT_WITHDRAWAL_SETTLEMENT";

    private final WalletBalanceRepository walletBalanceRepository;
    private final LedgerService ledgerService;

    public WalletService(
            WalletBalanceRepository walletBalanceRepository,
            LedgerService ledgerService
    ) {
        this.walletBalanceRepository = walletBalanceRepository;
        this.ledgerService = ledgerService;
    }

    @Transactional(readOnly = true)
    public List<WalletBalanceEntity> getBalances(UUID userId) {
        return walletBalanceRepository.findAllByUserIdOrderByAssetAsc(userId);
    }

    @Transactional
    public WalletBalanceEntity creditAvailable(
            UUID userId,
            String asset,
            BigDecimal amount,
            String referenceType,
            UUID referenceId
    ) {
        validateAmount(amount);
        String normalizedAsset = normalizeAsset(asset);
        WalletBalanceEntity balance = getOrCreateForUpdate(userId, normalizedAsset);

        balance.setAvailable(balance.getAvailable().add(amount));
        walletBalanceRepository.save(balance);

        ledgerService.recordEntry(userId, normalizedAsset, ENTRY_CREDIT_AVAILABLE, amount, referenceType, referenceId);
        return balance;
    }

    @Transactional
    public WalletBalanceEntity debitAvailable(
            UUID userId,
            String asset,
            BigDecimal amount,
            String referenceType,
            UUID referenceId
    ) {
        validateAmount(amount);
        String normalizedAsset = normalizeAsset(asset);
        WalletBalanceEntity balance = getOrCreateForUpdate(userId, normalizedAsset);

        if (balance.getAvailable().compareTo(amount) < 0) {
            throw new ConflictException("Insufficient available balance");
        }

        balance.setAvailable(balance.getAvailable().subtract(amount));
        walletBalanceRepository.save(balance);

        ledgerService.recordEntry(userId, normalizedAsset, ENTRY_DEBIT_AVAILABLE, amount, referenceType, referenceId);
        return balance;
    }

    @Transactional
    public WalletBalanceEntity lockFunds(
            UUID userId,
            String asset,
            BigDecimal amount,
            String referenceType,
            UUID referenceId
    ) {
        validateAmount(amount);
        String normalizedAsset = normalizeAsset(asset);
        WalletBalanceEntity balance = getOrCreateForUpdate(userId, normalizedAsset);

        if (balance.getAvailable().compareTo(amount) < 0) {
            throw new ConflictException("Insufficient available balance to lock");
        }

        balance.setAvailable(balance.getAvailable().subtract(amount));
        balance.setLocked(balance.getLocked().add(amount));
        walletBalanceRepository.save(balance);

        // Simplified double-entry: available decreases, locked increases.
        ledgerService.recordEntry(userId, normalizedAsset, ENTRY_DEBIT_AVAILABLE, amount, referenceType, referenceId);
        ledgerService.recordEntry(userId, normalizedAsset, ENTRY_CREDIT_LOCKED, amount, referenceType, referenceId);
        return balance;
    }

    @Transactional
    public WalletBalanceEntity releaseFunds(
            UUID userId,
            String asset,
            BigDecimal amount,
            String referenceType,
            UUID referenceId
    ) {
        validateAmount(amount);
        String normalizedAsset = normalizeAsset(asset);
        WalletBalanceEntity balance = getOrCreateForUpdate(userId, normalizedAsset);

        if (balance.getLocked().compareTo(amount) < 0) {
            throw new ConflictException("Insufficient locked balance to release");
        }

        balance.setLocked(balance.getLocked().subtract(amount));
        balance.setAvailable(balance.getAvailable().add(amount));
        walletBalanceRepository.save(balance);

        ledgerService.recordEntry(userId, normalizedAsset, ENTRY_DEBIT_LOCKED, amount, referenceType, referenceId);
        ledgerService.recordEntry(userId, normalizedAsset, ENTRY_CREDIT_AVAILABLE, amount, referenceType, referenceId);
        return balance;
    }

    @Transactional
    public WalletBalanceEntity applyFill(
            UUID userId,
            String asset,
            BigDecimal amount,
            String referenceType,
            UUID referenceId
    ) {
        validateAmount(amount);
        String normalizedAsset = normalizeAsset(asset);
        WalletBalanceEntity balance = getOrCreateForUpdate(userId, normalizedAsset);

        if (balance.getLocked().compareTo(amount) < 0) {
            throw new ConflictException("Insufficient locked balance to apply fill");
        }

        balance.setLocked(balance.getLocked().subtract(amount));
        walletBalanceRepository.save(balance);

        ledgerService.recordEntry(userId, normalizedAsset, ENTRY_DEBIT_LOCKED, amount, referenceType, referenceId);
        ledgerService.recordEntry(
                userId,
                normalizedAsset,
                ENTRY_CREDIT_TRADE_SETTLEMENT,
                amount,
                referenceType,
                referenceId
        );
        return balance;
    }

    @Transactional
    public WalletBalanceEntity settleLocked(
            UUID userId,
            String asset,
            BigDecimal amount,
            String referenceType,
            UUID referenceId
    ) {
        validateAmount(amount);
        String normalizedAsset = normalizeAsset(asset);
        WalletBalanceEntity balance = getOrCreateForUpdate(userId, normalizedAsset);

        if (balance.getLocked().compareTo(amount) < 0) {
            throw new ConflictException("Insufficient locked balance to settle");
        }

        balance.setLocked(balance.getLocked().subtract(amount));
        walletBalanceRepository.save(balance);

        ledgerService.recordEntry(userId, normalizedAsset, ENTRY_DEBIT_LOCKED, amount, referenceType, referenceId);
        ledgerService.recordEntry(
                userId,
                normalizedAsset,
                ENTRY_CREDIT_WITHDRAWAL_SETTLEMENT,
                amount,
                referenceType,
                referenceId
        );
        return balance;
    }

    private WalletBalanceEntity getOrCreateForUpdate(UUID userId, String asset) {
        return walletBalanceRepository.findForUpdate(userId, asset)
                .orElseGet(() -> {
                    WalletBalanceEntity balance = new WalletBalanceEntity();
                    balance.setUserId(userId);
                    balance.setAsset(asset);
                    balance.setAvailable(BigDecimal.ZERO);
                    balance.setLocked(BigDecimal.ZERO);
                    return walletBalanceRepository.save(balance);
                });
    }

    private static String normalizeAsset(String asset) {
        if (asset == null || asset.isBlank()) {
            throw new IllegalArgumentException("Asset is required");
        }
        return asset.trim().toUpperCase();
    }

    private static void validateAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
    }
}
