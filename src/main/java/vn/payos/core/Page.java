package vn.payos.core;

import java.util.*;
import lombok.Getter;
import vn.payos.PayOS;

/** Page */
public class Page<Item> {
  private final PayOS client;

  @Getter private final List<Item> items;

  @Getter private final Pagination pagination;

  private final FinalRequestOptions<?> options;
  private final Class<? extends PageableResponse<Item>> responseClass;

  /**
   * Page
   *
   * @param <T> Item type
   * @param client client
   * @param response response
   * @param responseClass response body type
   * @param options options
   */
  public <T extends PageableResponse<Item>> Page(
      PayOS client,
      T response,
      Class<? extends PageableResponse<Item>> responseClass,
      FinalRequestOptions<?> options) {
    this.client = client;
    this.options = options;
    this.items = response.getItems();
    this.pagination = response.getPagination();
    this.responseClass = responseClass;
  }

  /**
   * Has next page
   *
   * @return result
   */
  public boolean hasNextPage() {
    return pagination != null && pagination.isHasMore();
  }

  /**
   * Has previous page
   *
   * @return result
   */
  public boolean hasPreviousPage() {
    return pagination != null && pagination.getOffset() > 0;
  }

  /**
   * Get next page
   *
   * @return next page
   */
  public Page<Item> nextPage() {
    if (!hasNextPage()) {
      throw new IllegalStateException("No more pages available");
    }
    return fetchPage(pagination.getOffset() + pagination.getCount());
  }

  /**
   * Get previous page
   *
   * @return previous page
   */
  public Page<Item> previousPage() {
    if (!hasPreviousPage()) {
      throw new IllegalStateException("No previous pages available");
    }
    return fetchPage(Math.max(0, pagination.getOffset() - pagination.getLimit()));
  }

  /**
   * Auto pager
   *
   * @return auto pager
   */
  public AutoPager<Item> autoPager() {
    return new AutoPager<>(this);
  }

  private Page<Item> fetchPage(int offset) {
    Map<String, Object> nextQueries =
        options.getQueries() != null
            ? new LinkedHashMap<>(options.getQueries())
            : new LinkedHashMap<>();
    nextQueries.put("offset", offset);
    nextQueries.put("limit", pagination.getLimit());

    Map<String, String> nextHeaders =
        options.getHeaders() != null ? new LinkedHashMap<>(options.getHeaders()) : null;

    FinalRequestOptions<Object> nexOptions =
        FinalRequestOptions.<Object>builder()
            .method(options.getMethod())
            .path(options.getPath())
            .queries(nextQueries)
            .headers(nextHeaders)
            .signatureOpts(options.getSignatureOpts())
            .timeout(options.getTimeout())
            .maxRetries(options.getMaxRetries())
            .build();
    PageableResponse<Item> resp =
        (PageableResponse<Item>) client.request(nexOptions, responseClass);
    return new Page<>(client, resp, responseClass, nexOptions);
  }
}
