package vn.payos.core;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicReference;

/** AsyncAutoPager */
public class AsyncAutoPager<Item> implements AsyncStreamResponse<Item> {
  private final PageAsync<Item> initialPage;
  private final CompletableFuture<Void> onCompleteFuture = new CompletableFuture<>();
  private final AtomicReference<StreamState> state = new AtomicReference<>(StreamState.NEW);
  private final Executor defaultExecutor;

  /**
   * AsyncAutoPager
   *
   * @param initialPage initial page
   */
  public AsyncAutoPager(PageAsync<Item> initialPage) {
    this(initialPage, ForkJoinPool.commonPool());
  }

  /**
   * AsyncAutoPager
   *
   * @param initialPage initial page
   * @param defaultExecutor default executor
   */
  public AsyncAutoPager(PageAsync<Item> initialPage, Executor defaultExecutor) {
    this.initialPage = initialPage;
    this.defaultExecutor = defaultExecutor;
  }

  @Override
  public AsyncStreamResponse<Item> subscribe(Handler<Item> handler) {
    return subscribe(handler, defaultExecutor);
  }

  @Override
  public AsyncStreamResponse<Item> subscribe(Handler<Item> handler, Executor executor) {
    if (!state.compareAndSet(StreamState.NEW, StreamState.SUBSCRIBED)) {
      if (state.get() == StreamState.SUBSCRIBED) {
        throw new IllegalStateException("Cannot subscribe more than once");
      } else {
        throw new IllegalStateException("Cannot subscribe after the response is closed");
      }
    }

    processPageAsync(initialPage, handler, executor);
    return this;
  }

  @Override
  public CompletableFuture<Void> onCompleteFuture() {
    return onCompleteFuture;
  }

  @Override
  public void close() {
    StreamState previousState = state.getAndSet(StreamState.CLOSED);
    if (previousState == StreamState.CLOSED) {
      return;
    }

    if (!onCompleteFuture.isDone()) {
      onCompleteFuture.complete(null);
    }
  }

  private void processPageAsync(
      PageAsync<Item> currentPage, Handler<Item> handler, Executor executor) {
    CompletableFuture.runAsync(
            () -> {
              if (state.get() == StreamState.CLOSED) {
                return;
              }

              try {
                for (Item item : currentPage.getItems()) {
                  if (state.get() == StreamState.CLOSED) {
                    return;
                  }
                  handler.onNext(item);
                }
              } catch (Exception e) {
                handler.onComplete(Optional.of(e));
                completeWithError(e);
                return;
              }

              if (currentPage.hasNextPage() && state.get() != StreamState.CLOSED) {
                currentPage
                    .nextPage()
                    .thenAccept(nextPage -> processPageAsync(nextPage, handler, executor))
                    .exceptionally(
                        throwable -> {
                          handler.onComplete(Optional.of(throwable));
                          completeWithError(throwable);
                          return null;
                        });
              } else {
                if (state.get() != StreamState.CLOSED) {
                  handler.onComplete(Optional.empty());
                  completeSuccessfully();
                }
              }
            },
            executor)
        .exceptionally(
            throwable -> {
              handler.onComplete(Optional.of(throwable));
              completeWithError(throwable);
              return null;
            });
  }

  private void completeSuccessfully() {
    try {
      if (!onCompleteFuture.isDone()) {
        onCompleteFuture.complete(null);
      }
    } finally {
      close();
    }
  }

  private void completeWithError(Throwable error) {
    try {
      if (!onCompleteFuture.isDone()) {
        onCompleteFuture.completeExceptionally(error);
      }
    } finally {
      close();
    }
  }
}
