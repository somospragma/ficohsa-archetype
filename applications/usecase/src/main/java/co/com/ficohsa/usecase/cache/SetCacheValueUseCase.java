package co.com.ficohsa.usecase.cache;

import co.com.ficohsa.domain.ports.out.CacheGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Caso de uso: Guardar un valor en caché
 * Usa CacheGateway (output port del dominio)
 */
@RequiredArgsConstructor
public class SetCacheValueUseCase {
    private final CacheGateway cacheGateway;

    public Mono<Boolean> execute(String key, String value, Duration ttl) {
        return cacheGateway.set(key, value, ttl);
    }
}

