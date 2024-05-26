package org.example.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class FinnhubConfig {

    @Value("${finnhub.api.key}")
    private String secretKey;
}
