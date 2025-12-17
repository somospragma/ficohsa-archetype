package co.com.ficohsa.usecase;

import co.com.ficohsa.domain.ports.out.CryptoPriceGateway;
import co.com.ficohsa.domain.ports.out.CryptoPriceGateway.CryptoPriceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * Caso de uso: Obtener precio de criptomoneda
 * Usa CryptoPriceGateway (output port del dominio)
 */
@Slf4j
@RequiredArgsConstructor
public class GetCryptoPriceUseCase {
    private final CryptoPriceGateway cryptoPriceGateway;

    public Mono<CryptoPriceResponse> execute(String symbol) {
        log.info("Ejecutando caso de uso: Obtener precio de criptomoneda para el símbolo {}", symbol);
        return cryptoPriceGateway.getCryptoPrice(symbol);
    }
}
