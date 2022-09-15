package dev.intermediatebox.upstream.repository.service;

import dev.intermediatebox.application.exception.GenericException;
import dev.intermediatebox.application.exception.GitHubUsernameNotFoundException;
import dev.intermediatebox.upstream.repository.entity.RepositoryEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Predicate;

@Service
@Slf4j
public class RepositoryService {
  @Autowired
  private WebClient webClient;

  public Mono<List<RepositoryEntity>> getAllReposByUsername(String username) {
    log.info("Retrieving all repositories for {} user...", username);
    var reposByUsername = webClient.get()
        .uri("/users/{username}/repos", username)
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError, response -> {
          return response.createException().flatMap(e -> {
            if (response.statusCode() == HttpStatus.NOT_FOUND) {
              log.warn("An exception occurred while retrieving all repositories for {} user", username, e);
              return Mono.error(new GitHubUsernameNotFoundException(String.format("Username %s not found.", username)));
            }
            return Mono.error(new GenericException(e.getMessage()));
          });
        })
        .bodyToMono(new ParameterizedTypeReference<List<RepositoryEntity>>() {})
        .onErrorMap(Predicate.not(GitHubUsernameNotFoundException.class::isInstance), e -> {
          log.warn("An exception occurred", e);
          return new GenericException(e.getMessage());
        });
    log.info("Successfully retrieved all repositories for {} user.", username);
    return reposByUsername;
  }
}
