package com.epam.training.ticketservice.core.finance.bank;

import java.util.Currency;
import java.util.Optional;

public interface Bank {

    Optional<Double> getExchangeRate(Currency from, Currency to);

}
