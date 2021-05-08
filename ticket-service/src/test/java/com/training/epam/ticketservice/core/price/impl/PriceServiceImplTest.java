package com.training.epam.ticketservice.core.price.impl;


import com.epam.training.ticketservice.core.price.exceptions.PriceAlreadyExistsException;
import com.epam.training.ticketservice.core.price.exceptions.UnknownPriceException;
import com.epam.training.ticketservice.core.price.impl.PriceServiceImpl;
import com.epam.training.ticketservice.core.price.model.PriceDto;
import com.epam.training.ticketservice.core.price.persistence.entity.PriceEntity;
import com.epam.training.ticketservice.core.price.persistence.repository.PriceRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Currency;
import java.util.Optional;


public class PriceServiceImplTest {

    private PriceServiceImpl underTest;

    private PriceRepository priceRepository;

    private static final PriceEntity PRICE_ENTITY = PriceEntity.builder()
        .id(null)
        .name("Base")
        .currency("HUF")
        .value(1500)
        .build();
    private static final PriceDto PRICE_DTO = PriceDto.builder()
        .name("Base")
        .currency(Currency.getInstance("HUF"))
        .value(1500)
        .build();

    @BeforeEach
    public void init() {
        priceRepository = Mockito.mock(PriceRepository.class);
        underTest = new PriceServiceImpl(priceRepository);
    }

    @Test
    public void testCreatePriceShouldCallPriceRepositoryWhenPriceInputIsValid() throws PriceAlreadyExistsException {
        // Given
        Mockito.when(priceRepository.existsByName(PRICE_DTO.getName())).thenReturn(false);
        Mockito.when(priceRepository.save(PRICE_ENTITY)).thenReturn(PRICE_ENTITY);
        // When
        underTest.createPrice(PRICE_DTO);
        // Then
        Mockito.verify(priceRepository).existsByName(PRICE_DTO.getName());
        Mockito.verify(priceRepository).save(PRICE_ENTITY);
        Mockito.verifyNoMoreInteractions(priceRepository);
    }

    @Test
    public void testCreatePriceShouldThrowPriceAlreadyExistsExceptionWhenPriceInputIsExists() {
        // Given
        Mockito.when(priceRepository.existsByName(PRICE_DTO.getName())).thenReturn(true);
        // When
        Assertions.assertThrows(PriceAlreadyExistsException.class, () -> underTest.createPrice(PRICE_DTO));
        // Then
        Mockito.verify(priceRepository).existsByName(PRICE_DTO.getName());
        Mockito.verifyNoMoreInteractions(priceRepository);
    }

    @Test
    public void testCreatePriceShouldThrowNullPointerExceptionWhenPriceNameIsNull() {
        // Given
        PriceDto priceDto = PriceDto.builder()
            .name(null)
            .currency(Currency.getInstance("HUF"))
            .value(1500)
            .build();
        // When
        Assertions.assertThrows(NullPointerException.class, () -> underTest.createPrice(priceDto));
        // Then
        Mockito.verifyNoMoreInteractions(priceRepository);
    }

    @Test
    public void testCreatePriceShouldThrowNullPointerExceptionWhenPriceValueIsNull() {
        // Given
        PriceDto priceDto = PriceDto.builder()
            .name("Base")
            .currency(Currency.getInstance("HUF"))
            .value(null)
            .build();
        // When
        Assertions.assertThrows(NullPointerException.class, () -> underTest.createPrice(priceDto));
        // Then
        Mockito.verifyNoMoreInteractions(priceRepository);
    }

    @Test
    public void testUpdatePriceShouldThrowUnknownPriceExceptionExceptionWhenPriceIsNotFound() {
        // Given
        Mockito.when(priceRepository.findByName(PRICE_DTO.getName())).thenReturn(Optional.empty());
        // When
        Assertions.assertThrows(UnknownPriceException.class, () -> underTest.updatePrice(PRICE_DTO));
        // Then
        Mockito.verify(priceRepository).findByName(PRICE_DTO.getName());
        Mockito.verifyNoMoreInteractions(priceRepository);
    }

    @Test
    public void testUpdatePriceShouldCallPriceRepositoryWhenPriceInputIsValid()
        throws UnknownPriceException {
        // Given
        Mockito.when(priceRepository.findByName(PRICE_DTO.getName())).thenReturn(Optional.of(PRICE_ENTITY));
        Mockito.when(priceRepository.save(PRICE_ENTITY)).thenReturn(PRICE_ENTITY);
        // When
        underTest.updatePrice(PRICE_DTO);
        // Then
        Mockito.verify(priceRepository).findByName(PRICE_DTO.getName());
        Mockito.verify(priceRepository).save(PRICE_ENTITY);
        Mockito.verifyNoMoreInteractions(priceRepository);
    }

    @Test
    public void testUpdatePriceShouldThrowNullPointerExceptionWhenPriceInputIsNull() {
        // When
        Assertions.assertThrows(NullPointerException.class, () -> underTest.updatePrice(null));
        // Then
        Mockito.verifyNoMoreInteractions(priceRepository);
    }

}
