package vn.payos.core;

/** APIService */
public abstract class APIService<T> {
  /** HTTP client */
  protected final T client;

  /**
   * APIService
   *
   * @param client HTTP client
   */
  protected APIService(T client) {
    this.client = client;
  }
}
