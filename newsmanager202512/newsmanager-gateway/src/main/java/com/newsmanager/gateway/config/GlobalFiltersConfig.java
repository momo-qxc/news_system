package com.newsmanager.gateway.config;


import com.newsmanager.gateway.filters.GlobalFilters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GlobalFiltersConfig {
    @Bean
    public GlobalFilters customFilter() { return new GlobalFilters(); }
}