package dev.intermediatebox.upstream.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RepositoryEntity {
  private String name;

  private RepositoryOwnerEntity owner;

  private boolean fork;
}
