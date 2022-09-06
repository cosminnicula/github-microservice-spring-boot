package dev.intermediatebox.upstream.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BranchEntity {
  private String name;

  private BranchCommitEntity commit;
}