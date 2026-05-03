package com.ims.backend.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    // ✅ CREATE BEAN MANUALLY
    @Bean
    public RateLimiterFilter rateLimiterFilter() {
        return new RateLimiterFilter();
    }

    // ✅ REGISTER FILTER
    @Bean
    public FilterRegistrationBean<RateLimiterFilter> rateLimiterFilterRegistration() {
        FilterRegistrationBean<RateLimiterFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(rateLimiterFilter());
        reg.addUrlPatterns("/signals"); // IMPORTANT
        reg.setOrder(1);
        return reg;
    }
}