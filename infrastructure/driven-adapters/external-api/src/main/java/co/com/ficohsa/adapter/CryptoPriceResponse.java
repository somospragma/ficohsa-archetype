package co.com.ficohsa.adapter;

import lombok.Data;

@Data
public class CryptoPriceResponse {
    private String symbol;
    private String price;
    private Long timestamp;
}
