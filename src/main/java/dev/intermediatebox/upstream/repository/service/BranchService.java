package dev.intermediatebox.upstream.repository.service;

import dev.intermediatebox.application.exception.GenericException;
import dev.intermediatebox.upstream.repository.entity.BranchEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
public class BranchService {
  @Autowired
  private WebClient webClient;

  public Mono<List<BranchEntity>> getAllBranchesByRepositoryName(String username, String repositoryName) {
    log.info("Retrieving all branches for {} repository...", repositoryName);
    var branches = webClient.get()
        .uri("/repos/{username}/{repositoryName}/branches", username, repositoryName)
        .retrieve()
        .bodyToMono(new ParameterizedTypeReference<List<BranchEntity>>() {
        })
        .onErrorMap(e -> {
          log.warn("An exception occurred while retrieving all branches for {} repository and {} user", repositoryName, username, e);
          return new GenericException(e.getMessage());
        });
    log.info("Successfully retrieved all branches for {} repository.", repositoryName);
    return branches;
  }
}