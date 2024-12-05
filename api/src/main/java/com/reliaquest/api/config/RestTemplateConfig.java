package com.reliaquest.api.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

@Configuration
public class RestTemplateConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder()
                .requestFactory(() -> {
                    SimpleClientHttpRequestFactory rf = new SimpleClientHttpRequestFactory();
                    rf.setConnectTimeout(5000);  // 5 seconds
                    rf.setReadTimeout(5000);     // 5 seconds
                    return rf;
                })
                .build();
    }
}