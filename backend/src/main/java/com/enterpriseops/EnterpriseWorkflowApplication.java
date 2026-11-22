package com.enterpriseops;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EnterpriseWorkflowApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnterpriseWorkflowApplication.class, args);
    }
}
