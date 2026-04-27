package com.ran.hub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.ran")
public class HubApplication {
    public static void main(String[] args) {
        SpringApplication.run(HubApplication.class , args);
        System.out.println("""
            
            ========================================
              Console Hub Application Started!
            ========================================
              Port: 8080
              Health: http://localhost:8080/actuator/health
              Swagger: http://localhost:8080/doc.html
            ========================================
            
            """);
    }
}
