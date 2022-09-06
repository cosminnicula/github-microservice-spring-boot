package dev.intermediatebox.application.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiExceptionEntity {
  private int status;

  private String message;
}