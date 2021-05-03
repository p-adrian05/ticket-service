package com.training.epam.ticketservice.ui.converter;

import com.epam.training.ticketservice.core.booking.model.SeatDto;
import com.epam.training.ticketservice.ui.converter.SeatsConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class SeatsConverterlTest {


    private SeatsConverter underTest;

    @BeforeEach
    public void init() {
        underTest = new SeatsConverter();
    }

    @Test
    public void testConvertWithTwoSeatsReturnSetOfSeatDtos() {
        // When
        Set<SeatDto> actual = underTest.convert("5,5 5,6");

        // Then
        Assertions.assertEquals(Set.of(SeatDto.of(5, 5), SeatDto.of(5, 6)), actual);
    }

    @Test
    public void testConvertWithOneSeatReturnSetOfSeatDto() {
        // When
        Set<SeatDto> actual = underTest.convert("5,5");

        // Then
        Assertions.assertEquals(Set.of(SeatDto.of(5, 5)), actual);
    }

    @Test
    public void testConvertWithWrongInputShouldThrowException() {
        // When
        Assertions.assertThrows(Exception.class, () -> underTest.convert("5,5 2"));

    }
}
