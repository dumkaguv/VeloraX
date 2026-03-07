package me.didk.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> home() {
        return ResponseEntity.ok(Map.of(
                "application", "VeloraX",
                "api", Map.of(
                        "users", "/api/v1/users",
                        "userById", "/api/v1/users/{id}"
                 )
        ));
    }
}
