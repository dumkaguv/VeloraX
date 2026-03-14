package me.didk.velorax.wallet.repository;

import me.didk.velorax.wallet.domain.WalletAddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WalletAddressRepository extends JpaRepository<WalletAddressEntity, UUID> {
    List<WalletAddressEntity> findAllByUserIdOrderByCreatedAtDesc(UUID userId);

    Optional<WalletAddressEntity> findByUserIdAndAssetAndNetwork(UUID userId, String asset, String network);
}
