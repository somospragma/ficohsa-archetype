package co.com.ficohsa;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import reactor.blockhound.BlockHound;
import reactor.core.publisher.Mono;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertThrows;

class BlockHoundTest {

    @BeforeAll
    static void setUp() {
        BlockHound.install();
    }

    @Test
    void shouldDetectBlockingCall() {
        assertThrows(Exception.class, () -> {
            Mono.delay(Duration.ofMillis(1))
                    .doOnNext(it -> {
                        try {
                            Thread.sleep(10); // Blocking call
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .block();
        });
    }

    @Test
    void shouldAllowNonBlockingCall() {
        Mono.delay(Duration.ofMillis(1))
                .map(it -> it * 2)
                .block();
    }
}
