package com.sree.controllers;

import com.netflix.discovery.EurekaClient;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    @Lazy
    private EurekaClient eurekaClient;

    @Value("${spring.application.name}")
    private String appName;

    @Autowired
    RestTemplate restTemplate;

    @GetMapping("/callService")
    @CircuitBreaker(name = "eureka-company-client", fallbackMethod = "fallback")
    @Retry(name = "eureka-company-client")
    @RateLimiter(name = "eureka-company-client")
    @Bulkhead(name = "eureka-company-client")
    public String callService() {
        // Assume "eureka-company-client" is another service registered in Eureka
        String serviceUrl = "http://eureka-company-client/callCompanyService";
        return restTemplate.getForObject(serviceUrl, String.class);
    }

    @GetMapping("/callUserService")
    public String callCompanyService() {
        log.info("Called from Company Service");
        return "Welcome to "+appName;
    }

    public String fallback(Throwable throwable) {
        log.info("Company Service Unavailable : "+throwable.getLocalizedMessage());
        return "Company Service Unavailable";
    }

}
