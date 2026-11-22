package com.enterpriseops.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI workflowOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Enterprise Service & Workflow Automation REST API")
                        .description("Unified Platform merging ServiceOps, OpsPilot, and WorkForce. Automated SLA escalation and process routing.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Prasun Nandi")
                                .email("prasunnandi18@gmail.com")
                                .url("https://github.com/Prasunnandi/enterprise-workflow-automation"))
                        .license(new License().name("MIT").url("https://opensource.org/licenses/MIT")));
    }
}
