package com.sree.controllers;

import com.netflix.discovery.EurekaClient;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class CompanyController {

    @Autowired
    @Lazy
    private EurekaClient eurekaClient;

    @Value("${spring.application.name}")
    private String appName;

    @Autowired
    RestTemplate restTemplate;

    @CircuitBreaker(name = "eureka-user-client", fallbackMethod = "fallback")
    @Retry(name = "eureka-user-client")
    @RateLimiter(name = "eureka-user-client")
    @Bulkhead(name = "eureka-user-client")
    @GetMapping("/callService")
    public String callService() {
        // Assume "eureka-user-client" is another service registered in Eureka
        String serviceUrl = "http://eureka-user-client/callUserService";
        return restTemplate.getForObject(serviceUrl, String.class);
    }

    @GetMapping("/callCompanyService")
    public String callCompanyService() {
        System.out.println("Called from User Service");
        return "Welcome to "+appName;
    }
}
