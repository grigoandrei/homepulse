package org.homepulse.api.security;

import org.homepulse.api.people.Platform;

public record DevicePrincipal(long deviceId, long personId, Platform platform) {}
