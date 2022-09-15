package dev.intermediatebox.stats.service;

import dev.intermediatebox.stats.entity.BranchEntity;
import dev.intermediatebox.stats.entity.RepositoryBranchesEntity;
import dev.intermediatebox.upstream.repository.service.BranchService;
import dev.intermediatebox.upstream.repository.service.RepositoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RepositoryBranchesService {
  @Autowired
  private RepositoryService repositoryService;

  @Autowired
  private BranchService branchService;

  public List<RepositoryBranchesEntity> getAllRepositoriesAndBranches(String username) {
    return repositoryService.getAllReposByUsername(username)
        .flatMapMany(Flux::fromIterable)
        .parallel()
        .runOn(Schedulers.newBoundedElastic(10, 10, "RepositoryBranches"))
        .flatMap((repository) -> Mono.just(repository).zipWith(branchService.getAllBranchesByRepositoryName(username, repository.getName())))
        .sequential()
        .map(t2 -> {
          var repository = t2.getT1();
          var branches = t2.getT2();

          return RepositoryBranchesEntity.builder()
              .repositoryName(repository.getName())
              .repositoryOwner(repository.getOwner().getLogin())
              .branches(
                  branches.stream().map(branch -> BranchEntity.builder()
                      .name(branch.getName())
                      .commitSha(branch.getCommit().getSha())
                      .build()
                  ).collect(Collectors.toList())
              )
              .build();
        })
        .collectList()
        .block(Duration.of(5, ChronoUnit.SECONDS));
  }
}
