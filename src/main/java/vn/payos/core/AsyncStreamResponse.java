package vn.payos.core;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/** AsyncStreamResponse */
public interface AsyncStreamResponse<T> {

  /**
   * Registers handler to be called for events of this stream.
   *
   * <p>Handler's methods will be called in the client's configured or default thread pool.
   *
   * @param handler Handler to process items and completion events
   * @return this AsyncStreamResponse for chaining
   * @throws IllegalStateException if subscribe has already been called
   */
  AsyncStreamResponse<T> subscribe(Handler<T> handler);

  /**
   * Registers handler to be called for events of this stream.
   *
   * <p>Handler's methods will be called in the given executor.
   *
   * @param handler Handler to process items and completion events
   * @param executor Executor to run handler methods in
   * @return this AsyncStreamResponse for chaining
   * @throws IllegalStateException if subscribe has already been called
   */
  AsyncStreamResponse<T> subscribe(Handler<T> handler, Executor executor);

  /**
   * Returns a future that completes when a stream is fully consumed, errors, or gets closed early.
   *
   * @return CompletableFuture for completion tracking
   */
  CompletableFuture<Void> onCompleteFuture();

  /**
   * Closes this resource, relinquishing any underlying resources.
   *
   * <p>This is purposefully not inherited from AutoCloseable because this response should not be
   * synchronously closed via try-with-resources.
   */
  void close();

  /**
   * Handler interface for processing streaming responses.
   *
   * @param <T> The type of items being processed
   */
  @FunctionalInterface
  public interface Handler<T> {
    /**
     * Called for each item in the stream.
     *
     * @param item The item to process
     */
    void onNext(T item);

    /**
     * Called when the stream completes, either successfully or with an error.
     *
     * <p>onNext will not be called once this method is called.
     *
     * @param error Non-empty if the stream completed due to an error
     */
    default void onComplete(Optional<Throwable> error) {
      // Default implementation does nothing
    }
  }

  /** Stream state enumeration */
  enum State {
    /** NEW */
    NEW,
    /** SUBSCRIBED */
    SUBSCRIBED,
    /** CLOSED */
    CLOSED
  }
}
