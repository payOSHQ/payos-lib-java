package vn.payos.core;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/** Minimal HTTP server bound to 127.0.0.1 that records requests for test assertions. */
final class LocalHttpServer implements AutoCloseable {

  /** A recorded request. */
  static final class Recorded {
    final String method;
    final String rawPath;
    final Map<String, String> headers;
    final String body;

    Recorded(String method, String rawPath, Map<String, String> headers, String body) {
      this.method = method;
      this.rawPath = rawPath;
      this.headers = headers;
      this.body = body;
    }

    String header(String name) {
      return headers.get(name.toLowerCase());
    }
  }

  /** A canned response. */
  static final class Reply {
    final int status;
    final Map<String, String> headers = new HashMap<>();
    final String body;

    Reply(int status, String body) {
      this.status = status;
      this.body = body;
    }

    Reply header(String name, String value) {
      headers.put(name, value);
      return this;
    }

    static Reply json(int status, String body) {
      return new Reply(status, body).header("Content-Type", "application/json");
    }

    static Reply redirect(int status, String location) {
      return new Reply(status, "").header("Location", location);
    }
  }

  private final HttpServer server;
  private final List<Recorded> requests = Collections.synchronizedList(new ArrayList<>());

  LocalHttpServer(Function<Recorded, Reply> handler) throws IOException {
    server = HttpServer.create(new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 0), 0);
    server.createContext(
        "/",
        exchange -> {
          Recorded rec = record(exchange);
          requests.add(rec);
          Reply reply;
          try {
            reply = handler.apply(rec);
          } catch (RuntimeException e) {
            reply = Reply.json(500, "{\"code\":\"500\",\"desc\":\"handler error\"}");
          }
          for (Map.Entry<String, String> h : reply.headers.entrySet()) {
            exchange.getResponseHeaders().add(h.getKey(), h.getValue());
          }
          byte[] bytes = reply.body.getBytes(StandardCharsets.UTF_8);
          exchange.sendResponseHeaders(reply.status, bytes.length == 0 ? -1 : bytes.length);
          if (bytes.length > 0) {
            try (OutputStream os = exchange.getResponseBody()) {
              os.write(bytes);
            }
          }
          exchange.close();
        });
    server.start();
  }

  private static Recorded record(HttpExchange exchange) throws IOException {
    Map<String, String> headers = new HashMap<>();
    for (Map.Entry<String, List<String>> h : exchange.getRequestHeaders().entrySet()) {
      headers.put(h.getKey().toLowerCase(), String.join(",", h.getValue()));
    }
    ByteArrayOutputStream buf = new ByteArrayOutputStream();
    try (InputStream in = exchange.getRequestBody()) {
      byte[] chunk = new byte[4096];
      int n;
      while ((n = in.read(chunk)) > 0) {
        buf.write(chunk, 0, n);
      }
    }
    return new Recorded(
        exchange.getRequestMethod(),
        exchange.getRequestURI().getRawPath(),
        headers,
        new String(buf.toByteArray(), StandardCharsets.UTF_8));
  }

  /** Base URL using the 127.0.0.1 host. */
  String url() {
    return "http://127.0.0.1:" + server.getAddress().getPort();
  }

  int port() {
    return server.getAddress().getPort();
  }

  List<Recorded> requests() {
    synchronized (requests) {
      return new ArrayList<>(requests);
    }
  }

  @Override
  public void close() {
    server.stop(0);
  }
}
