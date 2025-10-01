package vn.payos.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Pagination */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pagination {
  private int limit;
  private int offset;
  private int total;
  private int count;
  private boolean hasMore;
}
