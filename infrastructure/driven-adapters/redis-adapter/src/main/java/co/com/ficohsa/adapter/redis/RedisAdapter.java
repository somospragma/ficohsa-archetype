package co.com.ficohsa.adapter.redis;

import co.com.ficohsa.logging.clients.annotation.LogExternalCall;
import co.com.ficohsa.domain.ports.out.CacheGateway;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Adaptador para Redis que implementa CacheGateway
 * Con logging automático de latencia mediante @LogExternalCall
 */
@Component
public class RedisAdapter implements CacheGateway {
    private final ReactiveRedisTemplate<String, String> redisTemplate;

    public RedisAdapter(ReactiveRedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    @LogExternalCall(provider = "Redis")
    public Mono<Boolean> set(String key, String value, Duration ttl) {
        return redisTemplate.opsForValue()
            .set(key, value, ttl);
    }

    @Override
    @LogExternalCall(provider = "Redis")
    public Mono<String> get(String key) {
        return redisTemplate.opsForValue()
            .get(key);
    }

    @Override
    @LogExternalCall(provider = "Redis")
    public Mono<Boolean> delete(String key) {
        return redisTemplate.delete(key)
            .map(count -> count > 0);
    }

    @Override
    @LogExternalCall(provider = "Redis")
    public Mono<Boolean> exists(String key) {
        return redisTemplate.hasKey(key);
    }

    @Override
    @LogExternalCall(provider = "Redis")
    public Mono<Boolean> expire(String key, Duration timeout) {
        return redisTemplate.expire(key, timeout);
    }

    @Override
    @LogExternalCall(provider = "Redis")
    public Mono<Long> increment(String key) {
        return redisTemplate.opsForValue()
            .increment(key);
    }
}

