package com.newsmanager.gateway.filters;

import com.newsmanager.gateway.config.GlobalFiltersConfig;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import org.springframework.stereotype.Component;

@Component
public class GlobalFilters implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        System.out.println("=========== 开始过滤 =============");
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}