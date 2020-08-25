package com.srikar.test.interfaces;

import static java.util.UUID.randomUUID;

import java.time.Instant;

import javax.inject.Inject;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

@Slf4j
@Controller
public class MyController {

    @Inject
    @Client("clienttest")
    private RxHttpClient client;

    @Get("/hello")
    public Response hello() {

        MDC.put("correlationId", randomUUID().toString());
        log.info("Request came at {}", Instant.now().toEpochMilli());

        Response response = new Response();
        response.setMessage(httpCall());

        log.info("Request completed at {}", Instant.now().toEpochMilli());
        return response;
    }

    private String httpCall() {
        try {
            log.info("Making a dummy call");
            return client.exchange("test").blockingFirst().getBody(String.class).get();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static class Response {
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}