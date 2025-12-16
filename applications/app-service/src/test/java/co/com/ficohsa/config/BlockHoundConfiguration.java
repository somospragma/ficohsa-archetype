package co.com.ficohsa.config;

import reactor.blockhound.BlockHound;
import reactor.blockhound.integration.BlockHoundIntegration;

public class BlockHoundConfiguration implements BlockHoundIntegration {

    @Override
    public void applyTo(BlockHound.Builder builder) {
        // Permitir llamadas bloqueantes específicas si es necesario
        builder.allowBlockingCallsInside("java.io.FileInputStream", "readBytes");
        builder.allowBlockingCallsInside("java.io.RandomAccessFile", "readBytes");
        
        // Permitir en clases de configuración
        builder.allowBlockingCallsInside("org.springframework.context.support.AbstractApplicationContext", "refresh");
    }
}
