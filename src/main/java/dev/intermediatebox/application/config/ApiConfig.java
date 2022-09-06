package dev.intermediatebox.application.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ApiConfig {
  @Value("${github.baseUrl}")
  private String githubBaseUrl;

  @Value("${github.personalAccessToken}")
  private String githubPersonalAccessToken;

  @Bean
  public WebClient webClient() {
    var webClient = WebClient.builder()
        .baseUrl(githubBaseUrl);

    if (!githubPersonalAccessToken.isEmpty()) {
      webClient.defaultHeader("Authorization", String.format("token %s", githubPersonalAccessToken));
    }

    return webClient.build();
  }
}
