package org.homepulse.api.system;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
public class PingController {

    @GetMapping("/api/ping")
    public PingResponse ping() {
        return new PingResponse("ok", Instant.now());
    }
}
