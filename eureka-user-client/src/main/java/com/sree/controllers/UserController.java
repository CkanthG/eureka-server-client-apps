package com.sree.controllers;

import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class UserController {

    @Autowired
    @Lazy
    private EurekaClient eurekaClient;

    @Value("${spring.application.name}")
    private String appName;

    @Autowired
    RestTemplate restTemplate;

    @GetMapping("/callService")
    public String callService() {
        // Assume "eureka-company-client" is another service registered in Eureka
        String serviceUrl = "http://eureka-company-client/callCompanyService";
        return restTemplate.getForObject(serviceUrl, String.class);
    }

    @GetMapping("/callUserService")
    public String callCompanyService() {
        System.out.println("Called from Company Service");
        return "Welcome to "+appName;
    }

}
