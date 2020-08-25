package com.srikar.test.logging;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.http.filter.ClientFilterChain;
import io.micronaut.http.filter.HttpClientFilter;
import io.reactivex.Flowable;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Filter("/**")
public class HttpClientExchangeLogFilter implements HttpClientFilter {

    private static final Logger HTTP_CLIENT_LOG = LoggerFactory.getLogger("httpclient");

    @Override
    public Publisher<? extends HttpResponse<?>> doFilter(MutableHttpRequest<?> request, ClientFilterChain chain) {

        return Flowable.fromPublisher(chain.proceed(request))
                .doOnNext(response -> {
                    logExchange(HTTP_CLIENT_LOG, request);
                })
                .doOnError(error -> {
                    if(error instanceof HttpClientResponseException) {
                        logExchange(HTTP_CLIENT_LOG, request);
                    } else {
                        logExchange(HTTP_CLIENT_LOG, request, error);
                    }
                });
    }

    public void logExchange(Logger logger, HttpRequest<?> request) {
        logger.info("Request: endpoint {}, method {}",
                request.getUri(),
                request.getMethodName());
    }

    public void logExchange(Logger logger, HttpRequest<?> request, Throwable throwable) {
        logger.info("Request: endpoint {}, method {}",
                request.getUri(),
                request.getMethodName(),
                throwable
        );
    }
}
