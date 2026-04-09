package me.didk.wallet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import me.didk.common.exception.ApiErrorResponse;
import me.didk.common.response.ApiEnvelope;
import me.didk.wallet.dto.DepositAddressResponse;
import me.didk.wallet.dto.WalletBalanceResponse;
import me.didk.wallet.service.WalletAddressService;
import me.didk.wallet.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Validated
@RestController
@Tag(name = "Wallet")
@RequestMapping("/api/v1/wallet")
public class WalletController {
    private final WalletService walletService;
    private final WalletAddressService walletAddressService;

    public WalletController(
            WalletService walletService,
            WalletAddressService walletAddressService
    ) {
        this.walletService = walletService;
        this.walletAddressService = walletAddressService;
    }

    @GetMapping("/balances")
    @Operation(summary = "Get wallet balances")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Wallet balances retrieved", useReturnTypeSchema = true),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public ResponseEntity<ApiEnvelope<List<WalletBalanceResponse>>> balances(
            @RequestHeader("X-User-Id") UUID userId
    ) {
        List<WalletBalanceResponse> balances = walletService.getBalances(userId)
                .stream()
                .map(WalletBalanceResponse::from)
                .toList();
        return ResponseEntity.ok(ApiEnvelope.success("Success", balances));
    }

    @GetMapping("/addresses")
    @Operation(summary = "Get or create deposit address")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Deposit address returned", useReturnTypeSchema = true),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public ResponseEntity<ApiEnvelope<DepositAddressResponse>> getOrCreateAddress(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestParam @NotBlank String asset
    ) {
        return ResponseEntity.ok(
                ApiEnvelope.success(
                        "Success",
                        DepositAddressResponse.from(walletAddressService.getOrCreateDepositAddress(userId, asset))
                )
        );
    }
}
