package com.visma.kalmar.api.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.Instant;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties({PublicEndpointsProperties.class, ResourceServer.class})
public class SecurityConfig {

    private final PublicEndpointsProperties publicEndpoints;
    private final ResourceServer resourceServer;

    public SecurityConfig(PublicEndpointsProperties publicEndpoints, ResourceServer resourceServer) {
        this.publicEndpoints = publicEndpoints;
        this.resourceServer = resourceServer;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        authz ->
                                authz
                                        .requestMatchers(HttpMethod.GET, publicEndpoints.get())
                                        .permitAll()
                                        .requestMatchers(HttpMethod.POST, publicEndpoints.post())
                                        .permitAll()
                                        .anyRequest()
                                        .authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    @Profile("stag | prod")
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder =
                NimbusJwtDecoder.withIssuerLocation(resourceServer.issuer()).validateType(false).build();
        jwtDecoder.setJwtValidator(
                JwtValidators.createDefaultWithValidators(
                        new JwtIssuerValidator(resourceServer.issuer()),
                        new JwtTypeValidator(resourceServer.vismaConnectJoseType())));
        return jwtDecoder;
    }

    /**
     * Tokens issues by AWS Cognito do not have the "typ" (type) header set and no audience claim, so
     * we need to ignore these validations in test, dev and int environments.
     */
    @Bean
    @Primary
    @Profile("test | dev | int")
    public JwtDecoder mockJwtDecoder() {
        // Create a simple mock JWT decoder that doesn't make any external calls
        return new JwtDecoder() {
            @Override
            public Jwt decode(String token) throws JwtException {
                // For testing, return a simple mock JWT
                return Jwt.withTokenValue(token)
                        .header("alg", "none")
                        .claim("sub", "test-user")
                        .claim("iss", "test-issuer")
                        .claim("aud", "test-audience")
                        .claim("exp", Instant.now().plusSeconds(3600))
                        .claim("iat", Instant.now())
                        .build();
            }
        };
    }
}
