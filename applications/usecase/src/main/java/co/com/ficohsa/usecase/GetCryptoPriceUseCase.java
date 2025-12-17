package co.com.ficohsa.usecase;

import co.com.ficohsa.domain.ports.out.CryptoPriceGateway;
import co.com.ficohsa.domain.ports.out.CryptoPriceGateway.CryptoPriceResponse;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * Caso de uso: Obtener precio de criptomoneda
 * Usa CryptoPriceGateway (output port del dominio)
 */
@RequiredArgsConstructor
public class GetCryptoPriceUseCase {
    private final CryptoPriceGateway cryptoPriceGateway;

    public Mono<CryptoPriceResponse> execute(String symbol) {
        return cryptoPriceGateway.getCryptoPrice(symbol);
    }
}
