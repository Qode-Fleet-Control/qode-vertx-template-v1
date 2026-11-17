package world.qode.app;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 * Writes one JSON line to stdout for every finished HTTP request, health checks included:
 * {"method":"GET","path":"/health","status":200,"durationMs":1.23,"ua":"curl/8.5.0"}
 *
 * KEEP THIS: one log line per request (status + duration) is what the workspace Monitoring tab charts.
 * Never add request/response bodies, cookies, Authorization headers or query strings here.
 *
 * Mounted first on the router (router.route()), so it sees every request; it logs once the
 * response has ended.
 */
public class RequestLog implements Handler<RoutingContext> {

  @Override
  public void handle(RoutingContext ctx) {
    long start = System.nanoTime();
    ctx.addEndHandler(done -> {
      double durationMs = Math.round((System.nanoTime() - start) / 10_000.0) / 100.0;
      System.out.println("{\"method\":\"" + json(ctx.request().method().name()) + "\",\"path\":\""
        + json(ctx.request().path()) + "\",\"status\":" + ctx.response().getStatusCode()
        + ",\"durationMs\":" + durationMs + ",\"ua\":\"" + json(ctx.request().getHeader("User-Agent")) + "\"}");
    });
    ctx.next();
  }

  private static String json(String s) {
    if (s == null) {
      return "";
    }
    StringBuilder b = new StringBuilder(s.length());
    for (char c : s.toCharArray()) {
      switch (c) {
        case '"' -> b.append("\\\"");
        case '\\' -> b.append("\\\\");
        default -> {
          if (c < 0x20) {
            b.append(String.format("\\u%04x", (int) c));
          } else {
            b.append(c);
          }
        }
      }
    }
    return b.toString();
  }
}
