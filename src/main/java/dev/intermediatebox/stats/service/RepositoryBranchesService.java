package dev.intermediatebox.stats.service;

import dev.intermediatebox.stats.entity.BranchEntity;
import dev.intermediatebox.stats.entity.RepositoryBranchesEntity;
import dev.intermediatebox.upstream.repository.entity.RepositoryEntity;
import dev.intermediatebox.upstream.repository.service.BranchService;
import dev.intermediatebox.upstream.repository.service.RepositoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RepositoryBranchesService {
  @Autowired
  RepositoryService repositoryService;

  @Autowired
  BranchService branchService;

  public List<RepositoryBranchesEntity> getAllRepositoriesAndBranches(String username) {
    List<RepositoryEntity> repositories = repositoryService.getAllReposByUsername(username);
    List<RepositoryBranchesEntity> repositoryBranches = new ArrayList<>();

    repositories.stream().forEach(repository -> {
      var branches = branchService.getAllBranchesByRepositoryName(username, repository.getName());
      repositoryBranches.add(RepositoryBranchesEntity.builder()
          .repositoryName(repository.getName())
          .repositoryOwner(repository.getOwner().getLogin())
          .branches(
              branches.stream().map(branch -> BranchEntity.builder()
                  .name(branch.getName())
                  .commitSha(branch.getCommit().getSha())
                  .build()
              ).collect(Collectors.toList())
          )
          .build()
      );
    });

    return repositoryBranches;
  }
}
