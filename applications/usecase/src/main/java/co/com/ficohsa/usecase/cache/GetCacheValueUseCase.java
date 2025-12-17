package co.com.ficohsa.usecase.cache;

import co.com.ficohsa.domain.ports.out.CacheGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * Caso de uso: Obtener un valor de caché
 * Usa CacheGateway (output port del dominio)
 */
@Slf4j
@RequiredArgsConstructor
public class GetCacheValueUseCase {
    private final CacheGateway cacheGateway;

    public Mono<String> execute(String key) {
        log.info("Ejecutando caso de uso: Obtener valor de caché para la clave {}", key);
        return cacheGateway.get(key);
    }
}

