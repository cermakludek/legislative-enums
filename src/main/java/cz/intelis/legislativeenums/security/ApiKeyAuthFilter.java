package cz.intelis.legislativeenums.security;

import cz.intelis.legislativeenums.apikey.ApiKey;
import cz.intelis.legislativeenums.apikey.ApiKeyService;
import cz.intelis.legislativeenums.monetization.MonetizationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * Filter for API key authentication.
 * All API requests require a valid API key in the X-API-Key header.
 * Usage is recorded to the api_usage table for monitoring and rate limiting.
 *
 * @author Legislative Codelists Team
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private final ApiKeyService apiKeyService;
    private final MonetizationService monetizationService;
    private static final String API_KEY_HEADER = "X-API-Key";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                   HttpServletResponse response,
                                   FilterChain filterChain) throws ServletException, IOException {

        String requestPath = request.getRequestURI();

        if (!requestPath.startsWith("/api/v1/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String apiKeyValue = request.getHeader(API_KEY_HEADER);

        // API key is required for all API endpoints
        if (apiKeyValue == null || apiKeyValue.isEmpty()) {
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED,
                "API key is required. Please provide a valid API key in the X-API-Key header.");
            return;
        }

        try {
            ApiKey apiKey = apiKeyService.findValidByApiKey(apiKeyValue);

            if (!monetizationService.checkRateLimit(apiKey)) {
                sendErrorResponse(response, 429, "Rate limit exceeded. Please try again later.");
                return;
            }

            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                    apiKey.getUser().getUsername(),
                    null,
                    Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_API_USER")
                    )
                );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            long startTime = System.currentTimeMillis();
            filterChain.doFilter(request, response);
            long responseTime = System.currentTimeMillis() - startTime;

            // Detect response format from Accept header or response Content-Type
            String responseFormat = detectResponseFormat(request, response);

            // Record API usage for monitoring and billing
            monetizationService.recordUsage(
                apiKey,
                requestPath,
                request.getRemoteAddr(),
                request.getHeader("User-Agent"),
                response.getStatus(),
                responseTime,
                responseFormat
            );

        } catch (Exception e) {
            log.warn("API key authentication failed for key '{}': {}", apiKeyValue, e.getMessage());
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED,
                "Invalid API key: " + e.getMessage());
        }
    }

    /**
     * Detects the response format based on Accept header or response Content-Type.
     *
     * @param request the HTTP request
     * @param response the HTTP response
     * @return "JSON", "XML", or null if unknown
     */
    private String detectResponseFormat(HttpServletRequest request, HttpServletResponse response) {
        // First check the response Content-Type
        String contentType = response.getContentType();
        if (contentType != null) {
            if (contentType.contains("xml")) {
                return "XML";
            }
            if (contentType.contains("json")) {
                return "JSON";
            }
        }

        // Fallback to Accept header
        String acceptHeader = request.getHeader("Accept");
        if (acceptHeader != null) {
            if (acceptHeader.contains("xml")) {
                return "XML";
            }
            if (acceptHeader.contains("json")) {
                return "JSON";
            }
        }

        // Default to JSON
        return "JSON";
    }

    /**
     * Sends a JSON error response.
     *
     * @param response the HTTP response
     * @param status the HTTP status code
     * @param message the error message
     */
    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"error\":\"" + message + "\"}");
    }
}
