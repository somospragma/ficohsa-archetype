package co.com.ficohsa.usecase.cache;

import co.com.ficohsa.domain.ports.out.CacheGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * Caso de uso: Obtener un valor de caché
 * Usa CacheGateway (output port del dominio)
 */
@RequiredArgsConstructor
public class GetCacheValueUseCase {
    private final CacheGateway cacheGateway;

    public Mono<String> execute(String key) {
        return cacheGateway.get(key);
    }
}

