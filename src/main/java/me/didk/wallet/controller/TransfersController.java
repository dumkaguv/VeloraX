package me.didk.wallet.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import me.didk.common.exception.ApiErrorResponse;
import me.didk.common.openapi.OneBasedPageableAsQueryParam;
import me.didk.common.response.ApiEnvelope;
import me.didk.common.response.PaginatedApiEnvelope;
import me.didk.wallet.dto.CreateDepositRequest;
import me.didk.wallet.dto.CreateWithdrawalRequest;
import me.didk.wallet.dto.DepositResponse;
import me.didk.wallet.dto.UpdateTransferStatusRequest;
import me.didk.wallet.dto.WithdrawalResponse;
import me.didk.wallet.service.DepositService;
import me.didk.wallet.service.WithdrawalService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Validated
@RestController
@Tag(name = "Transfers")
@RequestMapping("/api/v1/wallet")
public class TransfersController {
    private final DepositService depositService;
    private final WithdrawalService withdrawalService;

    public TransfersController(
            DepositService depositService,
            WithdrawalService withdrawalService
    ) {
        this.depositService = depositService;
        this.withdrawalService = withdrawalService;
    }

    @GetMapping("/deposits")
    @OneBasedPageableAsQueryParam
    @Operation(summary = "List deposits")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Deposits retrieved", useReturnTypeSchema = true),
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
    public ResponseEntity<PaginatedApiEnvelope<List<DepositResponse>>> deposits(
            @RequestHeader("X-User-Id") UUID userId,
            @Parameter(hidden = true)
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<DepositResponse> page = depositService.list(userId, pageable).map(DepositResponse::from);
        return ResponseEntity.ok(PaginatedApiEnvelope.success("Success", page));
    }

    @PostMapping("/deposits")
    @Operation(summary = "Create deposit")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Deposit created", useReturnTypeSchema = true),
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
    public ResponseEntity<ApiEnvelope<DepositResponse>> createDeposit(
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody CreateDepositRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiEnvelope.success("Success", DepositResponse.from(depositService.create(userId, request))));
    }

    @GetMapping("/deposits/{id}")
    @Operation(summary = "Get deposit by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Deposit retrieved", useReturnTypeSchema = true),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Deposit not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public ResponseEntity<ApiEnvelope<DepositResponse>> depositById(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable("id") UUID id
    ) {
        return ResponseEntity.ok(ApiEnvelope.success("Success", DepositResponse.from(depositService.getById(userId, id))));
    }

    @PatchMapping("/deposits/{id}/status")
    @Operation(summary = "Update deposit status")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Deposit status updated", useReturnTypeSchema = true),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Deposit not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Deposit status conflict",
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
    public ResponseEntity<ApiEnvelope<DepositResponse>> updateDepositStatus(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable("id") UUID id,
            @Valid @RequestBody UpdateTransferStatusRequest request
    ) {
        return ResponseEntity.ok(
                ApiEnvelope.success(
                        "Success",
                        DepositResponse.from(depositService.updateStatus(userId, id, request.status(), request.txHash()))
                )
        );
    }

    @PostMapping("/withdrawals")
    @Operation(summary = "Create withdrawal")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Withdrawal created", useReturnTypeSchema = true),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Withdrawal conflict",
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
    public ResponseEntity<ApiEnvelope<WithdrawalResponse>> createWithdrawal(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody CreateWithdrawalRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        ApiEnvelope.success(
                                "Success",
                                WithdrawalResponse.from(withdrawalService.create(userId, idempotencyKey, request))
                        )
                );
    }

    @GetMapping("/withdrawals")
    @OneBasedPageableAsQueryParam
    @Operation(summary = "List withdrawals")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Withdrawals retrieved", useReturnTypeSchema = true),
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
    public ResponseEntity<PaginatedApiEnvelope<List<WithdrawalResponse>>> withdrawals(
            @RequestHeader("X-User-Id") UUID userId,
            @Parameter(hidden = true)
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<WithdrawalResponse> page = withdrawalService.list(userId, pageable).map(WithdrawalResponse::from);
        return ResponseEntity.ok(PaginatedApiEnvelope.success("Success", page));
    }

    @GetMapping("/withdrawals/{id}")
    @Operation(summary = "Get withdrawal by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Withdrawal retrieved", useReturnTypeSchema = true),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Withdrawal not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public ResponseEntity<ApiEnvelope<WithdrawalResponse>> withdrawalById(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable("id") UUID id
    ) {
        return ResponseEntity.ok(
                ApiEnvelope.success("Success", WithdrawalResponse.from(withdrawalService.getById(userId, id)))
        );
    }

    @PostMapping("/withdrawals/{id}/cancel")
    @Operation(summary = "Cancel withdrawal")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Withdrawal canceled", useReturnTypeSchema = true),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Withdrawal not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Withdrawal cannot be canceled",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public ResponseEntity<ApiEnvelope<WithdrawalResponse>> cancelWithdrawal(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable("id") UUID id
    ) {
        return ResponseEntity.ok(ApiEnvelope.success("Success", WithdrawalResponse.from(withdrawalService.cancel(userId, id))));
    }

    @PatchMapping("/withdrawals/{id}/status")
    @Operation(summary = "Update withdrawal status")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Withdrawal status updated", useReturnTypeSchema = true),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Withdrawal not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Withdrawal status conflict",
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
    public ResponseEntity<ApiEnvelope<WithdrawalResponse>> updateWithdrawalStatus(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable("id") UUID id,
            @Valid @RequestBody UpdateTransferStatusRequest request
    ) {
        return ResponseEntity.ok(
                ApiEnvelope.success(
                        "Success",
                        WithdrawalResponse.from(withdrawalService.updateStatus(userId, id, request.status(), request.errorMessage()))
                )
        );
    }
}
