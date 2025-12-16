package co.com.ficohsa.adapter;

import co.com.ficohsa.logging.clients.annotation.LogExternalCall;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class CryptoApiAdapter {
    private final WebClient webClient;

    public CryptoApiAdapter(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
            .baseUrl("https://api.api-ninjas.com/v1")
            .defaultHeader("X-Api-Key", "gQUmlderYD2Mz9BiX+0Hdg==pvmSDmPKKdBfSTvv")
            .build();
    }

    public Mono<CryptoPriceResponse> getCryptoPrice(String symbol) {
        return webClient.get()
            .uri("/cryptoprice?symbol={symbol}", symbol)
            .retrieve()
            .bodyToMono(CryptoPriceResponse.class);
    }
}
