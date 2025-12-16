package co.com.ficohsa.usecase;

import co.com.ficohsa.adapter.CryptoApiAdapter;
import co.com.ficohsa.adapter.CryptoPriceResponse;
import co.com.ficohsa.logging.clients.annotation.LogExternalCall;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class GetCryptoPriceUseCase {
    private final CryptoApiAdapter cryptoApiAdapter;

    @LogExternalCall(provider = "ApiNinjas")
    public Mono<CryptoPriceResponse> execute(String symbol) {
        return cryptoApiAdapter.getCryptoPrice(symbol);
    }
}
