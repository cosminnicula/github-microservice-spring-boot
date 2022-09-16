package dev.intermediatebox;

import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
class RepositoryBranchesServiceIntegrationTests {
  private final String repositoryBranchesUrl = "/api/v1/stats/repository-branches";

  private final String username = "cosminnicula";

  private final String dummyUsername = "abc1816453621721xyz";

  private final Integer repositoriesNumber = 16;

  @Autowired
  private MockMvc mockMvc;

  @Test
  void given_userWithRepositoriesAndBranches_when_getAllApi_then_returnCorrectNumberOfItems() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get(repositoryBranchesUrl)
            .param("username", username))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.*", hasSize(repositoriesNumber)))
        .andExpect(jsonPath("$[?(@.repositoryName === 'algofun')].branches", hasSize(1)));
  }

  @Test
  void given_userWithRepositoriesAndBranches_when_getAllApi_then_respondsWithinTimeRange() throws Exception {
    SimpleMeterRegistry registry = new SimpleMeterRegistry();
    Timer timer = registry.timer("getAll");
    timer.record(() -> {
      try {
        given_userWithRepositoriesAndBranches_when_getAllApi_then_returnCorrectNumberOfItems();
      } catch (Exception e) {
      }
    });
    assertThat(timer.totalTime(TimeUnit.SECONDS)).isBetween(0.0, 10.0);
  }

  @Test
  void given_nonExistentUser_when_getAllApi_then_return404() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get(repositoryBranchesUrl)
            .param("username", dummyUsername))
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.status", is(HttpStatus.NOT_FOUND.value())))
        .andExpect(jsonPath("$.message", is(String.format("Username %s not found.", dummyUsername))));
  }

  @Test
  void given_notSupportedMediaTypeHeader_when_getAllApi_then_return406() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get(repositoryBranchesUrl)
            .header("Accept", MediaType.APPLICATION_XML)
            .param("username", username))
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.status", is(HttpStatus.NOT_ACCEPTABLE.value())))
        .andExpect(jsonPath("$.message", is(String.format
            ("Could not find acceptable representation. Supported media type(s): %s", MediaType.APPLICATION_JSON))
        ));
  }
}
