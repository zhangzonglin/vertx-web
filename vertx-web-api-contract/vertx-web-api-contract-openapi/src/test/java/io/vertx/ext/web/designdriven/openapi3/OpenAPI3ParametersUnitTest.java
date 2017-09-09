package io.vertx.ext.web.designdriven.openapi3;

import io.swagger.oas.models.OpenAPI;
import io.swagger.parser.models.ParseOptions;
import io.swagger.parser.v3.OpenAPIV3Parser;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RequestParameter;
import io.vertx.ext.web.RequestParameters;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.designdriven.openapi3.impl.OpenAPI3RouterFactoryImpl;
import io.vertx.ext.web.validation.WebTestValidationBase;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public class OpenAPI3ParametersUnitTest extends WebTestValidationBase {

  OpenAPI spec;
  ApiClient apiClient;
  OpenAPI3RouterFactory routerFactory;

  @Rule
  public ExternalResource resource = new ExternalResource() {
    @Override
    protected void before() throws Throwable {
      spec = loadSwagger("src/test/resources/swaggers/openapi.yaml");
    }

    @Override
    protected void after() {}
  };

  @Override
  public void setUp() throws Exception {
    super.setUp();
    stopServer(); // Have to stop default server of WebTestBase
    apiClient = new ApiClient(webClient);
    routerFactory = new OpenAPI3RouterFactoryImpl(this.vertx, spec);
    routerFactory.enableValidationFailureHandler(true);
    routerFactory.setValidationFailureHandler(generateFailureHandler());
    routerFactory.mountOperationsWithoutHandlers(false);
  }

  @Override
  public void tearDown() throws Exception {
    if (apiClient != null) {
      try {
        apiClient.close();
      } catch (IllegalStateException e) {}
    }
    stopServer();
    super.tearDown();
  }


  /**
   * Test: path_matrix_noexplode_string
   * Expected parameters sent:
   * color: ;color=blue
   * Expected response: {"color":"blue"}
   * @throws Exception
   */
  @Test
  public void testPathMatrixNoexplodeString() throws Exception {
    routerFactory.addHandlerByOperationId("path_matrix_noexplode_string", routingContext -> {
      RequestParameters params = routingContext.get("parsedParameters");
      JsonObject res = new JsonObject();

      RequestParameter color_path = params.pathParameter("color");
      assertNotNull(color_path);
      assertTrue(color_path.isString());
      assertEquals(color_path.getString(), "blue");
      res.put("color", color_path.getString());


      routingContext.response()
        .setStatusCode(200)
        .setStatusMessage("OK")
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(res.encode());
    });

    CountDownLatch latch = new CountDownLatch(1);

    String color_path;
    color_path = "blue";


    startServer();

    apiClient.pathMatrixNoexplodeString(color_path, (AsyncResult<HttpResponse> ar) -> {
      if (ar.succeeded()) {
        assertEquals(200, ar.result().statusCode());
        assertTrue("Expected: " + new JsonObject("{\"color\":\"blue\"}").encode() + " Actual: " + ar.result().bodyAsJsonObject().encode(), new JsonObject("{\"color\":\"blue\"}").equals(ar.result().bodyAsJsonObject()));
      } else {
        assertTrue(ar.cause().getMessage(), false);
      }
      latch.countDown();
    });
    awaitLatch(latch);

  }

  /**
   * Test: path_matrix_noexplode_array
   * Expected parameters sent:
   * color: ;color=blue,black,brown
   * Expected response: {"color":["blue","black","brown"]}
   * @throws Exception
   */
  @Test
  public void testPathMatrixNoexplodeArray() throws Exception {
    routerFactory.addHandlerByOperationId("path_matrix_noexplode_array", routingContext -> {
      RequestParameters params = routingContext.get("parsedParameters");
      JsonObject res = new JsonObject();

      RequestParameter color_path = params.pathParameter("color");
      assertNotNull(color_path);
      assertTrue(color_path.isArray());
      res.put("color", new JsonArray(color_path.getArray().stream().map(param -> param.getString()).collect(Collectors.toList())));


      routingContext.response()
        .setStatusCode(200)
        .setStatusMessage("OK")
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(res.encode());
    });

    CountDownLatch latch = new CountDownLatch(1);

    List<Object> color_path;
    color_path = new ArrayList<>();
    color_path.add("blue");
    color_path.add("black");
    color_path.add("brown");


    startServer();

    apiClient.pathMatrixNoexplodeArray(color_path, (AsyncResult<HttpResponse> ar) -> {
      if (ar.succeeded()) {
        assertEquals(200, ar.result().statusCode());
        assertTrue("Expected: " + new JsonObject("{\"color\":[\"blue\",\"black\",\"brown\"]}").encode() + " Actual: " + ar.result().bodyAsJsonObject().encode(), new JsonObject("{\"color\":[\"blue\",\"black\",\"brown\"]}").equals(ar.result().bodyAsJsonObject()));
      } else {
        assertTrue(ar.cause().getMessage(), false);
      }
      latch.countDown();
    });
    awaitLatch(latch);

  }

  /**
   * Test: path_matrix_noexplode_object
   * Expected parameters sent:
   * color: ;color=R,100,G,200,B,150
   * Expected response: {"color":{"R":"100","G":"200","B":"150"}}
   * @throws Exception
   */
  @Test
  public void testPathMatrixNoexplodeObject() throws Exception {
    routerFactory.addHandlerByOperationId("path_matrix_noexplode_object", routingContext -> {
      RequestParameters params = routingContext.get("parsedParameters");
      JsonObject res = new JsonObject();

      RequestParameter color_path = params.pathParameter("color");
      assertNotNull(color_path);
      assertTrue(color_path.isObject());
      Map<String, String> map = new HashMap<>();
      for (String key : color_path.getObjectKeys())
        map.put(key, color_path.getObjectValue(key).getString());
      res.put("color", map);
      

      routingContext.response()
        .setStatusCode(200)
        .setStatusMessage("OK")
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(res.encode());
    });

    CountDownLatch latch = new CountDownLatch(1);

    Map<String, Object> color_path;
    color_path = new HashMap<>();
    color_path.put("R", "100");
    color_path.put("G", "200");
    color_path.put("B", "150");


    startServer();

    apiClient.pathMatrixNoexplodeObject(color_path, (AsyncResult<HttpResponse> ar) -> {
      if (ar.succeeded()) {
        assertEquals(200, ar.result().statusCode());
        assertTrue("Expected: " + new JsonObject("{\"color\":{\"R\":\"100\",\"G\":\"200\",\"B\":\"150\"}}").encode() + " Actual: " + ar.result().bodyAsJsonObject().encode(), new JsonObject("{\"color\":{\"R\":\"100\",\"G\":\"200\",\"B\":\"150\"}}").equals(ar.result().bodyAsJsonObject()));
      } else {
        assertTrue(ar.cause().getMessage(), false);
      }
      latch.countDown();
    });
    awaitLatch(latch);

  }

  /**
   * Test: path_matrix_explode_string
   * Expected parameters sent:
   * color: ;color=blue
   * Expected response: {"color":"blue"}
   * @throws Exception
   */
  @Test
  public void testPathMatrixExplodeString() throws Exception {
    routerFactory.addHandlerByOperationId("path_matrix_explode_string", routingContext -> {
      RequestParameters params = routingContext.get("parsedParameters");
      JsonObject res = new JsonObject();

      RequestParameter color_path = params.pathParameter("color");
      assertNotNull(color_path);
      assertTrue(color_path.isString());
      assertEquals(color_path.getString(), "blue");
      res.put("color", color_path.getString());


      routingContext.response()
        .setStatusCode(200)
        .setStatusMessage("OK")
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(res.encode());
    });

    CountDownLatch latch = new CountDownLatch(1);

    String color_path;
    color_path = "blue";


    startServer();

    apiClient.pathMatrixExplodeString(color_path, (AsyncResult<HttpResponse> ar) -> {
      if (ar.succeeded()) {
        assertEquals(200, ar.result().statusCode());
        assertTrue("Expected: " + new JsonObject("{\"color\":\"blue\"}").encode() + " Actual: " + ar.result().bodyAsJsonObject().encode(), new JsonObject("{\"color\":\"blue\"}").equals(ar.result().bodyAsJsonObject()));
      } else {
        assertTrue(ar.cause().getMessage(), false);
      }
      latch.countDown();
    });
    awaitLatch(latch);

  }

  /**
   * Test: path_matrix_explode_array
   * Expected parameters sent:
   * color: ;color=blue;color=black;color=brown
   * Expected response: {"color":["blue","black","brown"]}
   * @throws Exception
   */
  @Test
  public void testPathMatrixExplodeArray() throws Exception {
    routerFactory.addHandlerByOperationId("path_matrix_explode_array", routingContext -> {
      RequestParameters params = routingContext.get("parsedParameters");
      JsonObject res = new JsonObject();

      RequestParameter color_path = params.pathParameter("color");
      assertNotNull(color_path);
      assertTrue(color_path.isArray());
      res.put("color", new JsonArray(color_path.getArray().stream().map(param -> param.getString()).collect(Collectors.toList())));


      routingContext.response()
        .setStatusCode(200)
        .setStatusMessage("OK")
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(res.encode());
    });

    CountDownLatch latch = new CountDownLatch(1);

    List<Object> color_path;
    color_path = new ArrayList<>();
    color_path.add("blue");
    color_path.add("black");
    color_path.add("brown");


    startServer();

    apiClient.pathMatrixExplodeArray(color_path, (AsyncResult<HttpResponse> ar) -> {
      if (ar.succeeded()) {
        assertEquals(200, ar.result().statusCode());
        assertTrue("Expected: " + new JsonObject("{\"color\":[\"blue\",\"black\",\"brown\"]}").encode() + " Actual: " + ar.result().bodyAsJsonObject().encode(), new JsonObject("{\"color\":[\"blue\",\"black\",\"brown\"]}").equals(ar.result().bodyAsJsonObject()));
      } else {
        assertTrue(ar.cause().getMessage(), false);
      }
      latch.countDown();
    });
    awaitLatch(latch);

  }

  /**
   * Test: path_matrix_explode_object
   * Expected parameters sent:
   * color: ;R=100;G=200;B=150
   * Expected response: {"color":{"R":"100","G":"200","B":"150"}}
   * @throws Exception
   */
  @Test
  public void testPathMatrixExplodeObject() throws Exception {
    routerFactory.addHandlerByOperationId("path_matrix_explode_object", routingContext -> {
      RequestParameters params = routingContext.get("parsedParameters");
      JsonObject res = new JsonObject();

      RequestParameter color_path = params.pathParameter("color");
      assertNotNull(color_path);
      assertTrue(color_path.isObject());
      Map<String, String> map = new HashMap<>();
      for (String key : color_path.getObjectKeys())
        map.put(key, color_path.getObjectValue(key).getString());
      res.put("color", map);
      

      routingContext.response()
        .setStatusCode(200)
        .setStatusMessage("OK")
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(res.encode());
    });

    CountDownLatch latch = new CountDownLatch(1);

    Map<String, Object> color_path;
    color_path = new HashMap<>();
    color_path.put("R", "100");
    color_path.put("G", "200");
    color_path.put("B", "150");


    startServer();

    apiClient.pathMatrixExplodeObject(color_path, (AsyncResult<HttpResponse> ar) -> {
      if (ar.succeeded()) {
        assertEquals(200, ar.result().statusCode());
        assertTrue("Expected: " + new JsonObject("{\"color\":{\"R\":\"100\",\"G\":\"200\",\"B\":\"150\"}}").encode() + " Actual: " + ar.result().bodyAsJsonObject().encode(), new JsonObject("{\"color\":{\"R\":\"100\",\"G\":\"200\",\"B\":\"150\"}}").equals(ar.result().bodyAsJsonObject()));
      } else {
        assertTrue(ar.cause().getMessage(), false);
      }
      latch.countDown();
    });
    awaitLatch(latch);

  }

  /**
   * Test: path_label_noexplode_string
   * Expected parameters sent:
   * color: .blue
   * Expected response: {"color":"blue"}
   * @throws Exception
   */
  @Test
  public void testPathLabelNoexplodeString() throws Exception {
    routerFactory.addHandlerByOperationId("path_label_noexplode_string", routingContext -> {
      RequestParameters params = routingContext.get("parsedParameters");
      JsonObject res = new JsonObject();

      RequestParameter color_path = params.pathParameter("color");
      assertNotNull(color_path);
      assertTrue(color_path.isString());
      assertEquals(color_path.getString(), "blue");
      res.put("color", color_path.getString());


      routingContext.response()
        .setStatusCode(200)
        .setStatusMessage("OK")
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(res.encode());
    });

    CountDownLatch latch = new CountDownLatch(1);

    String color_path;
    color_path = "blue";


    startServer();

    apiClient.pathLabelNoexplodeString(color_path, (AsyncResult<HttpResponse> ar) -> {
      if (ar.succeeded()) {
        assertEquals(200, ar.result().statusCode());
        assertTrue("Expected: " + new JsonObject("{\"color\":\"blue\"}").encode() + " Actual: " + ar.result().bodyAsJsonObject().encode(), new JsonObject("{\"color\":\"blue\"}").equals(ar.result().bodyAsJsonObject()));
      } else {
        assertTrue(ar.cause().getMessage(), false);
      }
      latch.countDown();
    });
    awaitLatch(latch);

  }

  /**
   * Test: path_label_noexplode_array
   * Expected parameters sent:
   * color: .blue.black.brown
   * Expected response: {"color":["blue","black","brown"]}
   * @throws Exception
   */
  @Test
  public void testPathLabelNoexplodeArray() throws Exception {
    routerFactory.addHandlerByOperationId("path_label_noexplode_array", routingContext -> {
      RequestParameters params = routingContext.get("parsedParameters");
      JsonObject res = new JsonObject();

      RequestParameter color_path = params.pathParameter("color");
      assertNotNull(color_path);
      assertTrue(color_path.isArray());
      res.put("color", new JsonArray(color_path.getArray().stream().map(param -> param.getString()).collect(Collectors.toList())));


      routingContext.response()
        .setStatusCode(200)
        .setStatusMessage("OK")
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(res.encode());
    });

    CountDownLatch latch = new CountDownLatch(1);

    List<Object> color_path;
    color_path = new ArrayList<>();
    color_path.add("blue");
    color_path.add("black");
    color_path.add("brown");


    startServer();

    apiClient.pathLabelNoexplodeArray(color_path, (AsyncResult<HttpResponse> ar) -> {
      if (ar.succeeded()) {
        assertEquals(200, ar.result().statusCode());
        assertTrue("Expected: " + new JsonObject("{\"color\":[\"blue\",\"black\",\"brown\"]}").encode() + " Actual: " + ar.result().bodyAsJsonObject().encode(), new JsonObject("{\"color\":[\"blue\",\"black\",\"brown\"]}").equals(ar.result().bodyAsJsonObject()));
      } else {
        assertTrue(ar.cause().getMessage(), false);
      }
      latch.countDown();
    });
    awaitLatch(latch);

  }

  /**
   * Test: path_label_noexplode_object
   * Expected parameters sent:
   * color: .R.100.G.200.B.150
   * Expected response: {"color":{"R":"100","G":"200","B":"150"}}
   * @throws Exception
   */
  @Test
  public void testPathLabelNoexplodeObject() throws Exception {
    routerFactory.addHandlerByOperationId("path_label_noexplode_object", routingContext -> {
      RequestParameters params = routingContext.get("parsedParameters");
      JsonObject res = new JsonObject();

      RequestParameter color_path = params.pathParameter("color");
      assertNotNull(color_path);
      assertTrue(color_path.isObject());
      Map<String, String> map = new HashMap<>();
      for (String key : color_path.getObjectKeys())
        map.put(key, color_path.getObjectValue(key).getString());
      res.put("color", map);
      

      routingContext.response()
        .setStatusCode(200)
        .setStatusMessage("OK")
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(res.encode());
    });

    CountDownLatch latch = new CountDownLatch(1);

    Map<String, Object> color_path;
    color_path = new HashMap<>();
    color_path.put("R", "100");
    color_path.put("G", "200");
    color_path.put("B", "150");


    startServer();

    apiClient.pathLabelNoexplodeObject(color_path, (AsyncResult<HttpResponse> ar) -> {
      if (ar.succeeded()) {
        assertEquals(200, ar.result().statusCode());
        assertTrue("Expected: " + new JsonObject("{\"color\":{\"R\":\"100\",\"G\":\"200\",\"B\":\"150\"}}").encode() + " Actual: " + ar.result().bodyAsJsonObject().encode(), new JsonObject("{\"color\":{\"R\":\"100\",\"G\":\"200\",\"B\":\"150\"}}").equals(ar.result().bodyAsJsonObject()));
      } else {
        assertTrue(ar.cause().getMessage(), false);
      }
      latch.countDown();
    });
    awaitLatch(latch);

  }

  /**
   * Test: path_label_explode_string
   * Expected parameters sent:
   * color: .blue
   * Expected response: {"color":"blue"}
   * @throws Exception
   */
  @Test
  public void testPathLabelExplodeString() throws Exception {
    routerFactory.addHandlerByOperationId("path_label_explode_string", routingContext -> {
      RequestParameters params = routingContext.get("parsedParameters");
      JsonObject res = new JsonObject();

      RequestParameter color_path = params.pathParameter("color");
      assertNotNull(color_path);
      assertTrue(color_path.isString());
      assertEquals(color_path.getString(), "blue");
      res.put("color", color_path.getString());


      routingContext.response()
        .setStatusCode(200)
        .setStatusMessage("OK")
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(res.encode());
    });

    CountDownLatch latch = new CountDownLatch(1);

    String color_path;
    color_path = "blue";


    startServer();

    apiClient.pathLabelExplodeString(color_path, (AsyncResult<HttpResponse> ar) -> {
      if (ar.succeeded()) {
        assertEquals(200, ar.result().statusCode());
        assertTrue("Expected: " + new JsonObject("{\"color\":\"blue\"}").encode() + " Actual: " + ar.result().bodyAsJsonObject().encode(), new JsonObject("{\"color\":\"blue\"}").equals(ar.result().bodyAsJsonObject()));
      } else {
        assertTrue(ar.cause().getMessage(), false);
      }
      latch.countDown();
    });
    awaitLatch(latch);

  }

  /**
   * Test: path_label_explode_array
   * Expected parameters sent:
   * color: .blue.black.brown
   * Expected response: {"color":["blue","black","brown"]}
   * @throws Exception
   */
  @Test
  public void testPathLabelExplodeArray() throws Exception {
    routerFactory.addHandlerByOperationId("path_label_explode_array", routingContext -> {
      RequestParameters params = routingContext.get("parsedParameters");
      JsonObject res = new JsonObject();

      RequestParameter color_path = params.pathParameter("color");
      assertNotNull(color_path);
      assertTrue(color_path.isArray());
      res.put("color", new JsonArray(color_path.getArray().stream().map(param -> param.getString()).collect(Collectors.toList())));


      routingContext.response()
        .setStatusCode(200)
        .setStatusMessage("OK")
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(res.encode());
    });

    CountDownLatch latch = new CountDownLatch(1);

    List<Object> color_path;
    color_path = new ArrayList<>();
    color_path.add("blue");
    color_path.add("black");
    color_path.add("brown");


    startServer();

    apiClient.pathLabelExplodeArray(color_path, (AsyncResult<HttpResponse> ar) -> {
      if (ar.succeeded()) {
        assertEquals(200, ar.result().statusCode());
        assertTrue("Expected: " + new JsonObject("{\"color\":[\"blue\",\"black\",\"brown\"]}").encode() + " Actual: " + ar.result().bodyAsJsonObject().encode(), new JsonObject("{\"color\":[\"blue\",\"black\",\"brown\"]}").equals(ar.result().bodyAsJsonObject()));
      } else {
        assertTrue(ar.cause().getMessage(), false);
      }
      latch.countDown();
    });
    awaitLatch(latch);

  }

  /**
   * Test: path_label_explode_object
   * Expected parameters sent:
   * color: .R=100.G=200.B=150
   * Expected response: {"color":{"R":"100","G":"200","B":"150"}}
   * @throws Exception
   */
  @Test
  public void testPathLabelExplodeObject() throws Exception {
    routerFactory.addHandlerByOperationId("path_label_explode_object", routingContext -> {
      RequestParameters params = routingContext.get("parsedParameters");
      JsonObject res = new JsonObject();

      RequestParameter color_path = params.pathParameter("color");
      assertNotNull(color_path);
      assertTrue(color_path.isObject());
      Map<String, String> map = new HashMap<>();
      for (String key : color_path.getObjectKeys())
        map.put(key, color_path.getObjectValue(key).getString());
      res.put("color", map);
      

      routingContext.response()
        .setStatusCode(200)
        .setStatusMessage("OK")
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(res.encode());
    });

    CountDownLatch latch = new CountDownLatch(1);

    Map<String, Object> color_path;
    color_path = new HashMap<>();
    color_path.put("R", "100");
    color_path.put("G", "200");
    color_path.put("B", "150");


    startServer();

    apiClient.pathLabelExplodeObject(color_path, (AsyncResult<HttpResponse> ar) -> {
      if (ar.succeeded()) {
        assertEquals(200, ar.result().statusCode());
        assertTrue("Expected: " + new JsonObject("{\"color\":{\"R\":\"100\",\"G\":\"200\",\"B\":\"150\"}}").encode() + " Actual: " + ar.result().bodyAsJsonObject().encode(), new JsonObject("{\"color\":{\"R\":\"100\",\"G\":\"200\",\"B\":\"150\"}}").equals(ar.result().bodyAsJsonObject()));
      } else {
        assertTrue(ar.cause().getMessage(), false);
      }
      latch.countDown();
    });
    awaitLatch(latch);

  }

  /**
   * Test: path_simple_noexplode_string
   * Expected parameters sent:
   * color: blue
   * Expected response: {"color":"blue"}
   * @throws Exception
   */
  @Test
  public void testPathSimpleNoexplodeString() throws Exception {
    routerFactory.addHandlerByOperationId("path_simple_noexplode_string", routingContext -> {
      RequestParameters params = routingContext.get("parsedParameters");
      JsonObject res = new JsonObject();

      RequestParameter color_path = params.pathParameter("color");
      assertNotNull(color_path);
      assertTrue(color_path.isString());
      assertEquals(color_path.getString(), "blue");
      res.put("color", color_path.getString());


      routingContext.response()
        .setStatusCode(200)
        .setStatusMessage("OK")
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(res.encode());
    });

    CountDownLatch latch = new CountDownLatch(1);

    String color_path;
    color_path = "blue";


    startServer();

    apiClient.pathSimpleNoexplodeString(color_path, (AsyncResult<HttpResponse> ar) -> {
      if (ar.succeeded()) {
        assertEquals(200, ar.result().statusCode());
        assertTrue("Expected: " + new JsonObject("{\"color\":\"blue\"}").encode() + " Actual: " + ar.result().bodyAsJsonObject().encode(), new JsonObject("{\"color\":\"blue\"}").equals(ar.result().bodyAsJsonObject()));
      } else {
        assertTrue(ar.cause().getMessage(), false);
      }
      latch.countDown();
    });
    awaitLatch(latch);

  }

  /**
   * Test: path_simple_noexplode_array
   * Expected parameters sent:
   * color: blue,black,brown
   * Expected response: {"color":["blue","black","brown"]}
   * @throws Exception
   */
  @Test
  public void testPathSimpleNoexplodeArray() throws Exception {
    routerFactory.addHandlerByOperationId("path_simple_noexplode_array", routingContext -> {
      RequestParameters params = routingContext.get("parsedParameters");
      JsonObject res = new JsonObject();

      RequestParameter color_path = params.pathParameter("color");
      assertNotNull(color_path);
      assertTrue(color_path.isArray());
      res.put("color", new JsonArray(color_path.getArray().stream().map(param -> param.getString()).collect(Collectors.toList())));


      routingContext.response()
        .setStatusCode(200)
        .setStatusMessage("OK")
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(res.encode());
    });

    CountDownLatch latch = new CountDownLatch(1);

    List<Object> color_path;
    color_path = new ArrayList<>();
    color_path.add("blue");
    color_path.add("black");
    color_path.add("brown");


    startServer();

    apiClient.pathSimpleNoexplodeArray(color_path, (AsyncResult<HttpResponse> ar) -> {
      if (ar.succeeded()) {
        assertEquals(200, ar.result().statusCode());
        assertTrue("Expected: " + new JsonObject("{\"color\":[\"blue\",\"black\",\"brown\"]}").encode() + " Actual: " + ar.result().bodyAsJsonObject().encode(), new JsonObject("{\"color\":[\"blue\",\"black\",\"brown\"]}").equals(ar.result().bodyAsJsonObject()));
      } else {
        assertTrue(ar.cause().getMessage(), false);
      }
      latch.countDown();
    });
    awaitLatch(latch);

  }

  /**
   * Test: path_simple_noexplode_object
   * Expected parameters sent:
   * color: R,100,G,200,B,150
   * Expected response: {"color":{"R":"100","G":"200","B":"150"}}
   * @throws Exception
   */
  @Test
  public void testPathSimpleNoexplodeObject() throws Exception {
    routerFactory.addHandlerByOperationId("path_simple_noexplode_object", routingContext -> {
      RequestParameters params = routingContext.get("parsedParameters");
      JsonObject res = new JsonObject();

      RequestParameter color_path = params.pathParameter("color");
      assertNotNull(color_path);
      assertTrue(color_path.isObject());
      Map<String, String> map = new HashMap<>();
      for (String key : color_path.getObjectKeys())
        map.put(key, color_path.getObjectValue(key).getString());
      res.put("color", map);
      

      routingContext.response()
        .setStatusCode(200)
        .setStatusMessage("OK")
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(res.encode());
    });

    CountDownLatch latch = new CountDownLatch(1);

    Map<String, Object> color_path;
    color_path = new HashMap<>();
    color_path.put("R", "100");
    color_path.put("G", "200");
    color_path.put("B", "150");


    startServer();

    apiClient.pathSimpleNoexplodeObject(color_path, (AsyncResult<HttpResponse> ar) -> {
      if (ar.succeeded()) {
        assertEquals(200, ar.result().statusCode());
        assertTrue("Expected: " + new JsonObject("{\"color\":{\"R\":\"100\",\"G\":\"200\",\"B\":\"150\"}}").encode() + " Actual: " + ar.result().bodyAsJsonObject().encode(), new JsonObject("{\"color\":{\"R\":\"100\",\"G\":\"200\",\"B\":\"150\"}}").equals(ar.result().bodyAsJsonObject()));
      } else {
        assertTrue(ar.cause().getMessage(), false);
      }
      latch.countDown();
    });
    awaitLatch(latch);

  }

  /**
   * Test: path_simple_explode_string
   * Expected parameters sent:
   * color: blue
   * Expected response: {"color":"blue"}
   * @throws Exception
   */
  @Test
  public void testPathSimpleExplodeString() throws Exception {
    routerFactory.addHandlerByOperationId("path_simple_explode_string", routingContext -> {
      RequestParameters params = routingContext.get("parsedParameters");
      JsonObject res = new JsonObject();

      RequestParameter color_path = params.pathParameter("color");
      assertNotNull(color_path);
      assertTrue(color_path.isString());
      assertEquals(color_path.getString(), "blue");
      res.put("color", color_path.getString());


      routingContext.response()
        .setStatusCode(200)
        .setStatusMessage("OK")
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(res.encode());
    });

    CountDownLatch latch = new CountDownLatch(1);

    String color_path;
    color_path = "blue";


    startServer();

    apiClient.pathSimpleExplodeString(color_path, (AsyncResult<HttpResponse> ar) -> {
      if (ar.succeeded()) {
        assertEquals(200, ar.result().statusCode());
        assertTrue("Expected: " + new JsonObject("{\"color\":\"blue\"}").encode() + " Actual: " + ar.result().bodyAsJsonObject().encode(), new JsonObject("{\"color\":\"blue\"}").equals(ar.result().bodyAsJsonObject()));
      } else {
        assertTrue(ar.cause().getMessage(), false);
      }
      latch.countDown();
    });
    awaitLatch(latch);

  }

  /**
   * Test: path_simple_explode_array
   * Expected parameters sent:
   * color: blue,black,brown
   * Expected response: {"color":["blue","black","brown"]}
   * @throws Exception
   */
  @Test
  public void testPathSimpleExplodeArray() throws Exception {
    routerFactory.addHandlerByOperationId("path_simple_explode_array", routingContext -> {
      RequestParameters params = routingContext.get("parsedParameters");
      JsonObject res = new JsonObject();

      RequestParameter color_path = params.pathParameter("color");
      assertNotNull(color_path);
      assertTrue(color_path.isArray());
      res.put("color", new JsonArray(color_path.getArray().stream().map(param -> param.getString()).collect(Collectors.toList())));


      routingContext.response()
        .setStatusCode(200)
        .setStatusMessage("OK")
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(res.encode());
    });

    CountDownLatch latch = new CountDownLatch(1);

    List<Object> color_path;
    color_path = new ArrayList<>();
    color_path.add("blue");
    color_path.add("black");
    color_path.add("brown");


    startServer();

    apiClient.pathSimpleExplodeArray(color_path, (AsyncResult<HttpResponse> ar) -> {
      if (ar.succeeded()) {
        assertEquals(200, ar.result().statusCode());
        assertTrue("Expected: " + new JsonObject("{\"color\":[\"blue\",\"black\",\"brown\"]}").encode() + " Actual: " + ar.result().bodyAsJsonObject().encode(), new JsonObject("{\"color\":[\"blue\",\"black\",\"brown\"]}").equals(ar.result().bodyAsJsonObject()));
      } else {
        assertTrue(ar.cause().getMessage(), false);
      }
      latch.countDown();
    });
    awaitLatch(latch);

  }

  /**
   * Test: path_simple_explode_object
   * Expected parameters sent:
   * color: R=100,G=200,B=150
   * Expected response: {"color":{"R":"100","G":"200","B":"150"}}
   * @throws Exception
   */
  @Test
  public void testPathSimpleExplodeObject() throws Exception {
    routerFactory.addHandlerByOperationId("path_simple_explode_object", routingContext -> {
      RequestParameters params = routingContext.get("parsedParameters");
      JsonObject res = new JsonObject();

      RequestParameter color_path = params.pathParameter("color");
      assertNotNull(color_path);
      assertTrue(color_path.isObject());
      Map<String, String> map = new HashMap<>();
      for (String key : color_path.getObjectKeys())
        map.put(key, color_path.getObjectValue(key).getString());
      res.put("color", map);
      

      routingContext.response()
        .setStatusCode(200)
        .setStatusMessage("OK")
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(res.encode());
    });

    CountDownLatch latch = new CountDownLatch(1);

    Map<String, Object> color_path;
    color_path = new HashMap<>();
    color_path.put("R", "100");
    color_path.put("G", "200");
    color_path.put("B", "150");


    startServer();

    apiClient.pathSimpleExplodeObject(color_path, (AsyncResult<HttpResponse> ar) -> {
      if (ar.succeeded()) {
        assertEquals(200, ar.result().statusCode());
        assertTrue("Expected: " + new JsonObject("{\"color\":{\"R\":\"100\",\"G\":\"200\",\"B\":\"150\"}}").encode() + " Actual: " + ar.result().bodyAsJsonObject().encode(), new JsonObject("{\"color\":{\"R\":\"100\",\"G\":\"200\",\"B\":\"150\"}}").equals(ar.result().bodyAsJsonObject()));
      } else {
        assertTrue(ar.cause().getMessage(), false);
      }
      latch.countDown();
    });
    awaitLatch(latch);

  }

  /**
   * Test: path_multi_simple_label
   * Expected parameters sent:
   * color_simple: blue
   * color_label: .blue.black.brown
   * Expected response: {"color_simple":"blue","color_label":["blue","black","brown"]}
   * @throws Exception
   */
  @Test
  public void testPathMultiSimpleLabel() throws Exception {
    routerFactory.addHandlerByOperationId("path_multi_simple_label", routingContext -> {
      RequestParameters params = routingContext.get("parsedParameters");
      JsonObject res = new JsonObject();

      RequestParameter colorSimple_path = params.pathParameter("color_simple");
      assertNotNull(colorSimple_path);
      assertTrue(colorSimple_path.isString());
      assertEquals(colorSimple_path.getString(), "blue");
      res.put("color_simple", colorSimple_path.getString());
      RequestParameter colorLabel_path = params.pathParameter("color_label");
      assertNotNull(colorLabel_path);
      assertTrue(colorLabel_path.isArray());
      res.put("color_label", new JsonArray(colorLabel_path.getArray().stream().map(param -> param.getString()).collect(Collectors.toList())));


      routingContext.response()
        .setStatusCode(200)
        .setStatusMessage("OK")
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(res.encode());
    });

    CountDownLatch latch = new CountDownLatch(1);

    String colorSimple_path;
    colorSimple_path = "blue";
    List<Object> colorLabel_path;
    colorLabel_path = new ArrayList<>();
    colorLabel_path.add("blue");
    colorLabel_path.add("black");
    colorLabel_path.add("brown");


    startServer();

    apiClient.pathMultiSimpleLabel(colorSimple_path, colorLabel_path, (AsyncResult<HttpResponse> ar) -> {
      if (ar.succeeded()) {
        assertEquals(200, ar.result().statusCode());
        assertTrue("Expected: " + new JsonObject("{\"color_simple\":\"blue\",\"color_label\":[\"blue\",\"black\",\"brown\"]}").encode() + " Actual: " + ar.result().bodyAsJsonObject().encode(), new JsonObject("{\"color_simple\":\"blue\",\"color_label\":[\"blue\",\"black\",\"brown\"]}").equals(ar.result().bodyAsJsonObject()));
      } else {
        assertTrue(ar.cause().getMessage(), false);
      }
      latch.countDown();
    });
    awaitLatch(latch);

  }

  /**
   * Test: path_multi_simple_matrix
   * Expected parameters sent:
   * color_simple: blue
   * color_matrix: ;color=blue,black,brown
   * Expected response: {"color_simple":"blue","color_matrix":["blue","black","brown"]}
   * @throws Exception
   */
  @Test
  public void testPathMultiSimpleMatrix() throws Exception {
    routerFactory.addHandlerByOperationId("path_multi_simple_matrix", routingContext -> {
      RequestParameters params = routingContext.get("parsedParameters");
      JsonObject res = new JsonObject();

      RequestParameter colorSimple_path = params.pathParameter("color_simple");
      assertNotNull(colorSimple_path);
      assertTrue(colorSimple_path.isString());
      assertEquals(colorSimple_path.getString(), "blue");
      res.put("color_simple", colorSimple_path.getString());
      RequestParameter colorMatrix_path = params.pathParameter("color_matrix");
      assertNotNull(colorMatrix_path);
      assertTrue(colorMatrix_path.isArray());
      res.put("color_matrix", new JsonArray(colorMatrix_path.getArray().stream().map(param -> param.getString()).collect(Collectors.toList())));


      routingContext.response()
        .setStatusCode(200)
        .setStatusMessage("OK")
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(res.encode());
    });

    CountDownLatch latch = new CountDownLatch(1);

    String colorSimple_path;
    colorSimple_path = "blue";
    List<Object> colorMatrix_path;
    colorMatrix_path = new ArrayList<>();
    colorMatrix_path.add("blue");
    colorMatrix_path.add("black");
    colorMatrix_path.add("brown");


    startServer();

    apiClient.pathMultiSimpleMatrix(colorSimple_path, colorMatrix_path, (AsyncResult<HttpResponse> ar) -> {
      if (ar.succeeded()) {
        assertEquals(200, ar.result().statusCode());
        assertTrue("Expected: " + new JsonObject("{\"color_simple\":\"blue\",\"color_matrix\":[\"blue\",\"black\",\"brown\"]}").encode() + " Actual: " + ar.result().bodyAsJsonObject().encode(), new JsonObject("{\"color_simple\":\"blue\",\"color_matrix\":[\"blue\",\"black\",\"brown\"]}").equals(ar.result().bodyAsJsonObject()));
      } else {
        assertTrue(ar.cause().getMessage(), false);
      }
      latch.countDown();
    });
    awaitLatch(latch);

  }

  /**
   * Test: path_multi_label_matrix
   * Expected parameters sent:
   * color_label: .blue.black.brown
   * color_matrix: ;R=100;G=200;B=150
   * Expected response: {"color_label":["blue","black","brown"],"color_matrix":{"R":"100","G":"200","B":"150"}}
   * @throws Exception
   */
  @Test
  public void testPathMultiLabelMatrix() throws Exception {
    routerFactory.addHandlerByOperationId("path_multi_label_matrix", routingContext -> {
      RequestParameters params = routingContext.get("parsedParameters");
      JsonObject res = new JsonObject();

      RequestParameter colorLabel_path = params.pathParameter("color_label");
      assertNotNull(colorLabel_path);
      assertTrue(colorLabel_path.isArray());
      res.put("color_label", new JsonArray(colorLabel_path.getArray().stream().map(param -> param.getString()).collect(Collectors.toList())));
      RequestParameter colorMatrix_path = params.pathParameter("color_matrix");
      assertNotNull(colorMatrix_path);
      assertTrue(colorMatrix_path.isObject());
      Map<String, String> map = new HashMap<>();
      for (String key : colorMatrix_path.getObjectKeys())
        map.put(key, colorMatrix_path.getObjectValue(key).getString());
      res.put("color_matrix", map);
      

      routingContext.response()
        .setStatusCode(200)
        .setStatusMessage("OK")
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(res.encode());
    });

    CountDownLatch latch = new CountDownLatch(1);

    List<Object> colorLabel_path;
    colorLabel_path = new ArrayList<>();
    colorLabel_path.add("blue");
    colorLabel_path.add("black");
    colorLabel_path.add("brown");
    Map<String, Object> colorMatrix_path;
    colorMatrix_path = new HashMap<>();
    colorMatrix_path.put("R", "100");
    colorMatrix_path.put("G", "200");
    colorMatrix_path.put("B", "150");


    startServer();

    apiClient.pathMultiLabelMatrix(colorLabel_path, colorMatrix_path, (AsyncResult<HttpResponse> ar) -> {
      if (ar.succeeded()) {
        assertEquals(200, ar.result().statusCode());
        assertTrue("Expected: " + new JsonObject("{\"color_label\":[\"blue\",\"black\",\"brown\"],\"color_matrix\":{\"R\":\"100\",\"G\":\"200\",\"B\":\"150\"}}").encode() + " Actual: " + ar.result().bodyAsJsonObject().encode(), new JsonObject("{\"color_label\":[\"blue\",\"black\",\"brown\"],\"color_matrix\":{\"R\":\"100\",\"G\":\"200\",\"B\":\"150\"}}").equals(ar.result().bodyAsJsonObject()));
      } else {
        assertTrue(ar.cause().getMessage(), false);
      }
      latch.countDown();
    });
    awaitLatch(latch);

  }

  /**
   * Test: query_form_noexplode_empty
   * Expected parameters sent:
   * color: color=
   * Expected response: {"color":null}
   * @throws Exception
   */
  @Test
  public void testQueryFormNoexplodeEmpty() throws Exception {
    routerFactory.addHandlerByOperationId("query_form_noexplode_empty", routingContext -> {
      RequestParameters params = routingContext.get("parsedParameters");
      JsonObject res = new JsonObject();

      RequestParameter color_query = params.queryParameter("color");
      assertNotNull(color_query);
      assertTrue(color_query.isEmpty());
      res.putNull("color");


      routingContext.response()
        .setStatusCode(200)
        .setStatusMessage("OK")
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(res.encode());
    });

    CountDownLatch latch = new CountDownLatch(1);

    String color_query;
    color_query = "";


    startServer();

    apiClient.queryFormNoexplodeEmpty(color_query, (AsyncResult<HttpResponse> ar) -> {
      if (ar.succeeded()) {
        assertEquals(200, ar.result().statusCode());
        assertTrue("Expected: " + new JsonObject("{\"color\":null}").encode() + " Actual: " + ar.result().bodyAsJsonObject().encode(), new JsonObject("{\"color\":null}").equals(ar.result().bodyAsJsonObject()));
      } else {
        assertTrue(ar.cause().getMessage(), false);
      }
      latch.countDown();
    });
    awaitLatch(latch);

  }

  /**
   * Test: query_form_noexplode_string
   * Expected parameters sent:
   * color: color=blue
   * Expected response: {"color":"blue"}
   * @throws Exception
   */
  @Test
  public void testQueryFormNoexplodeString() throws Exception {
    routerFactory.addHandlerByOperationId("query_form_noexplode_string", routingContext -> {
      RequestParameters params = routingContext.get("parsedParameters");
      JsonObject res = new JsonObject();

      RequestParameter color_query = params.queryParameter("color");
      assertNotNull(color_query);
      assertTrue(color_query.isString());
      assertEquals(color_query.getString(), "blue");
      res.put("color", color_query.getString());


      routingContext.response()
        .setStatusCode(200)
        .setStatusMessage("OK")
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(res.encode());
    });

    CountDownLatch latch = new CountDownLatch(1);

    String color_query;
    color_query = "blue";


    startServer();

    apiClient.queryFormNoexplodeString(color_query, (AsyncResult<HttpResponse> ar) -> {
      if (ar.succeeded()) {
        assertEquals(200, ar.result().statusCode());
        assertTrue("Expected: " + new JsonObject("{\"color\":\"blue\"}").encode() + " Actual: " + ar.result().bodyAsJsonObject().encode(), new JsonObject("{\"color\":\"blue\"}").equals(ar.result().bodyAsJsonObject()));
      } else {
        assertTrue(ar.cause().getMessage(), false);
      }
      latch.countDown();
    });
    awaitLatch(latch);

  }

  /**
   * Test: query_form_noexplode_array
   * Expected parameters sent:
   * color: color=blue,black,brown
   * Expected response: {"color":["blue","black","brown"]}
   * @throws Exception
   */
  @Test
  public void testQueryFormNoexplodeArray() throws Exception {
    routerFactory.addHandlerByOperationId("query_form_noexplode_array", routingContext -> {
      RequestParameters params = routingContext.get("parsedParameters");
      JsonObject res = new JsonObject();

      RequestParameter color_query = params.queryParameter("color");
      assertNotNull(color_query);
      assertTrue(color_query.isArray());
      res.put("color", new JsonArray(color_query.getArray().stream().map(param -> param.getString()).collect(Collectors.toList())));


      routingContext.response()
        .setStatusCode(200)
        .setStatusMessage("OK")
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(res.encode());
    });

    CountDownLatch latch = new CountDownLatch(1);

    List<Object> color_query;
    color_query = new ArrayList<>();
    color_query.add("blue");
    color_query.add("black");
    color_query.add("brown");


    startServer();

    apiClient.queryFormNoexplodeArray(color_query, (AsyncResult<HttpResponse> ar) -> {
      if (ar.succeeded()) {
        assertEquals(200, ar.result().statusCode());
        assertTrue("Expected: " + new JsonObject("{\"color\":[\"blue\",\"black\",\"brown\"]}").encode() + " Actual: " + ar.result().bodyAsJsonObject().encode(), new JsonObject("{\"color\":[\"blue\",\"black\",\"brown\"]}").equals(ar.result().bodyAsJsonObject()));
      } else {
        assertTrue(ar.cause().getMessage(), false);
      }
      latch.countDown();
    });
    awaitLatch(latch);

  }

  /**
   * Test: query_form_noexplode_object
   * Expected parameters sent:
   * color: color=R,100,G,200,B,150
   * Expected response: {"color":{"R":"100","G":"200","B":"150"}}
   * @throws Exception
   */
  @Test
  public void testQueryFormNoexplodeObject() throws Exception {
    routerFactory.addHandlerByOperationId("query_form_noexplode_object", routingContext -> {
      RequestParameters params = routingContext.get("parsedParameters");
      JsonObject res = new JsonObject();

      RequestParameter color_query = params.queryParameter("color");
      assertNotNull(color_query);
      assertTrue(color_query.isObject());
      Map<String, String> map = new HashMap<>();
      for (String key : color_query.getObjectKeys())
        map.put(key, color_query.getObjectValue(key).getString());
      res.put("color", map);
      

      routingContext.response()
        .setStatusCode(200)
        .setStatusMessage("OK")
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(res.encode());
    });

    CountDownLatch latch = new CountDownLatch(1);

    Map<String, Object> color_query;
    color_query = new HashMap<>();
    color_query.put("R", "100");
    color_query.put("G", "200");
    color_query.put("B", "150");


    startServer();

    apiClient.queryFormNoexplodeObject(color_query, (AsyncResult<HttpResponse> ar) -> {
      if (ar.succeeded()) {
        assertEquals(200, ar.result().statusCode());
        assertTrue("Expected: " + new JsonObject("{\"color\":{\"R\":\"100\",\"G\":\"200\",\"B\":\"150\"}}").encode() + " Actual: " + ar.result().bodyAsJsonObject().encode(), new JsonObject("{\"color\":{\"R\":\"100\",\"G\":\"200\",\"B\":\"150\"}}").equals(ar.result().bodyAsJsonObject()));
      } else {
        assertTrue(ar.cause().getMessage(), false);
      }
      latch.countDown();
    });
    awaitLatch(latch);

  }

  /**
   * Test: query_form_explode_empty
   * Expected parameters sent:
   * color: color=
   * Expected response: {"color":null}
   * @throws Exception
   */
  @Test
  public void testQueryFormExplodeEmpty() throws Exception {
    routerFactory.addHandlerByOperationId("query_form_explode_empty", routingContext -> {
      RequestParameters params = routingContext.get("parsedParameters");
      JsonObject res = new JsonObject();

      RequestParameter color_query = params.queryParameter("color");
      assertNotNull(color_query);
      assertTrue(color_query.isEmpty());
      res.putNull("color");


      routingContext.response()
        .setStatusCode(200)
        .setStatusMessage("OK")
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(res.encode());
    });

    CountDownLatch latch = new CountDownLatch(1);

    String color_query;
    color_query = "";


    startServer();

    apiClient.queryFormExplodeEmpty(color_query, (AsyncResult<HttpResponse> ar) -> {
      if (ar.succeeded()) {
        assertEquals(200, ar.result().statusCode());
        assertTrue("Expected: " + new JsonObject("{\"color\":null}").encode() + " Actual: " + ar.result().bodyAsJsonObject().encode(), new JsonObject("{\"color\":null}").equals(ar.result().bodyAsJsonObject()));
      } else {
        assertTrue(ar.cause().getMessage(), false);
      }
      latch.countDown();
    });
    awaitLatch(latch);

  }

  /**
   * Test: query_form_explode_string
   * Expected parameters sent:
   * color: color=blue
   * Expected response: {"color":"blue"}
   * @throws Exception
   */
  @Test
  public void testQueryFormExplodeString() throws Exception {
    routerFactory.addHandlerByOperationId("query_form_explode_string", routingContext -> {
      RequestParameters params = routingContext.get("parsedParameters");
      JsonObject res = new JsonObject();

      RequestParameter color_query = params.queryParameter("color");
      assertNotNull(color_query);
      assertTrue(color_query.isString());
      assertEquals(color_query.getString(), "blue");
      res.put("color", color_query.getString());


      routingContext.response()
        .setStatusCode(200)
        .setStatusMessage("OK")
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(res.encode());
    });

    CountDownLatch latch = new CountDownLatch(1);

    String color_query;
    color_query = "blue";


    startServer();

    apiClient.queryFormExplodeString(color_query, (AsyncResult<HttpResponse> ar) -> {
      if (ar.succeeded()) {
        assertEquals(200, ar.result().statusCode());
        assertTrue("Expected: " + new JsonObject("{\"color\":\"blue\"}").encode() + " Actual: " + ar.result().bodyAsJsonObject().encode(), new JsonObject("{\"color\":\"blue\"}").equals(ar.result().bodyAsJsonObject()));
      } else {
        assertTrue(ar.cause().getMessage(), false);
      }
      latch.countDown();
    });
    awaitLatch(latch);

  }

  /**
   * Test: query_form_explode_array
   * Expected parameters sent:
   * color: color=blue&color=black&color=brown
   * Expected response: {"color":["blue","black","brown"]}
   * @throws Exception
   */
  @Test
  public void testQueryFormExplodeArray() throws Exception {
    routerFactory.addHandlerByOperationId("query_form_explode_array", routingContext -> {
      RequestParameters params = routingContext.get("parsedParameters");
      JsonObject res = new JsonObject();

      RequestParameter color_query = params.queryParameter("color");
      assertNotNull(color_query);
      assertTrue(color_query.isArray());
      res.put("color", new JsonArray(color_query.getArray().stream().map(param -> param.getString()).collect(Collectors.toList())));


      routingContext.response()
        .setStatusCode(200)
        .setStatusMessage("OK")
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(res.encode());
    });

    CountDownLatch latch = new CountDownLatch(1);

    List<Object> color_query;
    color_query = new ArrayList<>();
    color_query.add("blue");
    color_query.add("black");
    color_query.add("brown");


    startServer();

    apiClient.queryFormExplodeArray(color_query, (AsyncResult<HttpResponse> ar) -> {
      if (ar.succeeded()) {
        assertEquals(200, ar.result().statusCode());
        assertTrue("Expected: " + new JsonObject("{\"color\":[\"blue\",\"black\",\"brown\"]}").encode() + " Actual: " + ar.result().bodyAsJsonObject().encode(), new JsonObject("{\"color\":[\"blue\",\"black\",\"brown\"]}").equals(ar.result().bodyAsJsonObject()));
      } else {
        assertTrue(ar.cause().getMessage(), false);
      }
      latch.countDown();
    });
    awaitLatch(latch);

  }

  /**
   * Test: query_form_explode_object
   * Expected parameters sent:
   * color: R=100&G=200&B=150
   * Expected response: {"color":{"R":"100","G":"200","B":"150"}}
   * @throws Exception
   */
  @Test
  public void testQueryFormExplodeObject() throws Exception {
    routerFactory.addHandlerByOperationId("query_form_explode_object", routingContext -> {
      RequestParameters params = routingContext.get("parsedParameters");
      JsonObject res = new JsonObject();

      RequestParameter color_query = params.queryParameter("color");
      assertNotNull(color_query);
      assertTrue(color_query.isObject());
      Map<String, String> map = new HashMap<>();
      for (String key : color_query.getObjectKeys())
        map.put(key, color_query.getObjectValue(key).getString());
      res.put("color", map);
      

      routingContext.response()
        .setStatusCode(200)
        .setStatusMessage("OK")
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(res.encode());
    });

    CountDownLatch latch = new CountDownLatch(1);

    Map<String, Object> color_query;
    color_query = new HashMap<>();
    color_query.put("R", "100");
    color_query.put("G", "200");
    color_query.put("B", "150");


    startServer();

    apiClient.queryFormExplodeObject(color_query, (AsyncResult<HttpResponse> ar) -> {
      if (ar.succeeded()) {
        assertEquals(200, ar.result().statusCode());
        assertTrue("Expected: " + new JsonObject("{\"color\":{\"R\":\"100\",\"G\":\"200\",\"B\":\"150\"}}").encode() + " Actual: " + ar.result().bodyAsJsonObject().encode(), new JsonObject("{\"color\":{\"R\":\"100\",\"G\":\"200\",\"B\":\"150\"}}").equals(ar.result().bodyAsJsonObject()));
      } else {
        assertTrue(ar.cause().getMessage(), false);
      }
      latch.countDown();
    });
    awaitLatch(latch);

  }

  /**
   * Test: query_spaceDelimited_noexplode_array
   * Expected parameters sent:
   * color: blue%20black%20brown
   * Expected response: {"color":["blue","black","brown"]}
   * @throws Exception
   */
  @Test
  public void testQuerySpaceDelimitedNoexplodeArray() throws Exception {
    routerFactory.addHandlerByOperationId("query_spaceDelimited_noexplode_array", routingContext -> {
      RequestParameters params = routingContext.get("parsedParameters");
      JsonObject res = new JsonObject();

      RequestParameter color_query = params.queryParameter("color");
      assertNotNull(color_query);
      assertTrue(color_query.isArray());
      res.put("color", new JsonArray(color_query.getArray().stream().map(param -> param.getString()).collect(Collectors.toList())));


      routingContext.response()
        .setStatusCode(200)
        .setStatusMessage("OK")
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(res.encode());
    });

    CountDownLatch latch = new CountDownLatch(1);

    List<Object> color_query;
    color_query = new ArrayList<>();
    color_query.add("blue");
    color_query.add("black");
    color_query.add("brown");


    startServer();

    apiClient.querySpaceDelimitedNoexplodeArray(color_query, (AsyncResult<HttpResponse> ar) -> {
      if (ar.succeeded()) {
        assertEquals(200, ar.result().statusCode());
        assertTrue("Expected: " + new JsonObject("{\"color\":[\"blue\",\"black\",\"brown\"]}").encode() + " Actual: " + ar.result().bodyAsJsonObject().encode(), new JsonObject("{\"color\":[\"blue\",\"black\",\"brown\"]}").equals(ar.result().bodyAsJsonObject()));
      } else {
        assertTrue(ar.cause().getMessage(), false);
      }
      latch.countDown();
    });
    awaitLatch(latch);

  }

  /**
   * Test: query_spaceDelimited_noexplode_object
   * Expected parameters sent:
   * color: R%20100%20G%20200%20B%20150
   * Expected response: {"color":{"R":"100","G":"200","B":"150"}}
   * @throws Exception
   */
  @Test
  public void testQuerySpaceDelimitedNoexplodeObject() throws Exception {
    routerFactory.addHandlerByOperationId("query_spaceDelimited_noexplode_object", routingContext -> {
      RequestParameters params = routingContext.get("parsedParameters");
      JsonObject res = new JsonObject();

      RequestParameter color_query = params.queryParameter("color");
      assertNotNull(color_query);
      assertTrue(color_query.isObject());
      Map<String, String> map = new HashMap<>();
      for (String key : color_query.getObjectKeys())
        map.put(key, color_query.getObjectValue(key).getString());
      res.put("color", map);
      

      routingContext.response()
        .setStatusCode(200)
        .setStatusMessage("OK")
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(res.encode());
    });

    CountDownLatch latch = new CountDownLatch(1);

    Map<String, Object> color_query;
    color_query = new HashMap<>();
    color_query.put("R", "100");
    color_query.put("G", "200");
    color_query.put("B", "150");


    startServer();

    apiClient.querySpaceDelimitedNoexplodeObject(color_query, (AsyncResult<HttpResponse> ar) -> {
      if (ar.succeeded()) {
        assertEquals(200, ar.result().statusCode());
        assertTrue("Expected: " + new JsonObject("{\"color\":{\"R\":\"100\",\"G\":\"200\",\"B\":\"150\"}}").encode() + " Actual: " + ar.result().bodyAsJsonObject().encode(), new JsonObject("{\"color\":{\"R\":\"100\",\"G\":\"200\",\"B\":\"150\"}}").equals(ar.result().bodyAsJsonObject()));
      } else {
        assertTrue(ar.cause().getMessage(), false);
      }
      latch.countDown();
    });
    awaitLatch(latch);

  }

  /**
   * Test: query_pipeDelimited_noexplode_array
   * Expected parameters sent:
   * color: blue|black|brown
   * Expected response: {"color":["blue","black","brown"]}
   * @throws Exception
   */
  @Test
  public void testQueryPipeDelimitedNoexplodeArray() throws Exception {
    routerFactory.addHandlerByOperationId("query_pipeDelimited_noexplode_array", routingContext -> {
      RequestParameters params = routingContext.get("parsedParameters");
      JsonObject res = new JsonObject();

      RequestParameter color_query = params.queryParameter("color");
      assertNotNull(color_query);
      assertTrue(color_query.isArray());
      res.put("color", new JsonArray(color_query.getArray().stream().map(param -> param.getString()).collect(Collectors.toList())));


      routingContext.response()
        .setStatusCode(200)
        .setStatusMessage("OK")
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(res.encode());
    });

    CountDownLatch latch = new CountDownLatch(1);

    List<Object> color_query;
    color_query = new ArrayList<>();
    color_query.add("blue");
    color_query.add("black");
    color_query.add("brown");


    startServer();

    apiClient.queryPipeDelimitedNoexplodeArray(color_query, (AsyncResult<HttpResponse> ar) -> {
      if (ar.succeeded()) {
        assertEquals(200, ar.result().statusCode());
        assertTrue("Expected: " + new JsonObject("{\"color\":[\"blue\",\"black\",\"brown\"]}").encode() + " Actual: " + ar.result().bodyAsJsonObject().encode(), new JsonObject("{\"color\":[\"blue\",\"black\",\"brown\"]}").equals(ar.result().bodyAsJsonObject()));
      } else {
        assertTrue(ar.cause().getMessage(), false);
      }
      latch.countDown();
    });
    awaitLatch(latch);

  }

  /**
   * Test: query_pipeDelimited_noexplode_object
   * Expected parameters sent:
   * color: R|100|G|200|B|150
   * Expected response: {"color":{"R":"100","G":"200","B":"150"}}
   * @throws Exception
   */
  @Test
  public void testQueryPipeDelimitedNoexplodeObject() throws Exception {
    routerFactory.addHandlerByOperationId("query_pipeDelimited_noexplode_object", routingContext -> {
      RequestParameters params = routingContext.get("parsedParameters");
      JsonObject res = new JsonObject();

      RequestParameter color_query = params.queryParameter("color");
      assertNotNull(color_query);
      assertTrue(color_query.isObject());
      Map<String, String> map = new HashMap<>();
      for (String key : color_query.getObjectKeys())
        map.put(key, color_query.getObjectValue(key).getString());
      res.put("color", map);
      

      routingContext.response()
        .setStatusCode(200)
        .setStatusMessage("OK")
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(res.encode());
    });

    CountDownLatch latch = new CountDownLatch(1);

    Map<String, Object> color_query;
    color_query = new HashMap<>();
    color_query.put("R", "100");
    color_query.put("G", "200");
    color_query.put("B", "150");


    startServer();

    apiClient.queryPipeDelimitedNoexplodeObject(color_query, (AsyncResult<HttpResponse> ar) -> {
      if (ar.succeeded()) {
        assertEquals(200, ar.result().statusCode());
        assertTrue("Expected: " + new JsonObject("{\"color\":{\"R\":\"100\",\"G\":\"200\",\"B\":\"150\"}}").encode() + " Actual: " + ar.result().bodyAsJsonObject().encode(), new JsonObject("{\"color\":{\"R\":\"100\",\"G\":\"200\",\"B\":\"150\"}}").equals(ar.result().bodyAsJsonObject()));
      } else {
        assertTrue(ar.cause().getMessage(), false);
      }
      latch.countDown();
    });
    awaitLatch(latch);

  }

  /**
   * Test: query_deepObject_explode_object
   * Expected parameters sent:
   * color: color[R]=100&color[G]=200&color[B]=150
   * Expected response: {"color":{"R":"100","G":"200","B":"150"}}
   * @throws Exception
   */
  @Test
  public void testQueryDeepObjectExplodeObject() throws Exception {
    routerFactory.addHandlerByOperationId("query_deepObject_explode_object", routingContext -> {
      RequestParameters params = routingContext.get("parsedParameters");
      JsonObject res = new JsonObject();

      RequestParameter color_query = params.queryParameter("color");
      assertNotNull(color_query);
      assertTrue(color_query.isObject());
      Map<String, String> map = new HashMap<>();
      for (String key : color_query.getObjectKeys())
        map.put(key, color_query.getObjectValue(key).getString());
      res.put("color", map);
      

      routingContext.response()
        .setStatusCode(200)
        .setStatusMessage("OK")
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(res.encode());
    });

    CountDownLatch latch = new CountDownLatch(1);

    Map<String, Object> color_query;
    color_query = new HashMap<>();
    color_query.put("R", "100");
    color_query.put("G", "200");
    color_query.put("B", "150");


    startServer();

    apiClient.queryDeepObjectExplodeObject(color_query, (AsyncResult<HttpResponse> ar) -> {
      if (ar.succeeded()) {
        assertEquals(200, ar.result().statusCode());
        assertTrue("Expected: " + new JsonObject("{\"color\":{\"R\":\"100\",\"G\":\"200\",\"B\":\"150\"}}").encode() + " Actual: " + ar.result().bodyAsJsonObject().encode(), new JsonObject("{\"color\":{\"R\":\"100\",\"G\":\"200\",\"B\":\"150\"}}").equals(ar.result().bodyAsJsonObject()));
      } else {
        assertTrue(ar.cause().getMessage(), false);
      }
      latch.countDown();
    });
    awaitLatch(latch);

  }

  /**
   * Test: cookie_form_noexplode_empty
   * Expected parameters sent:
   * color: color=
   * Expected response: {"color":null}
   * @throws Exception
   */
  @Test
  public void testCookieFormNoexplodeEmpty() throws Exception {
    routerFactory.addHandlerByOperationId("cookie_form_noexplode_empty", routingContext -> {
      RequestParameters params = routingContext.get("parsedParameters");
      JsonObject res = new JsonObject();

      RequestParameter color_cookie = params.cookieParameter("color");
      assertNotNull(color_cookie);
      assertTrue(color_cookie.isEmpty());
      res.putNull("color");


      routingContext.response()
        .setStatusCode(200)
        .setStatusMessage("OK")
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(res.encode());
    });

    CountDownLatch latch = new CountDownLatch(1);

    String color_cookie;
    color_cookie = "";


    startServer();

    apiClient.cookieFormNoexplodeEmpty(color_cookie, (AsyncResult<HttpResponse> ar) -> {
      if (ar.succeeded()) {
        assertEquals(200, ar.result().statusCode());
        assertTrue("Expected: " + new JsonObject("{\"color\":null}").encode() + " Actual: " + ar.result().bodyAsJsonObject().encode(), new JsonObject("{\"color\":null}").equals(ar.result().bodyAsJsonObject()));
      } else {
        assertTrue(ar.cause().getMessage(), false);
      }
      latch.countDown();
    });
    awaitLatch(latch);

  }

  /**
   * Test: cookie_form_noexplode_string
   * Expected parameters sent:
   * color: color=blue
   * Expected response: {"color":"blue"}
   * @throws Exception
   */
  @Test
  public void testCookieFormNoexplodeString() throws Exception {
    routerFactory.addHandlerByOperationId("cookie_form_noexplode_string", routingContext -> {
      RequestParameters params = routingContext.get("parsedParameters");
      JsonObject res = new JsonObject();

      RequestParameter color_cookie = params.cookieParameter("color");
      assertNotNull(color_cookie);
      assertTrue(color_cookie.isString());
      assertEquals(color_cookie.getString(), "blue");
      res.put("color", color_cookie.getString());


      routingContext.response()
        .setStatusCode(200)
        .setStatusMessage("OK")
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(res.encode());
    });

    CountDownLatch latch = new CountDownLatch(1);

    String color_cookie;
    color_cookie = "blue";


    startServer();

    apiClient.cookieFormNoexplodeString(color_cookie, (AsyncResult<HttpResponse> ar) -> {
      if (ar.succeeded()) {
        assertEquals(200, ar.result().statusCode());
        assertTrue("Expected: " + new JsonObject("{\"color\":\"blue\"}").encode() + " Actual: " + ar.result().bodyAsJsonObject().encode(), new JsonObject("{\"color\":\"blue\"}").equals(ar.result().bodyAsJsonObject()));
      } else {
        assertTrue(ar.cause().getMessage(), false);
      }
      latch.countDown();
    });
    awaitLatch(latch);

  }

  /**
   * Test: cookie_form_noexplode_array
   * Expected parameters sent:
   * color: color=blue,black,brown
   * Expected response: {"color":["blue","black","brown"]}
   * @throws Exception
   */
  @Test
  public void testCookieFormNoexplodeArray() throws Exception {
    routerFactory.addHandlerByOperationId("cookie_form_noexplode_array", routingContext -> {
      RequestParameters params = routingContext.get("parsedParameters");
      JsonObject res = new JsonObject();

      RequestParameter color_cookie = params.cookieParameter("color");
      assertNotNull(color_cookie);
      assertTrue(color_cookie.isArray());
      res.put("color", new JsonArray(color_cookie.getArray().stream().map(param -> param.getString()).collect(Collectors.toList())));


      routingContext.response()
        .setStatusCode(200)
        .setStatusMessage("OK")
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(res.encode());
    });

    CountDownLatch latch = new CountDownLatch(1);

    List<Object> color_cookie;
    color_cookie = new ArrayList<>();
    color_cookie.add("blue");
    color_cookie.add("black");
    color_cookie.add("brown");


    startServer();

    apiClient.cookieFormNoexplodeArray(color_cookie, (AsyncResult<HttpResponse> ar) -> {
      if (ar.succeeded()) {
        assertEquals(200, ar.result().statusCode());
        assertTrue("Expected: " + new JsonObject("{\"color\":[\"blue\",\"black\",\"brown\"]}").encode() + " Actual: " + ar.result().bodyAsJsonObject().encode(), new JsonObject("{\"color\":[\"blue\",\"black\",\"brown\"]}").equals(ar.result().bodyAsJsonObject()));
      } else {
        assertTrue(ar.cause().getMessage(), false);
      }
      latch.countDown();
    });
    awaitLatch(latch);

  }

  /**
   * Test: cookie_form_noexplode_object
   * Expected parameters sent:
   * color: color=R,100,G,200,B,150
   * Expected response: {"color":{"R":"100","G":"200","B":"150"}}
   * @throws Exception
   */
  @Test
  public void testCookieFormNoexplodeObject() throws Exception {
    routerFactory.addHandlerByOperationId("cookie_form_noexplode_object", routingContext -> {
      RequestParameters params = routingContext.get("parsedParameters");
      JsonObject res = new JsonObject();

      RequestParameter color_cookie = params.cookieParameter("color");
      assertNotNull(color_cookie);
      assertTrue(color_cookie.isObject());
      Map<String, String> map = new HashMap<>();
      for (String key : color_cookie.getObjectKeys())
        map.put(key, color_cookie.getObjectValue(key).getString());
      res.put("color", map);
      

      routingContext.response()
        .setStatusCode(200)
        .setStatusMessage("OK")
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(res.encode());
    });

    CountDownLatch latch = new CountDownLatch(1);

    Map<String, Object> color_cookie;
    color_cookie = new HashMap<>();
    color_cookie.put("R", "100");
    color_cookie.put("G", "200");
    color_cookie.put("B", "150");


    startServer();

    apiClient.cookieFormNoexplodeObject(color_cookie, (AsyncResult<HttpResponse> ar) -> {
      if (ar.succeeded()) {
        assertEquals(200, ar.result().statusCode());
        assertTrue("Expected: " + new JsonObject("{\"color\":{\"R\":\"100\",\"G\":\"200\",\"B\":\"150\"}}").encode() + " Actual: " + ar.result().bodyAsJsonObject().encode(), new JsonObject("{\"color\":{\"R\":\"100\",\"G\":\"200\",\"B\":\"150\"}}").equals(ar.result().bodyAsJsonObject()));
      } else {
        assertTrue(ar.cause().getMessage(), false);
      }
      latch.countDown();
    });
    awaitLatch(latch);

  }

  /**
   * Test: cookie_form_explode_empty
   * Expected parameters sent:
   * color: color=
   * Expected response: {"color":null}
   * @throws Exception
   */
  @Test
  public void testCookieFormExplodeEmpty() throws Exception {
    routerFactory.addHandlerByOperationId("cookie_form_explode_empty", routingContext -> {
      RequestParameters params = routingContext.get("parsedParameters");
      JsonObject res = new JsonObject();

      RequestParameter color_cookie = params.cookieParameter("color");
      assertNotNull(color_cookie);
      assertTrue(color_cookie.isEmpty());
      res.putNull("color");


      routingContext.response()
        .setStatusCode(200)
        .setStatusMessage("OK")
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(res.encode());
    });

    CountDownLatch latch = new CountDownLatch(1);

    String color_cookie;
    color_cookie = "";


    startServer();

    apiClient.cookieFormExplodeEmpty(color_cookie, (AsyncResult<HttpResponse> ar) -> {
      if (ar.succeeded()) {
        assertEquals(200, ar.result().statusCode());
        assertTrue("Expected: " + new JsonObject("{\"color\":null}").encode() + " Actual: " + ar.result().bodyAsJsonObject().encode(), new JsonObject("{\"color\":null}").equals(ar.result().bodyAsJsonObject()));
      } else {
        assertTrue(ar.cause().getMessage(), false);
      }
      latch.countDown();
    });
    awaitLatch(latch);

  }

  /**
   * Test: cookie_form_explode_string
   * Expected parameters sent:
   * color: color=blue
   * Expected response: {"color":"blue"}
   * @throws Exception
   */
  @Test
  public void testCookieFormExplodeString() throws Exception {
    routerFactory.addHandlerByOperationId("cookie_form_explode_string", routingContext -> {
      RequestParameters params = routingContext.get("parsedParameters");
      JsonObject res = new JsonObject();

      RequestParameter color_cookie = params.cookieParameter("color");
      assertNotNull(color_cookie);
      assertTrue(color_cookie.isString());
      assertEquals(color_cookie.getString(), "blue");
      res.put("color", color_cookie.getString());


      routingContext.response()
        .setStatusCode(200)
        .setStatusMessage("OK")
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(res.encode());
    });

    CountDownLatch latch = new CountDownLatch(1);

    String color_cookie;
    color_cookie = "blue";


    startServer();

    apiClient.cookieFormExplodeString(color_cookie, (AsyncResult<HttpResponse> ar) -> {
      if (ar.succeeded()) {
        assertEquals(200, ar.result().statusCode());
        assertTrue("Expected: " + new JsonObject("{\"color\":\"blue\"}").encode() + " Actual: " + ar.result().bodyAsJsonObject().encode(), new JsonObject("{\"color\":\"blue\"}").equals(ar.result().bodyAsJsonObject()));
      } else {
        assertTrue(ar.cause().getMessage(), false);
      }
      latch.countDown();
    });
    awaitLatch(latch);

  }

  /**
   * Test: cookie_form_explode_array
   * Expected parameters sent:
   * color: color=blue&color=black&color=brown
   * Expected response: {"color":["blue","black","brown"]}
   * @throws Exception
   */
  @Test
  public void testCookieFormExplodeArray() throws Exception {
    routerFactory.addHandlerByOperationId("cookie_form_explode_array", routingContext -> {
      RequestParameters params = routingContext.get("parsedParameters");
      JsonObject res = new JsonObject();

      RequestParameter color_cookie = params.cookieParameter("color");
      assertNotNull(color_cookie);
      assertTrue(color_cookie.isArray());
      res.put("color", new JsonArray(color_cookie.getArray().stream().map(param -> param.getString()).collect(Collectors.toList())));


      routingContext.response()
        .setStatusCode(200)
        .setStatusMessage("OK")
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(res.encode());
    });

    CountDownLatch latch = new CountDownLatch(1);

    List<Object> color_cookie;
    color_cookie = new ArrayList<>();
    color_cookie.add("blue");
    color_cookie.add("black");
    color_cookie.add("brown");


    startServer();

    apiClient.cookieFormExplodeArray(color_cookie, (AsyncResult<HttpResponse> ar) -> {
      if (ar.succeeded()) {
        assertEquals(200, ar.result().statusCode());
        assertTrue("Expected: " + new JsonObject("{\"color\":[\"blue\",\"black\",\"brown\"]}").encode() + " Actual: " + ar.result().bodyAsJsonObject().encode(), new JsonObject("{\"color\":[\"blue\",\"black\",\"brown\"]}").equals(ar.result().bodyAsJsonObject()));
      } else {
        assertTrue(ar.cause().getMessage(), false);
      }
      latch.countDown();
    });
    awaitLatch(latch);

  }

  /**
   * Test: cookie_form_explode_object
   * Expected parameters sent:
   * color: R=100&G=200&B=150
   * Expected response: {"color":{"R":"100","G":"200","B":"150"}}
   * @throws Exception
   */
  @Test
  public void testCookieFormExplodeObject() throws Exception {
    routerFactory.addHandlerByOperationId("cookie_form_explode_object", routingContext -> {
      RequestParameters params = routingContext.get("parsedParameters");
      JsonObject res = new JsonObject();

      RequestParameter color_cookie = params.cookieParameter("color");
      assertNotNull(color_cookie);
      assertTrue(color_cookie.isObject());
      Map<String, String> map = new HashMap<>();
      for (String key : color_cookie.getObjectKeys())
        map.put(key, color_cookie.getObjectValue(key).getString());
      res.put("color", map);
      

      routingContext.response()
        .setStatusCode(200)
        .setStatusMessage("OK")
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(res.encode());
    });

    CountDownLatch latch = new CountDownLatch(1);

    Map<String, Object> color_cookie;
    color_cookie = new HashMap<>();
    color_cookie.put("R", "100");
    color_cookie.put("G", "200");
    color_cookie.put("B", "150");


    startServer();

    apiClient.cookieFormExplodeObject(color_cookie, (AsyncResult<HttpResponse> ar) -> {
      if (ar.succeeded()) {
        assertEquals(200, ar.result().statusCode());
        assertTrue("Expected: " + new JsonObject("{\"color\":{\"R\":\"100\",\"G\":\"200\",\"B\":\"150\"}}").encode() + " Actual: " + ar.result().bodyAsJsonObject().encode(), new JsonObject("{\"color\":{\"R\":\"100\",\"G\":\"200\",\"B\":\"150\"}}").equals(ar.result().bodyAsJsonObject()));
      } else {
        assertTrue(ar.cause().getMessage(), false);
      }
      latch.countDown();
    });
    awaitLatch(latch);

  }

  /**
   * Test: header_simple_noexplode_string
   * Expected parameters sent:
   * color: blue
   * Expected response: {"color":"blue"}
   * @throws Exception
   */
  @Test
  public void testHeaderSimpleNoexplodeString() throws Exception {
    routerFactory.addHandlerByOperationId("header_simple_noexplode_string", routingContext -> {
      RequestParameters params = routingContext.get("parsedParameters");
      JsonObject res = new JsonObject();

      RequestParameter color_header = params.headerParameter("color");
      assertNotNull(color_header);
      assertTrue(color_header.isString());
      assertEquals(color_header.getString(), "blue");
      res.put("color", color_header.getString());


      routingContext.response()
        .setStatusCode(200)
        .setStatusMessage("OK")
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(res.encode());
    });

    CountDownLatch latch = new CountDownLatch(1);

    String color_header;
    color_header = "blue";


    startServer();

    apiClient.headerSimpleNoexplodeString(color_header, (AsyncResult<HttpResponse> ar) -> {
      if (ar.succeeded()) {
        assertEquals(200, ar.result().statusCode());
        assertTrue("Expected: " + new JsonObject("{\"color\":\"blue\"}").encode() + " Actual: " + ar.result().bodyAsJsonObject().encode(), new JsonObject("{\"color\":\"blue\"}").equals(ar.result().bodyAsJsonObject()));
      } else {
        assertTrue(ar.cause().getMessage(), false);
      }
      latch.countDown();
    });
    awaitLatch(latch);

  }

  /**
   * Test: header_simple_noexplode_array
   * Expected parameters sent:
   * color: blue,black,brown
   * Expected response: {"color":["blue","black","brown"]}
   * @throws Exception
   */
  @Test
  public void testHeaderSimpleNoexplodeArray() throws Exception {
    routerFactory.addHandlerByOperationId("header_simple_noexplode_array", routingContext -> {
      RequestParameters params = routingContext.get("parsedParameters");
      JsonObject res = new JsonObject();

      RequestParameter color_header = params.headerParameter("color");
      assertNotNull(color_header);
      assertTrue(color_header.isArray());
      res.put("color", new JsonArray(color_header.getArray().stream().map(param -> param.getString()).collect(Collectors.toList())));


      routingContext.response()
        .setStatusCode(200)
        .setStatusMessage("OK")
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(res.encode());
    });

    CountDownLatch latch = new CountDownLatch(1);

    List<Object> color_header;
    color_header = new ArrayList<>();
    color_header.add("blue");
    color_header.add("black");
    color_header.add("brown");


    startServer();

    apiClient.headerSimpleNoexplodeArray(color_header, (AsyncResult<HttpResponse> ar) -> {
      if (ar.succeeded()) {
        assertEquals(200, ar.result().statusCode());
        assertTrue("Expected: " + new JsonObject("{\"color\":[\"blue\",\"black\",\"brown\"]}").encode() + " Actual: " + ar.result().bodyAsJsonObject().encode(), new JsonObject("{\"color\":[\"blue\",\"black\",\"brown\"]}").equals(ar.result().bodyAsJsonObject()));
      } else {
        assertTrue(ar.cause().getMessage(), false);
      }
      latch.countDown();
    });
    awaitLatch(latch);

  }

  /**
   * Test: header_simple_noexplode_object
   * Expected parameters sent:
   * color: R,100,G,200,B,150
   * Expected response: {"color":{"R":"100","G":"200","B":"150"}}
   * @throws Exception
   */
  @Test
  public void testHeaderSimpleNoexplodeObject() throws Exception {
    routerFactory.addHandlerByOperationId("header_simple_noexplode_object", routingContext -> {
      RequestParameters params = routingContext.get("parsedParameters");
      JsonObject res = new JsonObject();

      RequestParameter color_header = params.headerParameter("color");
      assertNotNull(color_header);
      assertTrue(color_header.isObject());
      Map<String, String> map = new HashMap<>();
      for (String key : color_header.getObjectKeys())
        map.put(key, color_header.getObjectValue(key).getString());
      res.put("color", map);
      

      routingContext.response()
        .setStatusCode(200)
        .setStatusMessage("OK")
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(res.encode());
    });

    CountDownLatch latch = new CountDownLatch(1);

    Map<String, Object> color_header;
    color_header = new HashMap<>();
    color_header.put("R", "100");
    color_header.put("G", "200");
    color_header.put("B", "150");


    startServer();

    apiClient.headerSimpleNoexplodeObject(color_header, (AsyncResult<HttpResponse> ar) -> {
      if (ar.succeeded()) {
        assertEquals(200, ar.result().statusCode());
        assertTrue("Expected: " + new JsonObject("{\"color\":{\"R\":\"100\",\"G\":\"200\",\"B\":\"150\"}}").encode() + " Actual: " + ar.result().bodyAsJsonObject().encode(), new JsonObject("{\"color\":{\"R\":\"100\",\"G\":\"200\",\"B\":\"150\"}}").equals(ar.result().bodyAsJsonObject()));
      } else {
        assertTrue(ar.cause().getMessage(), false);
      }
      latch.countDown();
    });
    awaitLatch(latch);

  }

  /**
   * Test: header_simple_explode_string
   * Expected parameters sent:
   * color: blue
   * Expected response: {"color":"blue"}
   * @throws Exception
   */
  @Test
  public void testHeaderSimpleExplodeString() throws Exception {
    routerFactory.addHandlerByOperationId("header_simple_explode_string", routingContext -> {
      RequestParameters params = routingContext.get("parsedParameters");
      JsonObject res = new JsonObject();

      RequestParameter color_header = params.headerParameter("color");
      assertNotNull(color_header);
      assertTrue(color_header.isString());
      assertEquals(color_header.getString(), "blue");
      res.put("color", color_header.getString());


      routingContext.response()
        .setStatusCode(200)
        .setStatusMessage("OK")
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(res.encode());
    });

    CountDownLatch latch = new CountDownLatch(1);

    String color_header;
    color_header = "blue";


    startServer();

    apiClient.headerSimpleExplodeString(color_header, (AsyncResult<HttpResponse> ar) -> {
      if (ar.succeeded()) {
        assertEquals(200, ar.result().statusCode());
        assertTrue("Expected: " + new JsonObject("{\"color\":\"blue\"}").encode() + " Actual: " + ar.result().bodyAsJsonObject().encode(), new JsonObject("{\"color\":\"blue\"}").equals(ar.result().bodyAsJsonObject()));
      } else {
        assertTrue(ar.cause().getMessage(), false);
      }
      latch.countDown();
    });
    awaitLatch(latch);

  }

  /**
   * Test: header_simple_explode_array
   * Expected parameters sent:
   * color: blue,black,brown
   * Expected response: {"color":["blue","black","brown"]}
   * @throws Exception
   */
  @Test
  public void testHeaderSimpleExplodeArray() throws Exception {
    routerFactory.addHandlerByOperationId("header_simple_explode_array", routingContext -> {
      RequestParameters params = routingContext.get("parsedParameters");
      JsonObject res = new JsonObject();

      RequestParameter color_header = params.headerParameter("color");
      assertNotNull(color_header);
      assertTrue(color_header.isArray());
      res.put("color", new JsonArray(color_header.getArray().stream().map(param -> param.getString()).collect(Collectors.toList())));


      routingContext.response()
        .setStatusCode(200)
        .setStatusMessage("OK")
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(res.encode());
    });

    CountDownLatch latch = new CountDownLatch(1);

    List<Object> color_header;
    color_header = new ArrayList<>();
    color_header.add("blue");
    color_header.add("black");
    color_header.add("brown");


    startServer();

    apiClient.headerSimpleExplodeArray(color_header, (AsyncResult<HttpResponse> ar) -> {
      if (ar.succeeded()) {
        assertEquals(200, ar.result().statusCode());
        assertTrue("Expected: " + new JsonObject("{\"color\":[\"blue\",\"black\",\"brown\"]}").encode() + " Actual: " + ar.result().bodyAsJsonObject().encode(), new JsonObject("{\"color\":[\"blue\",\"black\",\"brown\"]}").equals(ar.result().bodyAsJsonObject()));
      } else {
        assertTrue(ar.cause().getMessage(), false);
      }
      latch.countDown();
    });
    awaitLatch(latch);

  }

  /**
   * Test: header_simple_explode_object
   * Expected parameters sent:
   * color: R=100,G=200,B=150
   * Expected response: {"color":{"R":"100","G":"200","B":"150"}}
   * @throws Exception
   */
  @Test
  public void testHeaderSimpleExplodeObject() throws Exception {
    routerFactory.addHandlerByOperationId("header_simple_explode_object", routingContext -> {
      RequestParameters params = routingContext.get("parsedParameters");
      JsonObject res = new JsonObject();

      RequestParameter color_header = params.headerParameter("color");
      assertNotNull(color_header);
      assertTrue(color_header.isObject());
      Map<String, String> map = new HashMap<>();
      for (String key : color_header.getObjectKeys())
        map.put(key, color_header.getObjectValue(key).getString());
      res.put("color", map);
      

      routingContext.response()
        .setStatusCode(200)
        .setStatusMessage("OK")
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(res.encode());
    });

    CountDownLatch latch = new CountDownLatch(1);

    Map<String, Object> color_header;
    color_header = new HashMap<>();
    color_header.put("R", "100");
    color_header.put("G", "200");
    color_header.put("B", "150");


    startServer();

    apiClient.headerSimpleExplodeObject(color_header, (AsyncResult<HttpResponse> ar) -> {
      if (ar.succeeded()) {
        assertEquals(200, ar.result().statusCode());
        assertTrue("Expected: " + new JsonObject("{\"color\":{\"R\":\"100\",\"G\":\"200\",\"B\":\"150\"}}").encode() + " Actual: " + ar.result().bodyAsJsonObject().encode(), new JsonObject("{\"color\":{\"R\":\"100\",\"G\":\"200\",\"B\":\"150\"}}").equals(ar.result().bodyAsJsonObject()));
      } else {
        assertTrue(ar.cause().getMessage(), false);
      }
      latch.countDown();
    });
    awaitLatch(latch);

  }


  private OpenAPI loadSwagger(String filename) {
    ParseOptions options = new ParseOptions();
    options.setResolve(true);
    return new OpenAPIV3Parser().read(filename, null, options);
  }

  public Handler<RoutingContext> generateFailureHandler() {
    return routingContext -> {
      Throwable failure = routingContext.failure();
      failure.printStackTrace();
      assertTrue(failure.getMessage(), false);
    };
  }

  private void startServer() throws Exception {
    router = routerFactory.getRouter();
    server = this.vertx.createHttpServer(new HttpServerOptions().setPort(8080).setHost("localhost"));
    CountDownLatch latch = new CountDownLatch(1);
    server.requestHandler(router::accept).listen(onSuccess(res -> {
      latch.countDown();
    }));
    awaitLatch(latch);
  }

  private void stopServer() throws Exception {
    if (server != null) {
      CountDownLatch latch = new CountDownLatch(1);
      try {
        server.close((asyncResult) -> {
          latch.countDown();
        });
      } catch (IllegalStateException e) { // Server is already open
        latch.countDown();
      }
      awaitLatch(latch);
    }
  }
}