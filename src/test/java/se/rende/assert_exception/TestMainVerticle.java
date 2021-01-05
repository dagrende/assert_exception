package se.rende.assert_exception;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

@ExtendWith(VertxExtension.class)
class TestMainVerticle {
	
	public class MainVerticle extends AbstractVerticle {

		  @Override
		  public void start(Promise<Void> startPromise) throws Exception {
		    vertx.createHttpServer().requestHandler(req -> {
		      req.response()
		        .putHeader("content-type", "text/plain")
		        .end("Hello from Vert.x!");
		    }).listen(8888, http -> {
		      if (http.succeeded()) {
		        startPromise.complete();
		        System.out.println("HTTP server started on port 8888");
		      } else {
		        startPromise.fail(http.cause());
		      }
		    });
		  }
		}
	
	@Test
	void smallText(Vertx vertx, VertxTestContext testContext) {
		testContext.verify(()->{
			assertEquals(1, 0);
			testContext.completeNow();
		});
	}
	
	@Test
	void testRequest(Vertx vertx, VertxTestContext testContext) {
		vertx.deployVerticle(new MainVerticle(), testContext.succeeding(id -> {
			System.out.println("id: " + id);
			WebClient client = WebClient.create(vertx);
			client.get(8888, "localhost", "/")
			.as(BodyCodec.string())
			.send(res -> {
				if (res.failed()) {
					System.err.println(res.cause());
				} else {
					HttpResponse<String> result = res.result();
					System.out.println("body: " + result.body());
					assertEquals(200, result.statusCode());
					assertEquals("Hi!", result.body());
				}
				testContext.completeNow();
			});
		}));
	}
}
