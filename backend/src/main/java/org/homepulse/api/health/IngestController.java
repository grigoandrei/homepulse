package org.homepulse.api.health;

import org.homepulse.api.security.DevicePrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class IngestController {

    private static final Logger log = LoggerFactory.getLogger(IngestController.class);

    @PostMapping("/api/ingest/health")
    ResponseEntity<Void> ingest(@AuthenticationPrincipal DevicePrincipal device, @RequestBody String body) {
        log.info("Ingest from device {} with {} bytes", device.deviceId(), body.length());
        return ResponseEntity.accepted().build();
    }
}
