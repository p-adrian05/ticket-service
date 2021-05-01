package com.epam.training.ticketservice.core.configuration;

import com.epam.training.ticketservice.core.finance.bank.Bank;
import com.epam.training.ticketservice.core.finance.bank.staticbank.impl.StaticBank;
import com.epam.training.ticketservice.core.finance.bank.staticbank.model.StaticExchangeRates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {
    @Bean
    public Bank bank(){
        return StaticBank.of(() -> new StaticExchangeRates.Builder()
            .addRate("HUF", "EUR", 0.0028, 360)
            .addRate("HUF", "USD", 0.0033, 300)
            .build());
    }
}