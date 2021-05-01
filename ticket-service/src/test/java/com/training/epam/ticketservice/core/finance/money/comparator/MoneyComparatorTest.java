package com.training.epam.ticketservice.core.finance.money.comparator;

import com.epam.training.ticketservice.core.finance.bank.Bank;
import com.epam.training.ticketservice.core.finance.money.Money;
import com.epam.training.ticketservice.core.finance.money.comparator.MoneyComparator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Currency;

public class MoneyComparatorTest {

    private static final Currency HUF = Currency.getInstance("HUF");

    private MoneyComparator underTest;

    @Test
    public void testCompareShouldThrowNullPointerExceptionWhenTheFirstParameterIsNull() {
        // Given
        Bank bank = Mockito.mock(Bank.class);
        Money o2 = Mockito.mock(Money.class);
        underTest = new MoneyComparator(bank);

        // When
        Assertions.assertThrows(NullPointerException.class, () -> underTest.compare(null, o2));

        // Then
        Mockito.verifyNoMoreInteractions(bank, o2);
    }

    @Test
    public void testCompareShouldThrowNullPointerExceptionWhenTheSecondParameterIsNull() {
        // Given
        Bank bank = Mockito.mock(Bank.class);
        Money o1 = Mockito.mock(Money.class);
        underTest = new MoneyComparator(bank);

        // When
        Assertions.assertThrows(NullPointerException.class, () -> underTest.compare(o1, null));

        // Then
        Mockito.verifyNoMoreInteractions(bank, o1);
    }

    @Test
    public void testCompareShouldReturnZeroWhenParametersAreEqual() {
        // Given
        Bank bank = Mockito.mock(Bank.class);
        Money o1 = Mockito.mock(Money.class);
        Mockito.when(o1.getAmount()).thenReturn(2.4D);
        Mockito.when(o1.getCurrency()).thenReturn(HUF);
        Money o2 = Mockito.mock(Money.class);
        Money convertedO2 = Mockito.mock(Money.class);
        Mockito.when(o2.to(HUF, bank)).thenReturn(convertedO2);
        Mockito.when(convertedO2.getAmount()).thenReturn(2.4D);
        underTest = new MoneyComparator(bank);
        int expected = 0;

        // When
        int actual = underTest.compare(o1, o2);

        // Then
        Assertions.assertEquals(expected, actual);
        Mockito.verify(o1).getAmount();
        Mockito.verify(o1).getCurrency();
        Mockito.verify(o2).to(HUF, bank);
        Mockito.verify(convertedO2).getAmount();
        Mockito.verifyNoMoreInteractions(bank, o1, o2, convertedO2);
    }

    @Test
    public void testCompareShouldReturnNegativeValueWhenFirstParameterIsNotGreater() {
        // Given
        Bank bank = Mockito.mock(Bank.class);
        Money o1 = Mockito.mock(Money.class);
        Mockito.when(o1.getAmount()).thenReturn(2.4D);
        Mockito.when(o1.getCurrency()).thenReturn(HUF);
        Money o2 = Mockito.mock(Money.class);
        Money convertedO2 = Mockito.mock(Money.class);
        Mockito.when(o2.to(HUF, bank)).thenReturn(convertedO2);
        Mockito.when(convertedO2.getAmount()).thenReturn(2.5D);
        underTest = new MoneyComparator(bank);

        // When
        int actual = underTest.compare(o1, o2);

        // Then
        Assertions.assertTrue(actual < 0);
        Mockito.verify(o1).getAmount();
        Mockito.verify(o1).getCurrency();
        Mockito.verify(o2).to(HUF, bank);
        Mockito.verify(convertedO2).getAmount();
        Mockito.verifyNoMoreInteractions(bank, o1, o2, convertedO2);
    }

    @Test
    public void testCompareShouldReturnPositiveValueWhenFirstParameterIsGreater() {
        // Given
        Bank bank = Mockito.mock(Bank.class);
        Money o1 = Mockito.mock(Money.class);
        Mockito.when(o1.getAmount()).thenReturn(2.5D);
        Mockito.when(o1.getCurrency()).thenReturn(HUF);
        Money o2 = Mockito.mock(Money.class);
        Money convertedO2 = Mockito.mock(Money.class);
        Mockito.when(o2.to(HUF, bank)).thenReturn(convertedO2);
        Mockito.when(convertedO2.getAmount()).thenReturn(2.4D);
        underTest = new MoneyComparator(bank);

        // When
        int actual = underTest.compare(o1, o2);

        // Then
        Assertions.assertTrue(actual > 0);
        Mockito.verify(o1).getAmount();
        Mockito.verify(o1).getCurrency();
        Mockito.verify(o2).to(HUF, bank);
        Mockito.verify(convertedO2).getAmount();
        Mockito.verifyNoMoreInteractions(bank, o1, o2, convertedO2);
    }

}
