package com.GenMan.GenMan.Security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    private final JwtFilter jwtFilter;
    private final AntiSpamFilter antiSpamFilter;

    public SecurityConfig(JwtFilter jwtFilter, AntiSpamFilter antiSpamFilter) {
        this.jwtFilter = jwtFilter;
        this.antiSpamFilter = antiSpamFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login").permitAll()
                        .requestMatchers("/auth/register-sucursal-admin").permitAll()
                        .requestMatchers("/auth/pagar-sucursal").permitAll()
                        .requestMatchers("/auth/desactivar-pago-sucursal").permitAll()
                        .requestMatchers("/auth/sucursales").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/v3/api-docs").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/apiManGen/Sucursal/**").hasRole("ADMIN")
                        .requestMatchers("/apiManGen/User/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) -> {
                            logger.warn("Solicitud rechazada: autenticacion requerida. ip={}, metodo={}, url={}, motivo={}",
                                    getClientIp(request), request.getMethod(), getFullUrl(request), authException.getMessage());
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "No autenticado");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            logger.warn("Solicitud rechazada: permisos insuficientes. ip={}, metodo={}, url={}, motivo={}",
                                    getClientIp(request), request.getMethod(), getFullUrl(request), accessDeniedException.getMessage());
                            response.sendError(HttpServletResponse.SC_FORBIDDEN, "No autorizado");
                        })
                )
                .addFilterBefore(antiSpamFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private static String getFullUrl(HttpServletRequest request) {
        String queryString = request.getQueryString();
        if (queryString == null || queryString.isBlank()) {
            return request.getRequestURI();
        }
        return request.getRequestURI() + "?" + queryString;
    }

    private static String getClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp;
        }
        return request.getRemoteAddr();
    }
}
