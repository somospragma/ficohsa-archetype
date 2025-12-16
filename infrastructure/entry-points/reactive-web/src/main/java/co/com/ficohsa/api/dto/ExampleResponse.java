package co.com.ficohsa.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExampleResponse {
    private String transactionId;
    private String status;
    private String message;
}
