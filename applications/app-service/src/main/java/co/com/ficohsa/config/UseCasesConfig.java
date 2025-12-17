package co.com.ficohsa.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

/**
 * Configuración para auto-registro de casos de uso.
 * Detecta automáticamente todas las clases que terminan en "UseCase"
 * y las registra como beans de Spring sin necesidad de anotaciones.
 * Esto mantiene los casos de uso libres de dependencias del framework (Clean Architecture).
 */
@Configuration
@ComponentScan(basePackages = "co.com.ficohsa.usecase",
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "^.+UseCase$")
        },
        useDefaultFilters = false)
public class UseCasesConfig {
}
