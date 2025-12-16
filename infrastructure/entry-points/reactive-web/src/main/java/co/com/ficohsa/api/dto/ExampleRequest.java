package co.com.ficohsa.api.dto;

import co.com.ficohsa.logging.domain.annotation.MaskStrategy;
import co.com.ficohsa.logging.domain.annotation.Sensitive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExampleRequest {
    
    @Sensitive(strategy = MaskStrategy.PREFIX, maskLength = 4)
    private String accountNumber;
    
    @Sensitive(strategy = MaskStrategy.FULL)
    private String pin;
    
    private BigDecimal amount;
    private String description;
}
