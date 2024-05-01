package com.steve.company_search.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.List;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableMethodSecurity
class SecurityConfiguration {

    @Autowired
    private ApiKeyAuthFilter apiKeyAuthFilter;

    @Autowired
    private UnauthorizedHandler unauthorizedHandler;
    private static final String[] AUTH_WHITELIST = {
        "/swagger-resources",
        "/swagger-resources/**",
        "/swagger-ui.html",
        "/swagger-ui/**",

    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.httpBasic(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> requests.requestMatchers(AUTH_WHITELIST)
                        .permitAll()
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/company/search/**")
                        .authenticated())
                .addFilterBefore(apiKeyAuthFilter, UsernamePasswordAuthenticationFilter.class)

                .sessionManagement(sessionManager -> sessionManager.sessionCreationPolicy(STATELESS))
                .exceptionHandling(configurer -> configurer.authenticationEntryPoint(unauthorizedHandler))

                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        /* Note: Allowing everything like this is not the correct way, so never do this in a practical environment. */

        var configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST"));
        configuration.setAllowedHeaders(List.of("*"));

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }


}
