package dev.intermediatebox.stats.api;

import dev.intermediatebox.stats.entity.RepositoryBranchesEntity;
import dev.intermediatebox.stats.service.RepositoryBranchesService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/stats/repository-branches")
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "openapi", name = "generated", havingValue = "false")
public class RepositoryBranchesApi {
  @Autowired
  RepositoryBranchesService repositoryBranchesService;

  @GetMapping(value = "", headers = {"Accept=application/json"})
  public ResponseEntity<List<RepositoryBranchesEntity>> getRepositories(@RequestParam String username) {
    return ResponseEntity.ok().body(repositoryBranchesService.getAllRepositoriesAndBranches(username));
  }
}
