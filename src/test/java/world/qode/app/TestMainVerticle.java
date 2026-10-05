package world.qode.app;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
public class TestMainVerticle {

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new MainVerticle()).onComplete(testContext.succeeding(id -> testContext.completeNow()));
  }

  @Test
  void verticle_deployed(Vertx vertx, VertxTestContext testContext) throws Throwable {
    testContext.completeNow();
  }

  @Test
  void health_answers_200(Vertx vertx, VertxTestContext testContext) {
    vertx.createHttpClient()
      .request(HttpMethod.GET, MainVerticle.port(), "127.0.0.1", "/health")
      .compose(req -> req.send())
      .onComplete(testContext.succeeding(resp -> testContext.verify(() -> {
        assertEquals(200, resp.statusCode());
        testContext.completeNow();
      })));
  }
}
