package org.homepulse.api.system;

import java.time.Instant;

record PingResponse(String status, Instant time) {}
