package dev.intermediatebox.application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class GitHubUsernameNotFoundException extends RuntimeException {

  public GitHubUsernameNotFoundException(String message) {
    super(message);
  }
}
