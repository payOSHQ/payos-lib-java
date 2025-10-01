package vn.payos.core;

import java.util.List;

/** PageableResponse */
public interface PageableResponse<T> {
  /**
   * Get items
   *
   * @return items
   */
  List<T> getItems();

  /**
   * Get pagination information
   *
   * @return pagination information
   */
  Pagination getPagination();
}
