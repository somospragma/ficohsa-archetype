package co.com.ficohsa.api.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RedisRouter {

    @Bean
    public RouterFunction<ServerResponse> redisRoutes(RedisHandler handler) {
        return route()
            .POST("/api/cache/set", handler::set)
            .GET("/api/cache/get", handler::get)
            .POST("/api/cache/increment", handler::increment)
            .build();
    }
}

