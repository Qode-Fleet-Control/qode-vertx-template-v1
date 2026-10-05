package world.qode.app;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

public class MainVerticle extends VerticleBase {

  // The fleet injects PORT; 8888 is start.vertx.io's default.
  static int port() {
    return Integer.parseInt(System.getenv().getOrDefault("PORT", "8888"));
  }

  @Override
  public Future<?> start() {
    Router router = Router.router(vertx);
    router.get("/").handler(ctx -> ctx.json(new JsonObject().put("app", "vertx-template").put("status", "ok")));
    // The fleet's health check (fleet.conf HEALTH_PATH).
    router.get("/health").handler(ctx -> ctx.json(new JsonObject().put("status", "ok")));

    int port = port();
    return vertx.createHttpServer()
      .requestHandler(router)
      .listen(port, "0.0.0.0")
      .onSuccess(http -> System.out.println("HTTP server started on port " + http.actualPort()));
  }
}
