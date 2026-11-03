package org.homepulse.api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.homepulse.api.people.DeviceRepository;
import org.homepulse.api.people.DeviceTokens;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

public class DeviceTokenFilter extends OncePerRequestFilter {

    private final DeviceRepository devices;
    private final DeviceTokens tokens;

    public DeviceTokenFilter(DeviceRepository devices, DeviceTokens tokens) {
        this.devices = devices;
        this.tokens = tokens;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = header.substring("Bearer ".length());

        devices.findByTokenHash(tokens.hash(token)).ifPresent(device -> {
            device.markSeen(Instant.now());
            devices.save(device);

            var principal = new DevicePrincipal(device.getId(), device.getPerson().getId(), device.getPlatform());
            var auth = UsernamePasswordAuthenticationToken.authenticated(
                    principal, null, List.of(new SimpleGrantedAuthority("ROLE_DEVICE")));

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(auth);
            SecurityContextHolder.setContext(context);
        });
        filterChain.doFilter(request, response);
    }
}
