package com.training.epam.ticketservice.core.booking.impl;

import com.epam.training.ticketservice.core.booking.SeatService;
import com.epam.training.ticketservice.core.booking.exceptions.BookingException;
import com.epam.training.ticketservice.core.booking.impl.TicketPriceServiceImpl;
import com.epam.training.ticketservice.core.booking.model.BookingDto;
import com.epam.training.ticketservice.core.booking.model.SeatDto;
import com.epam.training.ticketservice.core.booking.persistence.entity.SeatEntity;
import com.epam.training.ticketservice.core.booking.persistence.entity.SeatId;
import com.epam.training.ticketservice.core.booking.persistence.entity.TicketEntity;
import com.epam.training.ticketservice.core.finance.bank.Bank;
import com.epam.training.ticketservice.core.finance.bank.staticbank.impl.StaticBank;
import com.epam.training.ticketservice.core.finance.bank.staticbank.model.StaticExchangeRates;
import com.epam.training.ticketservice.core.finance.money.Money;
import com.epam.training.ticketservice.core.movie.persistence.entity.GenreEntity;
import com.epam.training.ticketservice.core.movie.persistence.entity.MovieEntity;
import com.epam.training.ticketservice.core.price.persistence.entity.PriceEntity;
import com.epam.training.ticketservice.core.room.persistence.entity.RoomEntity;
import com.epam.training.ticketservice.core.screening.model.BasicScreeningDto;
import com.epam.training.ticketservice.core.screening.persistence.entity.ScreeningEntity;
import com.epam.training.ticketservice.core.screening.persistence.repository.ScreeningRepository;
import com.epam.training.ticketservice.core.user.persistence.entity.UserEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Currency;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class TicketPriceServiceImplTest {

    private static final Currency HUF_CURRENCY = Currency.getInstance("HUF");
    private static final Currency USD_CURRENCY = Currency.getInstance("USD");
    private TicketPriceServiceImpl underTest;

    private ScreeningRepository screeningRepository;
    private SeatService seatService;
    private final Bank bank = StaticBank.of(() -> new StaticExchangeRates.Builder()
        .addRate(HUF_CURRENCY, USD_CURRENCY, 0.0034, 249.3)
        .build());

    private static final PriceEntity PRICE_ENTITY_EXTRA = PriceEntity
        .builder()
        .value(1000)
        .name("Extra")
        .currency("HUF")
        .build();
    private static final MovieEntity MOVIE_ENTITY = MovieEntity.builder()
        .id(1)
        .duration(100)
        .title("Test1 title")
        .moviePrices(Set.of(PRICE_ENTITY_EXTRA))
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
        .roomPrices(Set.of(PRICE_ENTITY_EXTRA))
        .rowsNumber(5)
        .build();
    private static final ScreeningEntity SCREENING_ENTITY = ScreeningEntity
        .builder()
        .movieEntity(MOVIE_ENTITY)
        .roomEntity(ROOM_ENTITY)
        .seats(Set.of(SEAT_ENTITY_1, SEAT_ENTITY_2))
        .startTime(LocalDateTime.of(2021, 4, 28, 15, 12))
        .screeningPrices(new HashSet<>())
        .build();
    private static final BookingDto BOOKING_DTO = BookingDto.builder()
        .screening(BasicScreeningDto.builder()
            .time(LocalDateTime.of(2021, 4, 28, 15, 12))
            .movieName("Test")
            .roomName("A1")
            .build())
        .seats(Set.of(SeatDto.of(1, 2), SeatDto.of(3, 4)))
        .build();

    private static final UserEntity USER_ENTITY = UserEntity.builder()
        .username("username")
        .role(UserEntity.Role.USER)
        .password("pass")
        .build();
    private static final TicketEntity TICKET_ENTITY = TicketEntity.builder()
        .id(null)
        .screeningEntity(SCREENING_ENTITY)
        .userEntity(USER_ENTITY)
        .price(7000.0)
        .currency("HUF")
        .build();

    @BeforeEach
    public void init() {
        screeningRepository = Mockito.mock(ScreeningRepository.class);
        seatService = Mockito.mock(SeatService.class);
        underTest = new TicketPriceServiceImpl(bank, screeningRepository, seatService);
    }

    @Test
    public void testCalculatePriceForBookingShouldCallSeatServiceAndReturnPriceWhenInputIsValid()
        throws BookingException {
        // Given
        Mockito.when(screeningRepository
            .findByMovieEntity_TitleAndAndRoomEntity_NameAndStartTime(BOOKING_DTO.getScreening().getMovieName(),
                BOOKING_DTO.getScreening().getRoomName(), BOOKING_DTO.getScreening().getTime()))
            .thenReturn(Optional.of(SCREENING_ENTITY));
        Mockito.when(seatService.calculateSeatPrice(BOOKING_DTO.getSeats()))
            .thenReturn(new Money(3000, Currency.getInstance("HUF")));
        Mockito.when(seatService.isFreeToSeat(BOOKING_DTO.getSeats(), SCREENING_ENTITY))
            .thenReturn(true);
        // When
        Optional<Money> actual = underTest.calculatePriceForBooking(BOOKING_DTO, Currency.getInstance("HUF"));

        // Then
        Assertions.assertEquals(TICKET_ENTITY.getPrice(), actual.get().getAmount());
        Assertions.assertEquals(TICKET_ENTITY.getCurrency(), actual.get().getCurrency().toString());
        Mockito.verify(screeningRepository)
            .findByMovieEntity_TitleAndAndRoomEntity_NameAndStartTime(BOOKING_DTO.getScreening().getMovieName(),
                BOOKING_DTO.getScreening().getRoomName(), BOOKING_DTO.getScreening().getTime());
        Mockito.verify(seatService).calculateSeatPrice(BOOKING_DTO.getSeats());
        Mockito.verify(seatService).isFreeToSeat(BOOKING_DTO.getSeats(), SCREENING_ENTITY);
        Mockito.verifyNoMoreInteractions(screeningRepository);
        Mockito.verifyNoMoreInteractions(seatService);
    }

    @Test
    public void testCalculatePriceForBookingShouldReturnEmptyOptionalPriceWhenIsFreeToSetReturnFalse()
        throws BookingException {
        // Given
        Mockito.when(screeningRepository
            .findByMovieEntity_TitleAndAndRoomEntity_NameAndStartTime(BOOKING_DTO.getScreening().getMovieName(),
                BOOKING_DTO.getScreening().getRoomName(), BOOKING_DTO.getScreening().getTime()))
            .thenReturn(Optional.of(SCREENING_ENTITY));
        Mockito.when(seatService.isFreeToSeat(BOOKING_DTO.getSeats(), SCREENING_ENTITY))
            .thenReturn(false);
        // When
        Optional<Money> actual = underTest.calculatePriceForBooking(BOOKING_DTO, Currency.getInstance("HUF"));

        // Then
        Assertions.assertEquals(Optional.empty(), actual);
        Mockito.verify(screeningRepository)
            .findByMovieEntity_TitleAndAndRoomEntity_NameAndStartTime(BOOKING_DTO.getScreening().getMovieName(),
                BOOKING_DTO.getScreening().getRoomName(), BOOKING_DTO.getScreening().getTime());
        Mockito.verify(seatService).isFreeToSeat(BOOKING_DTO.getSeats(), SCREENING_ENTITY);
        Mockito.verifyNoMoreInteractions(screeningRepository);
        Mockito.verifyNoMoreInteractions(seatService);
    }

    @Test
    public void testCalculatePriceForBookingShouldThrowBookingExceptionWhenSeatsNotFree() throws BookingException {
        // Given
        Mockito.when(screeningRepository
            .findByMovieEntity_TitleAndAndRoomEntity_NameAndStartTime(BOOKING_DTO.getScreening().getMovieName(),
                BOOKING_DTO.getScreening().getRoomName(), BOOKING_DTO.getScreening().getTime()))
            .thenReturn(Optional.of(SCREENING_ENTITY));
        Mockito.when(seatService.isFreeToSeat(BOOKING_DTO.getSeats(), SCREENING_ENTITY))
            .thenThrow(BookingException.class);
        // When
        Assertions.assertThrows(BookingException.class,
            () -> underTest.calculatePriceForBooking(BOOKING_DTO, Currency.getInstance("HUF")));

        // Then
        Mockito.verify(screeningRepository)
            .findByMovieEntity_TitleAndAndRoomEntity_NameAndStartTime(BOOKING_DTO.getScreening().getMovieName(),
                BOOKING_DTO.getScreening().getRoomName(), BOOKING_DTO.getScreening().getTime());
        Mockito.verify(seatService).isFreeToSeat(BOOKING_DTO.getSeats(), SCREENING_ENTITY);
        Mockito.verifyNoMoreInteractions(screeningRepository);
        Mockito.verifyNoMoreInteractions(seatService);
    }

    @Test
    public void testCalculatePriceForBookingShouldThrowBookingExceptionWhenScreeningNotFound() {
        // Given
        Mockito.when(screeningRepository
            .findByMovieEntity_TitleAndAndRoomEntity_NameAndStartTime(BOOKING_DTO.getScreening().getMovieName(),
                BOOKING_DTO.getScreening().getRoomName(), BOOKING_DTO.getScreening().getTime()))
            .thenReturn(Optional.empty());
        // When
        Assertions.assertThrows(BookingException.class,
            () -> underTest.calculatePriceForBooking(BOOKING_DTO, Currency.getInstance("HUF")));

        // Then
        Mockito.verify(screeningRepository)
            .findByMovieEntity_TitleAndAndRoomEntity_NameAndStartTime(BOOKING_DTO.getScreening().getMovieName(),
                BOOKING_DTO.getScreening().getRoomName(), BOOKING_DTO.getScreening().getTime());
        Mockito.verifyNoMoreInteractions(screeningRepository);
        Mockito.verifyNoMoreInteractions(seatService);
    }

    @Test
    public void testCalculatePriceForTicketShouldCallSeatServiceAndReturnTicketPriceWhenInputIsValid()
        throws BookingException {
        // Given
        Mockito.when(screeningRepository
            .findByMovieEntity_TitleAndAndRoomEntity_NameAndStartTime(BOOKING_DTO.getScreening().getMovieName(),
                BOOKING_DTO.getScreening().getRoomName(), BOOKING_DTO.getScreening().getTime()))
            .thenReturn(Optional.of(SCREENING_ENTITY));
        Mockito.when(seatService.bookSeatsToTicket(BOOKING_DTO.getSeats(), TICKET_ENTITY, SCREENING_ENTITY))
            .thenReturn(Optional.of(new Money(3000, HUF_CURRENCY)));
        // When
        Optional<Money> actual =
            underTest.calculatePriceForTicket(TICKET_ENTITY, BOOKING_DTO, Currency.getInstance("HUF"));

        // Then
        Assertions.assertEquals(TICKET_ENTITY.getPrice(), actual.get().getAmount());
        Assertions.assertEquals(TICKET_ENTITY.getCurrency(), actual.get().getCurrency().toString());
        Mockito.verify(screeningRepository)
            .findByMovieEntity_TitleAndAndRoomEntity_NameAndStartTime(BOOKING_DTO.getScreening().getMovieName(),
                BOOKING_DTO.getScreening().getRoomName(), BOOKING_DTO.getScreening().getTime());
        Mockito.verify(seatService).bookSeatsToTicket(BOOKING_DTO.getSeats(), TICKET_ENTITY, SCREENING_ENTITY);
        Mockito.verifyNoMoreInteractions(screeningRepository);
        Mockito.verifyNoMoreInteractions(seatService);
    }

    @Test
    public void testCalculatePriceForTicketShouldReturnEmptyTicketPriceWhenSeatServiceReturnEmptySeatPrice()
        throws BookingException {
        // Given
        Mockito.when(screeningRepository
            .findByMovieEntity_TitleAndAndRoomEntity_NameAndStartTime(BOOKING_DTO.getScreening().getMovieName(),
                BOOKING_DTO.getScreening().getRoomName(), BOOKING_DTO.getScreening().getTime()))
            .thenReturn(Optional.of(SCREENING_ENTITY));
        Mockito.when(seatService.bookSeatsToTicket(BOOKING_DTO.getSeats(), TICKET_ENTITY, SCREENING_ENTITY))
            .thenReturn(Optional.empty());
        // When
        Optional<Money> actual =
            underTest.calculatePriceForTicket(TICKET_ENTITY, BOOKING_DTO, Currency.getInstance("HUF"));

        // Then
        Assertions.assertEquals(Optional.empty(), actual);
        Mockito.verify(screeningRepository)
            .findByMovieEntity_TitleAndAndRoomEntity_NameAndStartTime(BOOKING_DTO.getScreening().getMovieName(),
                BOOKING_DTO.getScreening().getRoomName(), BOOKING_DTO.getScreening().getTime());
        Mockito.verify(seatService).bookSeatsToTicket(BOOKING_DTO.getSeats(), TICKET_ENTITY, SCREENING_ENTITY);
        Mockito.verifyNoMoreInteractions(screeningRepository);
        Mockito.verifyNoMoreInteractions(seatService);
    }

    @Test
    public void testCalculatePriceForTicketShouldThrowBookingExceptionWhenSeatServiceThrowsIt()
        throws BookingException {
        // Given
        Mockito.when(screeningRepository
            .findByMovieEntity_TitleAndAndRoomEntity_NameAndStartTime(BOOKING_DTO.getScreening().getMovieName(),
                BOOKING_DTO.getScreening().getRoomName(), BOOKING_DTO.getScreening().getTime()))
            .thenReturn(Optional.of(SCREENING_ENTITY));
        Mockito.when(seatService.bookSeatsToTicket(BOOKING_DTO.getSeats(), TICKET_ENTITY, SCREENING_ENTITY))
            .thenThrow(BookingException.class);
        // When
        Assertions.assertThrows(BookingException.class,
            () -> underTest.calculatePriceForTicket(TICKET_ENTITY, BOOKING_DTO, Currency.getInstance("HUF")));

        // Then
        Mockito.verify(screeningRepository)
            .findByMovieEntity_TitleAndAndRoomEntity_NameAndStartTime(BOOKING_DTO.getScreening().getMovieName(),
                BOOKING_DTO.getScreening().getRoomName(), BOOKING_DTO.getScreening().getTime());
        Mockito.verify(seatService).bookSeatsToTicket(BOOKING_DTO.getSeats(), TICKET_ENTITY, SCREENING_ENTITY);
        Mockito.verifyNoMoreInteractions(screeningRepository);
        Mockito.verifyNoMoreInteractions(seatService);
    }

    @Test
    public void testCalculatePriceForTicketShouldThrowBookingExceptionWhenScreeningNotFound() {
        // Given
        Mockito.when(screeningRepository
            .findByMovieEntity_TitleAndAndRoomEntity_NameAndStartTime(BOOKING_DTO.getScreening().getMovieName(),
                BOOKING_DTO.getScreening().getRoomName(), BOOKING_DTO.getScreening().getTime()))
            .thenReturn(Optional.empty());
        // When
        Assertions.assertThrows(BookingException.class,
            () -> underTest.calculatePriceForTicket(TICKET_ENTITY, BOOKING_DTO, Currency.getInstance("HUF")));

        // Then
        Mockito.verify(screeningRepository)
            .findByMovieEntity_TitleAndAndRoomEntity_NameAndStartTime(BOOKING_DTO.getScreening().getMovieName(),
                BOOKING_DTO.getScreening().getRoomName(), BOOKING_DTO.getScreening().getTime());
        Mockito.verifyNoMoreInteractions(screeningRepository);
        Mockito.verifyNoMoreInteractions(seatService);
    }
}
