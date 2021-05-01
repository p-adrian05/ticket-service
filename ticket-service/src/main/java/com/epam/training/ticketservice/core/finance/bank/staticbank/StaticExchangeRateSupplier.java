package com.epam.training.ticketservice.core.finance.bank.staticbank;


import com.epam.training.ticketservice.core.finance.bank.staticbank.model.StaticExchangeRates;

import java.util.function.Supplier;

@FunctionalInterface
public interface StaticExchangeRateSupplier extends Supplier<StaticExchangeRates> {

}
