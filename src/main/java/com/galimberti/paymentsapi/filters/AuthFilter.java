package com.galimberti.paymentsapi.filters;

import java.io.IOException;

import javax.crypto.SecretKey;

import org.springframework.http.HttpStatus;
import org.springframework.web.filter.GenericFilterBean;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthFilter extends GenericFilterBean {

    private static final String BEARER_PREFIX = "Bearer ";

    private final SecretKey key;

    public AuthFilter(SecretKey key) {
        this.key = key;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            unauthorized(httpResponse, "Authorization token must be Bearer [token]");
            return;
        }

        String token = authHeader.substring(BEARER_PREFIX.length());
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            httpRequest.setAttribute("userId", Integer.parseInt(claims.get("userId").toString()));
        } catch (Exception e) {
            unauthorized(httpResponse, "invalid/expired token");
            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    private void unauthorized(HttpServletResponse response, String message) throws IOException {
        response.setHeader("WWW-Authenticate", "Bearer");
        response.sendError(HttpStatus.UNAUTHORIZED.value(), message);
    }
}
