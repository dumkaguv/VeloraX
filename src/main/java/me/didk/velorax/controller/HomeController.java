package me.didk.controller;

import me.didk.common.response.ApiEnvelope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public ApiEnvelope<Map<String, Object>> home() {
        return ApiEnvelope.success("Success", Map.of(
                "application", "VeloraX",
                "api", Map.of(
                        "users", "/api/v1/users",
                        "userById", "/api/v1/users/{id}"
                 )
        ));
    }
}
