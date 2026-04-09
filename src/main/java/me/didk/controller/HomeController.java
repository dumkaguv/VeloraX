package me.didk.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import me.didk.common.exception.ApiErrorResponse;
import me.didk.common.response.ApiEnvelope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    @Operation(summary = "Get API overview")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "API overview returned", useReturnTypeSchema = true),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public ResponseEntity<ApiEnvelope<Map<String, Object>>> home() {
        return ResponseEntity.ok(ApiEnvelope.success("Success", Map.of(
                "application", "VeloraX",
                "api", Map.of(
                        "users", "/api/v1/users",
                        "userById", "/api/v1/users/{id}"
                )
        )));
    }
}
