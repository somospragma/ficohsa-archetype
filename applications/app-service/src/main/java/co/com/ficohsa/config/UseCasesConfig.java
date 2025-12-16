package co.com.ficohsa.config;

import co.com.ficohsa.adapter.CryptoApiAdapter;
import co.com.ficohsa.usecase.GetCryptoPriceUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(basePackages = "co.com.ficohsa.usecase",
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "^.+UseCase$")
        },
        useDefaultFilters = false)
public class UseCasesConfig {

    @Bean
    public GetCryptoPriceUseCase getCryptoPriceUseCase(CryptoApiAdapter cryptoApiAdapter) {
        return new GetCryptoPriceUseCase(cryptoApiAdapter);
    }
}
