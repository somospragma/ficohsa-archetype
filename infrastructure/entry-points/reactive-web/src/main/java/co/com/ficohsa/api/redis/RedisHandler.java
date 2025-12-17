package co.com.ficohsa.api.redis;

import co.com.ficohsa.usecase.cache.GetCacheValueUseCase;
import co.com.ficohsa.usecase.cache.IncrementCacheCounterUseCase;
import co.com.ficohsa.usecase.cache.SetCacheValueUseCase;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

/**
 * Handler para operaciones de caché
 * Usa casos de uso en lugar de acceder directamente al adaptador
 */
@Component
public class RedisHandler {
    private final SetCacheValueUseCase setCacheValueUseCase;
    private final GetCacheValueUseCase getCacheValueUseCase;
    private final IncrementCacheCounterUseCase incrementCacheCounterUseCase;

    public RedisHandler(SetCacheValueUseCase setCacheValueUseCase,
                        GetCacheValueUseCase getCacheValueUseCase,
                        IncrementCacheCounterUseCase incrementCacheCounterUseCase) {
        this.setCacheValueUseCase = setCacheValueUseCase;
        this.getCacheValueUseCase = getCacheValueUseCase;
        this.incrementCacheCounterUseCase = incrementCacheCounterUseCase;
    }

    /**
     * POST /api/redis/set?key=...&value=...&ttl=60
     */
    public Mono<ServerResponse> set(ServerRequest request) {
        String key = request.queryParam("key").orElse("test-key");
        String value = request.queryParam("value").orElse("test-value");
        long ttlSeconds = Long.parseLong(request.queryParam("ttl").orElse("60"));

        return setCacheValueUseCase.execute(key, value, Duration.ofSeconds(ttlSeconds))
            .flatMap(result -> ServerResponse.ok().bodyValue(Map.of(
                "success", result,
                "key", key,
                "value", value,
                "ttl", ttlSeconds
            )));
    }

    /**
     * GET /api/redis/get?key=...
     */
    public Mono<ServerResponse> get(ServerRequest request) {
        String key = request.queryParam("key").orElse("test-key");

        return getCacheValueUseCase.execute(key)
            .flatMap(value -> ServerResponse.ok().bodyValue(Map.of(
                "key", key,
                "value", value,
                "found", true
            )))
            .switchIfEmpty(ServerResponse.ok().bodyValue(Map.of(
                "key", key,
                "found", false
            )));
    }

    /**
     * POST /api/redis/increment?key=...
     */
    public Mono<ServerResponse> increment(ServerRequest request) {
        String key = request.queryParam("key").orElse("counter");

        return incrementCacheCounterUseCase.execute(key)
            .flatMap(newValue -> ServerResponse.ok().bodyValue(Map.of(
                "key", key,
                "value", newValue
            )));
    }
}

