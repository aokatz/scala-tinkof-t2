package org.tinkoff.task.two.service;

import org.tinkoff.task.two.exception.ServiceException;
import org.tinkoff.task.two.model.Address;
import org.tinkoff.task.two.model.Event;
import org.tinkoff.task.two.model.Payload;
import org.tinkoff.task.two.model.Result;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class HandlerImpl implements Handler {

    private static final long RETRY_TIMEOUT_SECONDS = 3L;
    private static final int RETRY_MAX_ATTEMPTS = 3;
    private final Client client;

    public HandlerImpl(Client client) {
        this.client = client;
    }

    @Override
    public Duration timeout() {
        return Duration.ofSeconds(RETRY_TIMEOUT_SECONDS);
    }

    @Override
    public void performOperation() {
        Event responseEvent = client.readData();
        List<Address> addresses = Optional.ofNullable(responseEvent.recipients()).orElseGet(Collections::emptyList);
        Flux.fromIterable(addresses)
                .flatMap(destinationAddress -> sendData(destinationAddress, responseEvent.payload()))
                .flatMap(this::validateResponse)
                .retryWhen(Retry.fixedDelay(RETRY_MAX_ATTEMPTS, timeout())
                        .filter(throwable -> throwable instanceof ServiceException))
                .subscribeOn(Schedulers.boundedElastic())
                .blockLast();
    }

    private Mono<Result> validateResponse(Result result) {
        if (Result.REJECTED.equals(result)) {
            return Mono.error(new ServiceException("Request REJECTED"));
        }
        return Mono.just(result);
    }

    private Mono<Result> sendData(Address address, Payload payload) {
        return Mono.just(client.sendData(address, payload));
    }
}
