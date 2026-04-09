package me.didk.wallet.repository;

import me.didk.wallet.domain.WalletLedgerEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WalletLedgerEntryRepository extends JpaRepository<WalletLedgerEntryEntity, UUID> {
    List<WalletLedgerEntryEntity> findAllByReferenceTypeAndReferenceId(String referenceType, UUID referenceId);
}
