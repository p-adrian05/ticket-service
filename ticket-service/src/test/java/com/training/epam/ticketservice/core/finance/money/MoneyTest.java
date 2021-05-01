package com.training.epam.ticketservice.core.finance.money;

import com.epam.training.ticketservice.core.finance.bank.Bank;
import com.epam.training.ticketservice.core.finance.money.Money;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Currency;
import java.util.Optional;

public class MoneyTest {

    private static final Currency HUF_CURRENCY = Currency.getInstance("HUF");
    private static final Currency USD_CURRENCY = Currency.getInstance("USD");
    private static final Currency GBP_CURRENCY = Currency.getInstance("GBP");

    @Test
    public void testToShouldReturnsExpectedResultWhenDifferentCurrencyIsUsed() {
        // Given
        Money underTest = new Money(3, USD_CURRENCY);
        Money expected = new Money(6, HUF_CURRENCY);
        Bank mockBank = Mockito.mock(Bank.class);
        Mockito.when(mockBank.getExchangeRate(USD_CURRENCY, HUF_CURRENCY)).thenReturn(Optional.of(2D));

        // When
        Money actual = underTest.to(HUF_CURRENCY, mockBank);

        // Then
        Assertions.assertEquals(expected, actual);
        Mockito.verify(mockBank).getExchangeRate(USD_CURRENCY, HUF_CURRENCY);
        Mockito.verifyNoMoreInteractions(mockBank);
    }

    @Test
    public void testToShouldThrowExceptionWhenExchangeRateDoesNotExist() {
        // Given
        Money money = new Money(1, HUF_CURRENCY);
        Bank mockBank = Mockito.mock(Bank.class);
        Mockito.when(mockBank.getExchangeRate(HUF_CURRENCY, GBP_CURRENCY)).thenReturn(Optional.empty());

        // When
        Assertions.assertThrows(UnsupportedOperationException.class, () -> money.to(GBP_CURRENCY, mockBank));

        // Then
        Mockito.verify(mockBank).getExchangeRate(HUF_CURRENCY, GBP_CURRENCY);
        Mockito.verifyNoMoreInteractions(mockBank);
    }

    @Test
    public void testToShouldThrowNullPointerExceptionWhenCurrencyParameterIsNull() {
        // Given
        Money money = new Money(1, HUF_CURRENCY);
        Bank mockBank = Mockito.mock(Bank.class);

        // When
        Assertions.assertThrows(NullPointerException.class, () -> money.to((Currency) null, mockBank));
    }

    @Test
    public void testToShouldThrowNullPointerExceptionWhenBankParameterIsNull() {
        // Given
        Money money = new Money(1, HUF_CURRENCY);

        // When
        Assertions.assertThrows(NullPointerException.class, () -> money.to(USD_CURRENCY, null));
    }

    @Test
    public void testToWithStringParameterShouldReturnsExpectedResultWhenDifferentCurrencyIsUsed() {
        // Given
        Money underTest = new Money(3, USD_CURRENCY);
        Money expected = new Money(6, HUF_CURRENCY);
        Bank mockBank = Mockito.mock(Bank.class);
        Mockito.when(mockBank.getExchangeRate(USD_CURRENCY, HUF_CURRENCY)).thenReturn(Optional.of(2D));

        // When
        Money actual = underTest.to("HUF", mockBank);

        // Then
        Assertions.assertEquals(expected, actual);
        Mockito.verify(mockBank).getExchangeRate(USD_CURRENCY, HUF_CURRENCY);
        Mockito.verifyNoMoreInteractions(mockBank);
    }

    @Test
    public void testAddShouldReturnsExpectedResultWhenDifferentCurrencyIsUsed() {
        // Given
        Money underTest = new Money(120, USD_CURRENCY);
        Money expected = new Money(369.3, USD_CURRENCY);
        Bank mockBank = Mockito.mock(Bank.class);
        Money moneyToAdd = Mockito.mock(Money.class);
        Mockito.when(moneyToAdd.to(USD_CURRENCY, mockBank)).thenReturn(new Money(249.3, HUF_CURRENCY));
        Mockito.when(mockBank.getExchangeRate(HUF_CURRENCY, USD_CURRENCY)).thenReturn(Optional.of(249.3));

        // When
        Money actual = underTest.add(moneyToAdd, mockBank);

        // Then
        Assertions.assertEquals(expected, actual);
        Mockito.verify(moneyToAdd).to(USD_CURRENCY, mockBank);
        Mockito.verifyNoMoreInteractions(moneyToAdd, mockBank);
    }

    @Test
    public void testAddShouldThrowExceptionWhenExchangeRateDoesNotExist() {
        // Given
        Money money = new Money(1, HUF_CURRENCY);
        Money moneyToAdd = new Money(1, GBP_CURRENCY);
        Bank mockBank = Mockito.mock(Bank.class);
        Mockito.when(mockBank.getExchangeRate(GBP_CURRENCY, HUF_CURRENCY)).thenReturn(Optional.empty());

        // When
        Assertions.assertThrows(UnsupportedOperationException.class, () -> money.add(moneyToAdd, mockBank));

        // Then
        Mockito.verify(mockBank).getExchangeRate(GBP_CURRENCY, HUF_CURRENCY);
        Mockito.verifyNoMoreInteractions(mockBank);
    }

    @Test
    public void testAddShouldThrowNullPointerExceptionWhenBankParameterIsNull() {
        // Given
        Money money = new Money(1, HUF_CURRENCY);
        Money moneyToAdd = new Money(1, USD_CURRENCY);

        // When
        Assertions.assertThrows(NullPointerException.class, () -> money.add(moneyToAdd, null));
    }

    @Test
    public void testAddShouldThrowNullPointerExceptionWhenMoneyToAddParameterIsNull() {
        // Given
        Money money = new Money(1, HUF_CURRENCY);
        Bank mockBank = Mockito.mock(Bank.class);

        // When
        Assertions.assertThrows(NullPointerException.class, () -> money.add(null, mockBank));

        // Then
        Mockito.verifyNoMoreInteractions(mockBank);
    }

    @Test
    public void testSubtractShouldReturnsExpectedResultWhenDifferentCurrencyIsUsed() {
        // Given
        Money underTest = new Money(120, USD_CURRENCY);
        Money expected = new Money(-129.3, USD_CURRENCY);
        Bank mockBank = Mockito.mock(Bank.class);
        Money moneyToSubtract = Mockito.mock(Money.class);
        Mockito.when(moneyToSubtract.to(USD_CURRENCY, mockBank)).thenReturn(new Money(249.3, HUF_CURRENCY));
        Mockito.when(mockBank.getExchangeRate(HUF_CURRENCY, USD_CURRENCY)).thenReturn(Optional.of(249.3));

        // When
        Money actual = underTest.subtract(moneyToSubtract, mockBank);

        // Then
        Assertions.assertEquals(expected, actual);
        Mockito.verify(moneyToSubtract).to(USD_CURRENCY, mockBank);
        Mockito.verifyNoMoreInteractions(moneyToSubtract, mockBank);
    }

    @Test
    public void testSubtractShouldThrowExceptionWhenExchangeRateDoesNotExist() {
        // Given
        Money money = new Money(1, HUF_CURRENCY);
        Money moneyToSubtract = new Money(1, GBP_CURRENCY);
        Bank mockBank = Mockito.mock(Bank.class);
        Mockito.when(mockBank.getExchangeRate(GBP_CURRENCY, HUF_CURRENCY)).thenReturn(Optional.empty());

        // When
        Assertions.assertThrows(UnsupportedOperationException.class, () -> money.subtract(moneyToSubtract, mockBank));

        // Then
        Mockito.verify(mockBank).getExchangeRate(GBP_CURRENCY, HUF_CURRENCY);
        Mockito.verifyNoMoreInteractions(mockBank);
    }

    @Test
    public void testSubtractShouldThrowNullPointerExceptionWhenBankParameterIsNull() {
        // Given
        Money money = new Money(1, HUF_CURRENCY);
        Money moneyToAdd = new Money(1, USD_CURRENCY);

        // When
        Assertions.assertThrows(NullPointerException.class, () -> money.subtract(moneyToAdd, null));
    }

    @Test
    public void testSubtractShouldThrowNullPointerExceptionWhenMoneyToSubtractParameterIsNull() {
        // Given
        Money money = new Money(1, HUF_CURRENCY);
        Bank mockBank = Mockito.mock(Bank.class);

        // When
        Assertions.assertThrows(NullPointerException.class, () -> money.subtract(null, mockBank));

        // Then
        Mockito.verifyNoMoreInteractions(mockBank);
    }

    @Test
    public void testMultiplyShouldReturnsExpectedResultWhenTheMultiplierIsAPositiveWholeNumber() {
        // Given
        Money underTest = new Money(120, USD_CURRENCY);
        Money expected = new Money(240, USD_CURRENCY);

        // When
        Money actual = underTest.multiply(2);

        // Then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testMultiplyShouldReturnsExpectedResultWhenTheMultiplierIsANegativeWholeNumber() {
        // Given
        Money underTest = new Money(120, USD_CURRENCY);
        Money expected = new Money(-240, USD_CURRENCY);

        // When
        Money actual = underTest.multiply(-2);

        // Then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testMultiplyShouldReturnsExpectedResultWhenTheMultiplierIsNotWholeNumber() {
        // Given
        Money underTest = new Money(120, USD_CURRENCY);
        Money expected = new Money(60, USD_CURRENCY);

        // When
        Money actual = underTest.multiply(0.5);

        // Then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testDivideShouldReturnsExpectedResultWhenTheDividerIsAPositiveWholeNumber() {
        // Given
        Money underTest = new Money(120, USD_CURRENCY);
        Money expected = new Money(60, USD_CURRENCY);

        // When
        Money actual = underTest.divide(2);

        // Then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testDivideShouldReturnsExpectedResultWhenTheDividerIsANegativeWholeNumber() {
        // Given
        Money underTest = new Money(120, USD_CURRENCY);
        Money expected = new Money(-60, USD_CURRENCY);

        // When
        Money actual = underTest.divide(-2);

        // Then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testDivideShouldReturnsExpectedResultWhenTheDividerIsNotWholeNumber() {
        // Given
        Money underTest = new Money(120, USD_CURRENCY);
        Money expected = new Money(240, USD_CURRENCY);

        // When
        Money actual = underTest.divide(0.5);

        // Then
        Assertions.assertEquals(expected, actual);
    }

}
