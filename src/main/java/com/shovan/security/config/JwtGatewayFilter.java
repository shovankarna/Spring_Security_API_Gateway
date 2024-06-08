package com.shovan.security.config;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.annotation.Order;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.shovan.security.service.JwtService;
import com.shovan.security.service.TokenStoreService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@Order(0)
@RequiredArgsConstructor
public class JwtGatewayFilter implements GatewayFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenStoreService tokenStoreService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        String jwt;
        String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        jwt = authHeader.substring(7);
        userEmail = jwtService.extractUsername(jwt);

        if (userEmail != null && ReactiveSecurityContextHolder.getContext().block().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            if (jwtService.isTokenValid(jwt, userDetails) && tokenStoreService.isTokenPresent(userEmail)) {
                if (tokenStoreService.isTokenValid(userEmail, jwt)) {
                    List<String> roles = jwtService.extractRoles(jwt);
                    Collection<SimpleGrantedAuthority> authorities = roles.stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, authorities);

                    SecurityContext securityContext = new SecurityContextImpl(authToken);

                    return chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));
                } else {
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    exchange.getResponse().getHeaders().set("Content-Type", "application/json");
                    exchange.getResponse().getHeaders().set("error", "Token does not match");
                    return exchange.getResponse().setComplete();
                }
            } else {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                exchange.getResponse().getHeaders().set("Content-Type", "application/json");
                exchange.getResponse().getHeaders().set("error", "Invalid token");
                return exchange.getResponse().setComplete();
            }
        }

        return chain.filter(exchange);
    }
}