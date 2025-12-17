package co.com.ficohsa.adapter;

import co.com.ficohsa.domain.ports.out.CryptoPriceGateway;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Adaptador para API Ninjas que implementa CryptoPriceGateway
 * El logging se hace automáticamente por el WebClient customizado
 */
@Component
public class CryptoApiAdapter implements CryptoPriceGateway {
    private final WebClient webClient;

    public CryptoApiAdapter(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
            .baseUrl("https://api.api-ninjas.com/v1")
            .defaultHeader("X-Api-Key", "gQUmlderYD2Mz9BiX+0Hdg==pvmSDmPKKdBfSTvv")
            .build();
    }

    @Override
    public Mono<CryptoPriceResponse> getCryptoPrice(String symbol) {
        return webClient.get()
            .uri("/cryptoprice?symbol={symbol}", symbol)
            .retrieve()
            .bodyToMono(ApiNinjasResponse.class)
            .map(apiResponse -> new CryptoPriceResponse(
                apiResponse.symbol(),
                apiResponse.price(),
                apiResponse.timestamp()
            ));
    }

    /**
     * DTO interno para mapear la respuesta de API Ninjas
     */
    private record ApiNinjasResponse(
        String symbol,
        Double price,
        Long timestamp
    ) {}
}
