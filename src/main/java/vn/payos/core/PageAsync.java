package vn.payos.core;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import lombok.Getter;
import vn.payos.PayOSAsync;

/** PageAsync */
public class PageAsync<Item> {
  private final PayOSAsync client;

  @Getter private final List<Item> items;

  @Getter private final Pagination pagination;

  private final FinalRequestOptions<?> options;
  private final Class<? extends PageableResponse<Item>> responseClass;

  /**
   * PageAsync
   *
   * @param <T> Item type
   * @param client client
   * @param responseFuture response
   * @param responseClass response body type
   * @param options options
   */
  public <T extends PageableResponse<Item>> PageAsync(
      PayOSAsync client,
      CompletableFuture<T> responseFuture,
      Class<? extends PageableResponse<Item>> responseClass,
      FinalRequestOptions<?> options) {
    this.client = client;
    this.options = options;
    this.responseClass = responseClass;

    List<Item> initialData;
    Pagination initialPagination;
    try {
      T response = responseFuture.join();
      initialData = response.getItems();
      initialPagination = response.getPagination();
    } catch (Exception e) {
      initialData = Collections.emptyList();
      initialPagination = null;
    }
    this.items = initialData;
    this.pagination = initialPagination;
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
  public CompletableFuture<PageAsync<Item>> nextPage() {
    if (!hasNextPage()) {
      CompletableFuture<PageAsync<Item>> future = new CompletableFuture<>();
      future.completeExceptionally(new IllegalStateException("No more pages available"));
      return future;
    }
    return fetchPage(pagination.getOffset() + pagination.getCount());
  }

  /**
   * Get previous page
   *
   * @return previous page
   */
  public CompletableFuture<PageAsync<Item>> previousPage() {
    if (!hasPreviousPage()) {
      CompletableFuture<PageAsync<Item>> future = new CompletableFuture<>();
      future.completeExceptionally(new IllegalStateException("No previous pages available"));
      return future;
    }
    return fetchPage(Math.max(0, pagination.getOffset() - pagination.getLimit()));
  }

  /**
   * Auto pager
   *
   * @return auto pager
   */
  public AsyncAutoPager<Item> autoPager() {
    return new AsyncAutoPager<>(this);
  }

  private CompletableFuture<PageAsync<Item>> fetchPage(int offset) {
    Map<String, Object> nextQueries =
        options.getQueries() != null
            ? new LinkedHashMap<>(options.getQueries())
            : new LinkedHashMap<>();
    nextQueries.put("offset", offset);
    nextQueries.put("limit", pagination.getLimit());

    Map<String, String> nextHeaders =
        options.getHeaders() != null ? new LinkedHashMap<>(options.getHeaders()) : null;

    FinalRequestOptions<Object> nextOptions =
        FinalRequestOptions.<Object>builder()
            .method(options.getMethod())
            .path(options.getPath())
            .queries(nextQueries)
            .headers(nextHeaders)
            .signatureOpts(options.getSignatureOpts())
            .timeout(options.getTimeout())
            .maxRetries(options.getMaxRetries())
            .build();

    CompletableFuture<? extends PageableResponse<Item>> responseFuture =
        client.requestAsync(nextOptions, responseClass);

    return responseFuture.thenApply(
        response ->
            new PageAsync<>(
                client, CompletableFuture.completedFuture(response), responseClass, nextOptions));
  }
}
