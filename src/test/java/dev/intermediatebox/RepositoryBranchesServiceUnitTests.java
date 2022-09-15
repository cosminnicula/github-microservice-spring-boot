package dev.intermediatebox;

import dev.intermediatebox.application.exception.GitHubUsernameNotFoundException;
import dev.intermediatebox.stats.service.RepositoryBranchesService;
import dev.intermediatebox.upstream.repository.entity.BranchCommitEntity;
import dev.intermediatebox.upstream.repository.entity.BranchEntity;
import dev.intermediatebox.upstream.repository.entity.RepositoryEntity;
import dev.intermediatebox.upstream.repository.entity.RepositoryOwnerEntity;
import dev.intermediatebox.upstream.repository.service.BranchService;
import dev.intermediatebox.upstream.repository.service.RepositoryService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@SpringBootTest
class RepositoryBranchesServiceUnitTests {

  @MockBean
  RepositoryService repositoryService;

  @MockBean
  BranchService branchService;

  @Autowired
  RepositoryBranchesService repositoryBranchesService;

  @Test
  void given_userWithRepositoriesAndBranches_when_getAll_then_returnCorrectNumberOfItems() {
    String username = "u1";

    List<RepositoryEntity> repositories = List.of(
        new RepositoryEntity("r1", new RepositoryOwnerEntity(username)),
        new RepositoryEntity("r2", new RepositoryOwnerEntity(username))
    );

    List<BranchEntity> repository1Branches = List.of(
        new BranchEntity("main", new BranchCommitEntity("15610ccc7244c6a289944d1f4e39635371248f00"))
    );
    List<BranchEntity> repository2Branches = List.of(
        new BranchEntity("main", new BranchCommitEntity("b2656fd21bbdfa67872d6b521d7dba083e0474a3"))
    );

    when(repositoryService.getAllReposByUsername(username)).thenReturn(Mono.just(repositories));
    when(branchService.getAllBranchesByRepositoryName(username, repositories.get(0).getName()))
        .thenReturn(Mono.just(repository1Branches));
    when(branchService.getAllBranchesByRepositoryName(username, repositories.get(1).getName()))
        .thenReturn(Mono.just(repository2Branches));

    assertEquals(repositories.size(), repositoryBranchesService.getAllRepositoriesAndBranches(username).size());
    Assertions.assertEquals(repository1Branches.size(),
        repositoryBranchesService.getAllRepositoriesAndBranches(username).get(0).getBranches().size());
    Assertions.assertEquals(repository2Branches.size(),
        repositoryBranchesService.getAllRepositoriesAndBranches(username).get(1).getBranches().size());
  }

  @Test
  void given_userWithNoRepositories_when_getAll_then_returnEmptyResponse() {
    String username = "u1";

    List<RepositoryEntity> repositories = List.of();

    when(repositoryService.getAllReposByUsername(username)).thenReturn(Mono.just(repositories));

    assertEquals(repositories.size(), repositoryBranchesService.getAllRepositoriesAndBranches(username).size());
  }

  @Test
  void given_repositoryWithNoBranches_when_getAll_then_returnEmptyListOfBranches() {
    String username = "u1";

    List<RepositoryEntity> repositories = List.of(
        new RepositoryEntity("r1", new RepositoryOwnerEntity(username))
    );

    List<BranchEntity> repository1Branches = List.of();

    when(repositoryService.getAllReposByUsername(username)).thenReturn(Mono.just(repositories));
    when(branchService.getAllBranchesByRepositoryName(username, repositories.get(0).getName()))
        .thenReturn(Mono.just(repository1Branches));

    Assertions.assertEquals(repository1Branches.size(),
        repositoryBranchesService.getAllRepositoriesAndBranches(username).get(0).getBranches().size());
  }

  @Test
  void given_nonExistentUser_when_getAll_then_throwException() {
    String nonExistentUsername = "dummy";
    doThrow(new GitHubUsernameNotFoundException(nonExistentUsername))
        .when(repositoryService).getAllReposByUsername(nonExistentUsername);

    assertThrows(GitHubUsernameNotFoundException.class, () -> {
      repositoryBranchesService.getAllRepositoriesAndBranches(nonExistentUsername);
    });
  }
}
