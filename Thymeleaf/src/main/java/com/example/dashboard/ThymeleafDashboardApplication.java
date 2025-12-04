package com.example.dashboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.example.dashboard", "com.example.pak"})
public class ThymeleafDashboardApplication {
    public static void main(String[] args) {
        SpringApplication.run(ThymeleafDashboardApplication.class, args);
    }
}
