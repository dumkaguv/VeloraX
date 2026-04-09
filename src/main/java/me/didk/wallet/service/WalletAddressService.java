package me.didk.wallet.service;

import me.didk.wallet.domain.WalletAddressEntity;
import me.didk.wallet.repository.WalletAddressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class WalletAddressService {
    private static final String DEFAULT_NETWORK = "MAINNET";

    private final WalletAddressRepository walletAddressRepository;

    public WalletAddressService(WalletAddressRepository walletAddressRepository) {
        this.walletAddressRepository = walletAddressRepository;
    }

    @Transactional
    public WalletAddressEntity getOrCreateDepositAddress(UUID userId, String asset) {
        String normalizedAsset = normalize(asset);

        return walletAddressRepository.findByUserIdAndAssetAndNetwork(userId, normalizedAsset, DEFAULT_NETWORK)
                .orElseGet(() -> {
                    WalletAddressEntity address = new WalletAddressEntity();
                    address.setUserId(userId);
                    address.setAsset(normalizedAsset);
                    address.setNetwork(DEFAULT_NETWORK);
                    address.setAddress(generateMockAddress(normalizedAsset));
                    return walletAddressRepository.save(address);
                });
    }

    private static String normalize(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Asset is required");
        }
        return value.trim().toUpperCase();
    }

    private static String generateMockAddress(String asset) {
        return "mock_" + asset + "_" + UUID.randomUUID().toString().replace("-", "");
    }
}
