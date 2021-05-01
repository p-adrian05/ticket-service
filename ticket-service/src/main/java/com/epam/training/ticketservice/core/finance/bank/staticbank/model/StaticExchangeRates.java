package com.epam.training.ticketservice.core.finance.bank.staticbank.model;

import org.springframework.stereotype.Component;

import java.util.Currency;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class StaticExchangeRates {

    private final Map<CurrencyPair,Double> exchangeRatesMap;

    StaticExchangeRates(Map<CurrencyPair, Double> exchangeRatesMap) {
        this.exchangeRatesMap = exchangeRatesMap;
    }

    public Double get(Currency from, Currency to) {
        return exchangeRatesMap.get(CurrencyPair.of(from, to));
    }

    public static class Builder {

        private final Map<CurrencyPair,Double> exchangeRatesMap = new HashMap<>();

        public Builder addRate(String from, String to, double rate) {
            return addRate(Currency.getInstance(from), Currency.getInstance(to), rate);
        }

        public Builder addRate(Currency from, Currency to, double rate) {
            exchangeRatesMap.put(CurrencyPair.of(from, to), rate);
            return this;
        }

        public Builder addRate(String from, String to, double rate, double reverseRate) {
            return addRate(Currency.getInstance(from), Currency.getInstance(to), rate, reverseRate);
        }

        public Builder addRate(Currency from, Currency to, double rate, double reverseRate) {
            exchangeRatesMap.put(CurrencyPair.of(to, from), reverseRate);
            return addRate(from, to, rate);
        }

        public StaticExchangeRates build() {
            if(exchangeRatesMap.isEmpty()) {
                throw new IllegalArgumentException("No exchange rate has been added");
            }
            return new StaticExchangeRates(exchangeRatesMap);
        }

    }

    private static class CurrencyPair {

        private final Currency from;
        private final Currency to;

        public static CurrencyPair of(Currency from, Currency to) {
            return new CurrencyPair(from, to);
        }

        private CurrencyPair(Currency from, Currency to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CurrencyPair that = (CurrencyPair) o;
            return Objects.equals(from, that.from) && Objects.equals(to, that.to);
        }

        @Override
        public int hashCode() {
            return Objects.hash(from, to);
        }

        @Override
        public String toString() {
            return "CurrencyPair{" +
                    "from=" + from +
                    ", to=" + to +
                    '}';
        }

    }

}
