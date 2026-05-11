package com.GenMan.GenMan.Security;

import com.GenMan.GenMan.Entities.User;
import com.GenMan.GenMan.Entities.Sucursal;
import com.GenMan.GenMan.Repository.SucursalRepository;
import com.GenMan.GenMan.Repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final SucursalRepository sucursalRepository;

    public JwtFilter(JwtService jwtService, UserRepository userRepository, SucursalRepository sucursalRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.sucursalRepository = sucursalRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String requestUri = request.getRequestURI();

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())
                || "/auth/login".equals(requestUri)
                || "/auth/register-sucursal-admin".equals(requestUri)
                || "/auth/pagar-sucursal".equals(requestUri)
                || "/auth/desactivar-pago-sucursal".equals(requestUri)
                || "/auth/sucursales".equals(requestUri)
                || "/swagger-ui.html".equals(requestUri)
                || requestUri.startsWith("/swagger-ui/")
                || "/v3/api-docs".equals(requestUri)
                || requestUri.startsWith("/v3/api-docs/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            logger.warn("Solicitud rechazada por token faltante o invalido. ip={}, metodo={}, url={}",
                    getClientIp(request), request.getMethod(), getFullUrl(request));
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token faltante o invalido");
            return;
        }

        try {
            String token = header.substring(7);
            Claims claims = jwtService.extractClaims(token);
            Long userId = jwtService.extractUserId(token);
            Long sucursalId = claims.get("sucursal_id", Long.class);

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Usuario del token no encontrado"));

            Long userSucursalId = user.getSucursal() == null ? null : user.getSucursal().getId();
            if (userSucursalId == null || !userSucursalId.equals(sucursalId)) {
                throw new RuntimeException("La sucursal del token no coincide con la del usuario");
            }

            Sucursal sucursal = sucursalRepository.findById(sucursalId)
                    .orElseThrow(() -> new RuntimeException("Sucursal del token no encontrada"));

            if (!Boolean.TRUE.equals(sucursal.getEstaPago())) {
                logger.warn("Solicitud rechazada por falta de pago. ip={}, metodo={}, url={}, userId={}, sucursalId={}",
                        getClientIp(request), request.getMethod(), getFullUrl(request), userId, sucursalId);
                response.setStatus(HttpServletResponse.SC_PAYMENT_REQUIRED);
                response.setContentType("text/plain;charset=UTF-8");
                response.getWriter().write("falta de pago");
                return;
            }

            String role = user.getRoll() == null || user.getRoll().isBlank()
                    ? "USER"
                    : user.getRoll().toUpperCase();

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            user.getEmail(),
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + role))
                    );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            SucursalContext.set(sucursalId);
            AuthenticatedUserContext.set(new AuthenticatedUser(user.getId(), user.getEmail(), role, sucursalId));

            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            SecurityContextHolder.clearContext();
            logger.warn("Solicitud rechazada por token invalido o usuario no autorizado. ip={}, metodo={}, url={}, motivo={}",
                    getClientIp(request), request.getMethod(), getFullUrl(request), ex.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token invalido o usuario no autorizado");
        } finally {
            SucursalContext.clear();
            AuthenticatedUserContext.clear();
            SecurityContextHolder.clearContext();
        }
    }

    private String getFullUrl(HttpServletRequest request) {
        String queryString = request.getQueryString();
        if (queryString == null || queryString.isBlank()) {
            return request.getRequestURI();
        }
        return request.getRequestURI() + "?" + queryString;
    }

    private String getClientIp(HttpServletRequest request) {
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
