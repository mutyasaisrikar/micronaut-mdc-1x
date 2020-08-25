package com.srikar.test;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.srikar.test.MyControllerTest.DEFAULT_PORT;
import static com.srikar.test.MyControllerTest.SERVER_PORT;
import static io.micronaut.http.HttpRequest.GET;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URL;
import javax.inject.Inject;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.srikar.test.interfaces.MyController;
import io.micronaut.context.annotation.Property;
import io.micronaut.http.client.DefaultHttpClient;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.annotation.MicronautTest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@Slf4j
@MicronautTest
@Property(name = "micronaut.server.port", value = SERVER_PORT + "")
@Property(name = "micronaut.http.services.clienttest.urls", value = "http://localhost:" + DEFAULT_PORT + "/")
public class MyControllerTest {


    public static final int SERVER_PORT = 30623;
    public static final int DEFAULT_PORT = 60106;
    public static WireMockServer wireMockServer = new WireMockServer(DEFAULT_PORT);

    @Inject
    @Client("/")
    RxHttpClient client;

    @BeforeAll
    public static void beforeAll() {
        wireMockServer.start();
        wireMockServer.stubFor(
            get(urlPathEqualTo("/test"))
                .willReturn(aResponse().withBody("Hello World"))
        );
    }

    @AfterAll
    public static void afterAll() {
        wireMockServer.stop();
    }

    @Test
    @SneakyThrows
    public void hello() {

        URL url = new URL("http://localhost:" + SERVER_PORT + "/");

        var result = new DefaultHttpClient(url).toBlocking().retrieve(GET("hello"), MyController.Response.class);

        log.info("Result is {}", result.getMessage());
        assertEquals("Hello World", result.getMessage());
    }
}
