package com.GenMan.GenMan.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AntiSpamFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(AntiSpamFilter.class);

    private final ConcurrentHashMap<String, Long> recentRequests = new ConcurrentHashMap<>();
    private final boolean enabled;
    private final long windowMillis;
    private final int maxEntries;

    public AntiSpamFilter(
            @Value("${app.anti-spam.enabled:true}") boolean enabled,
            @Value("${app.anti-spam.window-ms:200}") long windowMillis,
            @Value("${app.anti-spam.max-entries:10000}") int maxEntries) {
        this.enabled = enabled;
        this.windowMillis = windowMillis;
        this.maxEntries = maxEntries;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        if (!enabled || windowMillis <= 0 || shouldSkip(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        HttpServletRequest requestToUse = request;
        if (hasBody(request)) {
            requestToUse = new CachedBodyHttpServletRequest(request);
        }

        long now = System.currentTimeMillis();
        cleanupIfNeeded(now);

        String requestKey = buildRequestKey(requestToUse);
        Long previousExpiration = recentRequests.putIfAbsent(requestKey, now + windowMillis);

        if (previousExpiration != null && previousExpiration > now) {
            logger.warn("Solicitud duplicada rechazada. ip={}, metodo={}, url={}",
                    getClientIp(request), request.getMethod(), getFullUrl(request));
            response.setStatus(429);
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().write("Solicitud duplicada. Intente nuevamente en unos instantes.");
            return;
        }

        if (previousExpiration != null) {
            recentRequests.put(requestKey, now + windowMillis);
        }

        filterChain.doFilter(requestToUse, response);
    }

    private boolean shouldSkip(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        return "OPTIONS".equalsIgnoreCase(request.getMethod())
                || "/swagger-ui.html".equals(requestUri)
                || requestUri.startsWith("/swagger-ui/")
                || "/v3/api-docs".equals(requestUri)
                || requestUri.startsWith("/v3/api-docs/");
    }

    private boolean hasBody(HttpServletRequest request) {
        String method = request.getMethod().toUpperCase(Locale.ROOT);
        return method.equals("POST") || method.equals("PUT") || method.equals("PATCH");
    }

    private String buildRequestKey(HttpServletRequest request) throws IOException {
        return getClientIp(request)
                + "|" + request.getMethod()
                + "|" + getFullUrl(request)
                + "|" + nullToEmpty(request.getHeader("Authorization"))
                + "|" + hashBody(request);
    }

    private String hashBody(HttpServletRequest request) throws IOException {
        if (request instanceof CachedBodyHttpServletRequest cachedRequest) {
            return sha256(cachedRequest.getCachedBody());
        }
        return "";
    }

    private String sha256(byte[] bytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(bytes));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 no disponible", ex);
        }
    }

    private void cleanupIfNeeded(long now) {
        if (recentRequests.size() < maxEntries) {
            return;
        }
        recentRequests.entrySet().removeIf(entry -> entry.getValue() <= now);
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

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private static final class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {

        private final byte[] cachedBody;

        private CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException {
            super(request);
            this.cachedBody = request.getInputStream().readAllBytes();
        }

        private byte[] getCachedBody() {
            return cachedBody;
        }

        @Override
        public ServletInputStream getInputStream() {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(cachedBody);
            return new ServletInputStream() {
                @Override
                public boolean isFinished() {
                    return inputStream.available() == 0;
                }

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setReadListener(ReadListener readListener) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public int read() {
                    return inputStream.read();
                }
            };
        }

        @Override
        public BufferedReader getReader() {
            Charset charset = StandardCharsets.UTF_8;
            String encoding = getCharacterEncoding();
            if (encoding != null && !encoding.isBlank()) {
                charset = Charset.forName(encoding);
            }
            return new BufferedReader(new InputStreamReader(getInputStream(), charset));
        }
    }
}
