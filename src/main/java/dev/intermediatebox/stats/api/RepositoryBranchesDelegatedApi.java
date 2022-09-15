package dev.intermediatebox.stats.api;

import dev.intermediatebox.stats.entity.RepositoryBranchesEntity;
import dev.intermediatebox.stats.service.RepositoryBranchesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@ConditionalOnProperty(prefix = "openapi", name = "generated", havingValue = "true")
public class RepositoryBranchesDelegatedApi implements StatsApiDelegate {
  @Autowired
  private RepositoryBranchesService repositoryBranchesService;
  
  @Override
  public ResponseEntity<List<RepositoryBranchesEntity>> getRepositories(String username,
                                                                        String accept) {
    return ResponseEntity.ok().body(repositoryBranchesService.getAllRepositoriesAndBranches(username));
  }
}
