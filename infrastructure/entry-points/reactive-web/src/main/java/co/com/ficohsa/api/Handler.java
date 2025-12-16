package co.com.ficohsa.api;

import co.com.ficohsa.api.dto.ExampleRequest;
import co.com.ficohsa.api.dto.ExampleResponse;
import co.com.ficohsa.logging.infrastructure.context.LogContextHelper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class Handler {
    private static final Logger log = LoggerFactory.getLogger(Handler.class);
    private final co.com.ficohsa.usecase.GetCryptoPriceUseCase getCryptoPriceUseCase;

    public Mono<ServerResponse> listenGETUseCase(ServerRequest serverRequest) {
        log.info("Processing GET request");
        // useCase.logic();
        return ServerResponse.ok().bodyValue("");
    }

    public Mono<ServerResponse> listenGETOtherUseCase(ServerRequest serverRequest) {
        log.info("Processing other GET request");
        // useCase2.logic();
        return ServerResponse.ok().bodyValue("");
    }

    public Mono<ServerResponse> listenPOSTUseCase(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(ExampleRequest.class)
            .doOnNext(request -> {
                LogContextHelper.addData(request);
                log.info("Processing POST request");
            })
            .map(request -> {
                // useCase.logic();
                return new ExampleResponse(
                    UUID.randomUUID().toString(),
                    "SUCCESS",
                    "Transaction processed successfully"
                );
            })
            .flatMap(response -> ServerResponse.ok().bodyValue(response));
    }

    public Mono<ServerResponse> getCryptoPrice(ServerRequest serverRequest) {
        String symbol = serverRequest.queryParam("symbol").orElse("BTCUSDT");
        return getCryptoPriceUseCase.execute(symbol)
            .flatMap(response -> ServerResponse.ok().bodyValue(response));
    }

    public Mono<ServerResponse> testError(ServerRequest serverRequest) {
        log.info("Testing error endpoint");
        return Mono.error(new RuntimeException("This is a test error"));
    }
}
