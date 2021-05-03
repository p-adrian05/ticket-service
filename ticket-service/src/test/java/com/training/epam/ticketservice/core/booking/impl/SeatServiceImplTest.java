package com.training.epam.ticketservice.core.booking.impl;

import com.epam.training.ticketservice.core.booking.exceptions.BookingException;
import com.epam.training.ticketservice.core.booking.impl.SeatServiceImpl;
import com.epam.training.ticketservice.core.booking.model.SeatDto;
import com.epam.training.ticketservice.core.booking.persistence.entity.SeatEntity;
import com.epam.training.ticketservice.core.booking.persistence.entity.SeatId;
import com.epam.training.ticketservice.core.booking.persistence.entity.TicketEntity;
import com.epam.training.ticketservice.core.booking.persistence.repository.SeatRepository;
import com.epam.training.ticketservice.core.finance.money.Money;
import com.epam.training.ticketservice.core.movie.persistence.entity.GenreEntity;
import com.epam.training.ticketservice.core.movie.persistence.entity.MovieEntity;
import com.epam.training.ticketservice.core.price.persistence.entity.PriceEntity;
import com.epam.training.ticketservice.core.price.persistence.repository.PriceRepository;
import com.epam.training.ticketservice.core.room.persistence.entity.RoomEntity;
import com.epam.training.ticketservice.core.screening.persistence.entity.ScreeningEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Currency;
import java.util.Optional;
import java.util.Set;

public class SeatServiceImplTest {


    private SeatServiceImpl underTest;
    private PriceRepository priceRepository;
    private SeatRepository seatRepository;
    private static final MovieEntity MOVIE_ENTITY = MovieEntity.builder()
        .id(1)
        .duration(100)
        .title("Test1 title")
        .genreEntity(new GenreEntity(1, "Action")).build();

    private static final SeatEntity SEAT_ENTITY_1 = SeatEntity.builder()
        .id(new SeatId(1, 2))
        .build();
    private static final SeatEntity SEAT_ENTITY_2 = SeatEntity.builder()
        .id(new SeatId(3, 4))
        .build();
    private static final RoomEntity ROOM_ENTITY = RoomEntity.builder()
        .id(1)
        .columns(5)
        .name("A1")
        .rows(5)
        .build();
    private static final ScreeningEntity SCREENING_ENTITY = ScreeningEntity
        .builder()
        .movieEntity(MOVIE_ENTITY)
        .roomEntity(ROOM_ENTITY)
        .seats(Set.of(SEAT_ENTITY_1, SEAT_ENTITY_2))
        .build();
    private static final TicketEntity TICKET_ENTITY = TicketEntity.builder()
        .id(1)
        .screeningEntity(SCREENING_ENTITY)
        .price(1500.0)
        .currency("HUF")
        .build();
    private static final PriceEntity PRICE_ENTITY = PriceEntity
        .builder()
        .value(1500)
        .name("Base")
        .currency("HUF")
        .build();

    @BeforeEach
    public void init() {
        priceRepository = Mockito.mock(PriceRepository.class);
        seatRepository = Mockito.mock(SeatRepository.class);
        underTest = new SeatServiceImpl(priceRepository, seatRepository);
    }

    @Test
    public void testIsFreeToSeatShouldReturnTrueWhenSeatsNotBooked() throws BookingException {
        // Given
        Set<SeatDto> seats = Set.of(SeatDto.of(4, 5), SeatDto.of(5, 2));
        // When
        boolean actual = underTest.isFreeToSeat(seats, SCREENING_ENTITY);

        // Then
        Assertions.assertTrue(actual);
    }

    @Test
    public void testIsFreeToSeatShouldThrowBookingExceptionWhenSeatAlreadyTaken() {
        // Given
        Set<SeatDto> seats = Set.of(SeatDto.of(1, 2), SeatDto.of(4, 4));
        // When
        Assertions.assertThrows(BookingException.class, () -> underTest.isFreeToSeat(seats, SCREENING_ENTITY));
    }

    @Test
    public void testIsFreeToSeatShouldThrowBookingExceptionWhenSeatNotExistsByRow() {
        // Given
        Set<SeatDto> seats = Set.of(SeatDto.of(4, 2), SeatDto.of(6, 5));
        // When
        Assertions.assertThrows(BookingException.class, () -> underTest.isFreeToSeat(seats, SCREENING_ENTITY));
    }

    @Test
    public void testIsFreeToSeatShouldThrowBookingExceptionWhenSeatNotExistsByColumn() {
        // Given
        Set<SeatDto> seats = Set.of(SeatDto.of(4, 2), SeatDto.of(5, 6));
        // When
        Assertions.assertThrows(BookingException.class, () -> underTest.isFreeToSeat(seats, SCREENING_ENTITY));
    }

    @Test
    public void testIsFreeToSeatShouldThrowBookingExceptionWhenSeatColumnIsNegative() {
        // Given
        Set<SeatDto> seats = Set.of(SeatDto.of(4, 2), SeatDto.of(5, -3));
        // When
        Assertions.assertThrows(BookingException.class, () -> underTest.isFreeToSeat(seats, SCREENING_ENTITY));
    }

    @Test
    public void testCalculateSeatPriceShouldCallPriceRepository() {
        // Given
        Set<SeatDto> seats = Set.of(SeatDto.of(4, 2), SeatDto.of(5, 6));
        Mockito.when(priceRepository.findByName(PRICE_ENTITY.getName()))
            .thenReturn(Optional.of(PRICE_ENTITY));
        // When
        Money actual = underTest.calculateSeatPrice(seats);

        // Then
        Assertions.assertEquals(3000.0, actual.getAmount());
        Mockito.verify(priceRepository).findByName(PRICE_ENTITY.getName());
        Mockito.verifyNoMoreInteractions(priceRepository);
    }

    @Test
    public void testBookSeatsToTicketShouldCallSeatRepositoryWhenInputIsValid() throws BookingException {
        // Given
        Set<SeatDto> seats = Set.of(SeatDto.of(5, 5), SeatDto.of(4, 5));
        Set<SeatEntity> seatEntities = Set.of(SeatEntity.builder()
            .id(new SeatId(4, 5))
            .ticketEntity(TICKET_ENTITY)
            .screeningEntity(TICKET_ENTITY.getScreeningEntity())
            .priceEntity(PRICE_ENTITY)
            .build(), SeatEntity.builder()
            .id(new SeatId(5, 5))
            .ticketEntity(TICKET_ENTITY)
            .priceEntity(PRICE_ENTITY)
            .screeningEntity(TICKET_ENTITY.getScreeningEntity())
            .build());
        Mockito.when(seatRepository.saveAll(seatEntities)).thenReturn(seatEntities);
        Mockito.when(priceRepository.findByName(PRICE_ENTITY.getName()))
            .thenReturn(Optional.of(PRICE_ENTITY));
        // When
        underTest.bookSeatsToTicket(seats, TICKET_ENTITY, SCREENING_ENTITY);
        // Then
        Mockito.verify(seatRepository).saveAll(seatEntities);
        Mockito.verify(priceRepository, Mockito.times(2)).findByName(PRICE_ENTITY.getName());
        Mockito.verifyNoMoreInteractions(seatRepository);
        Mockito.verifyNoMoreInteractions(priceRepository);
    }

    @Test
    public void testBookSeatsToTicketShouldReturnOptionalEmptyWhenPriceNotExists() throws BookingException {
        // Given
        Set<SeatDto> seats = Set.of(SeatDto.of(5, 5), SeatDto.of(4, 5));
        Mockito.when(priceRepository.findByName(PRICE_ENTITY.getName()))
            .thenReturn(Optional.empty());
        // When
        Optional<Money> actual = underTest.bookSeatsToTicket(seats, TICKET_ENTITY, SCREENING_ENTITY);
        // Then
        Assertions.assertEquals(Optional.empty(),actual);
        Mockito.verify(priceRepository).findByName(PRICE_ENTITY.getName());
        Mockito.verifyNoMoreInteractions(seatRepository);
        Mockito.verifyNoMoreInteractions(priceRepository);
    }

    @Test
    public void testBookSeatsToTicketShouldCallSeatRepositoryWhenInputsSeatsNotValid() {
        // Given
        Set<SeatDto> seats = Set.of(SeatDto.of(1, 2), SeatDto.of(4, 5));
        Set<SeatEntity> seatEntities = Set.of(SeatEntity.builder()
            .id(new SeatId(1, 2))
            .ticketEntity(TICKET_ENTITY)
            .screeningEntity(TICKET_ENTITY.getScreeningEntity())
            .priceEntity(PRICE_ENTITY)
            .build(), SeatEntity.builder()
            .id(new SeatId(5, 5))
            .ticketEntity(TICKET_ENTITY)
            .priceEntity(PRICE_ENTITY)
            .screeningEntity(TICKET_ENTITY.getScreeningEntity())
            .build());
        Mockito.when(seatRepository.saveAll(seatEntities)).thenReturn(seatEntities);
        Mockito.when(priceRepository.findByName(PRICE_ENTITY.getName()))
            .thenReturn(Optional.of(PRICE_ENTITY));
        // When
        Assertions.assertThrows(BookingException.class,
            () -> underTest.bookSeatsToTicket(seats, TICKET_ENTITY, SCREENING_ENTITY));

    }
}
