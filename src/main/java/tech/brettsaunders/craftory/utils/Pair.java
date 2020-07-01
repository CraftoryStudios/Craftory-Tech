package tech.brettsaunders.craftory.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class Pair<X, Y> {
  @Getter
  @Setter
  private X x;
  @Getter
  @Setter
  private Y y;
}
