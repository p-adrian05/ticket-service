package com.training.epam.ticketservice.core.finance.bank.staticbank.impl;

import com.epam.training.ticketservice.core.finance.bank.staticbank.impl.StaticBank;

import com.epam.training.ticketservice.core.finance.bank.staticbank.model.StaticExchangeRates;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Currency;
import java.util.Optional;

public class StaticBankTest {

    private static final Currency HUF_CURRENCY = Currency.getInstance("HUF");
    private static final Currency USD_CURRENCY = Currency.getInstance("USD");

    private StaticBank underTest;

    @Test
    public void testConstructorShouldThrowNullPointerExceptionWhenStaticExchangeRatesDependencyIsNull() {
        // Given

        // When
        Assertions.assertThrows(NullPointerException.class, () -> new StaticBank(null));

        // Then
    }

    @Test
    public void testGetExchangeRateShouldThrowNullPointerExceptionWhenFromParameterIsNull() {
        // Given
        StaticExchangeRates staticExchangeRates = Mockito.mock(StaticExchangeRates.class);
        underTest = new StaticBank(staticExchangeRates);

        // When
        Assertions.assertThrows(NullPointerException.class, () -> underTest.getExchangeRate(null, HUF_CURRENCY));

        // Then
        Mockito.verifyNoMoreInteractions(staticExchangeRates);
    }

    @Test
    public void testGetExchangeRateShouldThrowNullPointerExceptionWhenToParameterIsNull() {
        // Given
        StaticExchangeRates staticExchangeRates = Mockito.mock(StaticExchangeRates.class);
        underTest = new StaticBank(staticExchangeRates);

        // When
        Assertions.assertThrows(NullPointerException.class, () -> underTest.getExchangeRate(HUF_CURRENCY, null));

        // Then
        Mockito.verifyNoMoreInteractions(staticExchangeRates);
    }

    @Test
    public void testGetExchangeRateShouldReturnOneWhenTheCurrenciesAreTheSame() {
        // Given
        StaticExchangeRates staticExchangeRates = Mockito.mock(StaticExchangeRates.class);
        underTest = new StaticBank(staticExchangeRates);
        Optional<Double> expected = Optional.of(1D);

        // When
        Optional<Double> actual = underTest.getExchangeRate(HUF_CURRENCY, HUF_CURRENCY);

        // Then
        Assertions.assertEquals(expected, actual);
        Mockito.verifyNoMoreInteractions(staticExchangeRates);
    }

    @Test
    public void testGetExchangeRateShouldReturnWithTheCorrectRateWhenRateExists() {
        // Given
        StaticExchangeRates staticExchangeRates = Mockito.mock(StaticExchangeRates.class);
        Mockito.when(staticExchangeRates.get(HUF_CURRENCY, USD_CURRENCY)).thenReturn(234.5D);
        underTest = new StaticBank(staticExchangeRates);
        Optional<Double> expected = Optional.of(234.5D);

        // When
        Optional<Double> actual = underTest.getExchangeRate(HUF_CURRENCY, USD_CURRENCY);

        // Then
        Assertions.assertEquals(expected, actual);
        Mockito.verify(staticExchangeRates).get(HUF_CURRENCY, USD_CURRENCY);
        Mockito.verifyNoMoreInteractions(staticExchangeRates);
    }

    @Test
    public void testGetExchangeRateShouldReturnWithOptionalEmptyWhenRateDoesNotExist() {
        // Given
        StaticExchangeRates staticExchangeRates = Mockito.mock(StaticExchangeRates.class);
        Mockito.when(staticExchangeRates.get(HUF_CURRENCY, USD_CURRENCY)).thenReturn(null);
        underTest = new StaticBank(staticExchangeRates);
        Optional<Double> expected = Optional.empty();

        // When
        Optional<Double> actual = underTest.getExchangeRate(HUF_CURRENCY, USD_CURRENCY);

        // Then
        Assertions.assertEquals(expected, actual);
        Mockito.verify(staticExchangeRates).get(HUF_CURRENCY, USD_CURRENCY);
        Mockito.verifyNoMoreInteractions(staticExchangeRates);
    }
}
