package com.shovan.security.config;

import java.util.List;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.shovan.security.entity.EndPointConfig;
import com.shovan.security.repository.EndPointConfigRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class GatewayConfig {

    private final EndPointConfigRepository configRepository;
    private final JwtGatewayFilter jwtGatewayFilter;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        List<EndPointConfig> configs = configRepository.findAll();
        RouteLocatorBuilder.Builder routes = builder.routes();

        for (EndPointConfig config : configs) {
            routes.route(r -> r
                    .path(config.getPath())
                    .and().method(config.getMethod())
                    .filters(f -> {
                        if (config.isAuthRequired()) {
                            f.filter(jwtGatewayFilter);
                        }
                        return f;
                    })
                    .uri(config.getTargetService()));
        }

        return routes.build();
    }
}
