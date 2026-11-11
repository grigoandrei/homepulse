package org.homepulse.api.health;

import org.homepulse.api.security.DevicePrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.core.JacksonException;

import java.util.Optional;

@RestController
class IngestController {

    private static final Logger log = LoggerFactory.getLogger(IngestController.class);
    private final IngestService ingestService;

    public IngestController(IngestService ingestService) {
        this.ingestService = ingestService;
    }

    @PostMapping("/api/ingest/health")
    ResponseEntity<Void> ingest(@AuthenticationPrincipal DevicePrincipal device, @RequestBody String body) {
        Optional<Long> id = ingestService.receive(device, body);
        if (id.isPresent()) {
            log.info("Saved payload {} from device {}", id.get(), device.deviceId());
        } else {
            log.info("Skipped resend from device {}", device.deviceId());
        }
        log.info("Ingest from device {} with {} bytes", device.deviceId(), body.length());
        return ResponseEntity.accepted().build();
    }

    @ExceptionHandler(JacksonException.class)
    ResponseEntity<Void> badJson() {
        return ResponseEntity.badRequest().build();
    }
}