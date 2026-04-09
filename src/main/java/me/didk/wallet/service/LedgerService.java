package me.didk.wallet.service;

import me.didk.wallet.domain.WalletLedgerEntryEntity;
import me.didk.wallet.repository.WalletLedgerEntryRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class LedgerService {
    private final WalletLedgerEntryRepository walletLedgerEntryRepository;

    public LedgerService(WalletLedgerEntryRepository walletLedgerEntryRepository) {
        this.walletLedgerEntryRepository = walletLedgerEntryRepository;
    }

    public void recordEntry(
            UUID userId,
            String asset,
            String entryType,
            BigDecimal amount,
            String referenceType,
            UUID referenceId
    ) {
        WalletLedgerEntryEntity entry = new WalletLedgerEntryEntity();
        entry.setUserId(userId);
        entry.setAsset(asset);
        entry.setEntryType(entryType);
        entry.setAmount(amount);
        entry.setReferenceType(referenceType);
        entry.setReferenceId(referenceId);
        walletLedgerEntryRepository.save(entry);
    }
}
