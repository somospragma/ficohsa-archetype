package co.com.ficohsa.usecase.cache;

import co.com.ficohsa.domain.ports.out.CacheGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * Caso de uso: Incrementar un contador en caché
 * Usa CacheGateway (output port del dominio)
 */
@RequiredArgsConstructor
public class IncrementCacheCounterUseCase {
    private final CacheGateway cacheGateway;

    public Mono<Long> execute(String key) {
        return cacheGateway.increment(key);
    }
}

