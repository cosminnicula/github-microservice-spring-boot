package dev.intermediatebox.application.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
  @Bean
  public GroupedOpenApi apiGroup() {
    return GroupedOpenApi
        .builder()
        .group("Api")
        .pathsToMatch("/api/**")
        .build();
  }

  @Bean
  public OpenAPI apiInfo() {
    return new OpenAPI().info(
        new Info()
            .title("GitHub Microservice")
            .description("This is a sample Java + Spring Boot microservice that interacts with the GitHub API and makes use of the Spring WebFlux reactive-stack web framework in order to retrieve and process Repositories + Branches information.")
            .version("1.0.0")
    );
  }
}
