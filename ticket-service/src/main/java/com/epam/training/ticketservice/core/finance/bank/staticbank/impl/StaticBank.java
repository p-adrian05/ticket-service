package com.epam.training.ticketservice.core.finance.bank.staticbank.impl;


import com.epam.training.ticketservice.core.finance.bank.Bank;
import com.epam.training.ticketservice.core.finance.bank.staticbank.StaticExchangeRateSupplier;
import com.epam.training.ticketservice.core.finance.bank.staticbank.model.StaticExchangeRates;
import org.springframework.stereotype.Component;

import java.util.Currency;
import java.util.Objects;
import java.util.Optional;

@Component
public class StaticBank implements Bank {

    private final StaticExchangeRates exchangeRates;

    public static StaticBank of(StaticExchangeRateSupplier exchangeRateSupplier) {
        return new StaticBank(exchangeRateSupplier.get());
    }

    StaticBank(StaticExchangeRates exchangeRates) {
        Objects.requireNonNull(exchangeRates, "StaticExchangeRates is a mandatory field of StaticBank");
        this.exchangeRates = exchangeRates;
    }

    @Override
    public Optional<Double> getExchangeRate(Currency from, Currency to) {
        Objects.requireNonNull(from, "From is a mandatory field of StaticBank");
        Objects.requireNonNull(to, "To is a mandatory field of StaticBank");
        if(from.equals(to)) {
            return Optional.of(1D);
        }
        return Optional.ofNullable(exchangeRates.get(from, to));
    }

}
