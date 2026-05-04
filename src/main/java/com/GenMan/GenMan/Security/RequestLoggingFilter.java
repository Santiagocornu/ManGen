package com.GenMan.GenMan.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final int MAX_BODY_LENGTH = 10_000;
    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);
    private static final Set<String> SENSITIVE_FIELDS = Set.of(
            "password",
            "newPassword",
            "passwordAdmin",
            "token",
            "authorization"
    );

    private final ObjectMapper objectMapper;

    public RequestLoggingFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request, MAX_BODY_LENGTH);

        try {
            filterChain.doFilter(wrappedRequest, response);
        } finally {
            logger.info("URL ingresada: {} {}", wrappedRequest.getMethod(), getFullUrl(wrappedRequest));
            String body = getRequestBody(wrappedRequest);
            if (!body.isBlank()) {
                logger.info("Datos ingresados: {}", sanitizeBody(body));
            }
        }
    }

    private String getFullUrl(HttpServletRequest request) {
        String queryString = request.getQueryString();
        if (queryString == null || queryString.isBlank()) {
            return request.getRequestURI();
        }
        return request.getRequestURI() + "?" + queryString;
    }

    private String getRequestBody(ContentCachingRequestWrapper request) {
        byte[] content = request.getContentAsByteArray();
        if (content.length == 0) {
            return "";
        }

        Charset charset = StandardCharsets.UTF_8;
        String encoding = request.getCharacterEncoding();
        if (encoding != null && !encoding.isBlank()) {
            charset = Charset.forName(encoding);
        }

        return new String(content, charset).trim();
    }

    private String sanitizeBody(String body) {
        try {
            JsonNode root = objectMapper.readTree(body);
            sanitizeJson(root);
            return objectMapper.writeValueAsString(root);
        } catch (Exception ex) {
            return body;
        }
    }

    private void sanitizeJson(JsonNode node) {
        if (node == null || node.isNull()) {
            return;
        }

        if (node.isObject()) {
            ObjectNode objectNode = (ObjectNode) node;
            for (String fieldName : new ArrayList<>(objectNode.propertyNames())) {
                JsonNode child = objectNode.get(fieldName);
                if (isSensitive(fieldName)) {
                    objectNode.put(fieldName, "***");
                } else {
                    sanitizeJson(child);
                }
            }
        } else if (node.isArray()) {
            node.forEach(this::sanitizeJson);
        }
    }

    private boolean isSensitive(String fieldName) {
        String normalized = fieldName.toLowerCase(Locale.ROOT);
        return SENSITIVE_FIELDS.stream()
                .map(field -> field.toLowerCase(Locale.ROOT))
                .anyMatch(normalized::contains);
    }
}
