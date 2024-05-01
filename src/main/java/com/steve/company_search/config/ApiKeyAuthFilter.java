package com.steve.company_search.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    //would be retrieved normally via secrets manager etc, pass as env variable for example test
    @Value("${api.key:''}")
    private String apiKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Get the API key and secret from request headers
        String requestApiKey = request.getHeader("X-API-KEY");

        //set teh authentetion context tp be X-API-Key, sually would authnection tokens we be checking agints
        //sinc only a example, think we ok with this :-)
        validateApiKey(requestApiKey).ifPresent(SecurityContextHolder.getContext()::setAuthentication);

        // Continue processing the request
        filterChain.doFilter(request, response);

    }

    public Optional<Authentication> validateApiKey(String providedKey) {

        if (providedKey ==null || !providedKey.equals(apiKey))
            return Optional.empty();

        return Optional.of(new ApiKeyAuth(providedKey, AuthorityUtils.NO_AUTHORITIES));
    }
}