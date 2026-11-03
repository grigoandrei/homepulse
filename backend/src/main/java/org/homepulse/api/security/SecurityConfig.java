package org.homepulse.api.security;

import org.homepulse.api.people.DeviceRepository;
import org.homepulse.api.people.DeviceTokens;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, DeviceRepository devices, DeviceTokens tokens) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/ping", "/actuator/health", "/error").permitAll()
                .requestMatchers("/api/ingest/**").hasRole("DEVICE")
                .anyRequest().denyAll());
        http.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.csrf(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);
        http.formLogin(AbstractHttpConfigurer::disable);
        http.exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));
        http.addFilterBefore(new DeviceTokenFilter(devices, tokens), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}