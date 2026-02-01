package com.github.renancvitor.inventory.infra.observability;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
@Profile("!test")
public class HealthController {

    @Value("${app.name}")
    private String application;

    @Value("${app.version}")
    private String version;

    @Value("${app.environment.default}")
    private String environment;

    @GetMapping
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();

        response.put("status", "UP");
        response.put("application", application);
        response.put("version", version);
        response.put("environment", environment);
        response.put("timestamp", OffsetDateTime.now().toString());

        return ResponseEntity.ok(response);
    }

}
