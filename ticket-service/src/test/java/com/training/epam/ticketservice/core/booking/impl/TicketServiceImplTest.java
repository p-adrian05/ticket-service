package com.training.epam.ticketservice.core.booking.impl;

import com.epam.training.ticketservice.core.booking.TicketPriceCalculator;
import com.epam.training.ticketservice.core.booking.exceptions.BookingException;
import com.epam.training.ticketservice.core.booking.impl.TicketServiceImpl;
import com.epam.training.ticketservice.core.booking.model.BookingDto;
import com.epam.training.ticketservice.core.booking.model.SeatDto;
import com.epam.training.ticketservice.core.booking.model.TicketDto;
import com.epam.training.ticketservice.core.booking.persistence.entity.SeatEntity;
import com.epam.training.ticketservice.core.booking.persistence.entity.SeatId;
import com.epam.training.ticketservice.core.booking.persistence.entity.TicketEntity;
import com.epam.training.ticketservice.core.booking.persistence.repository.TicketRepository;
import com.epam.training.ticketservice.core.finance.money.Money;
import com.epam.training.ticketservice.core.movie.persistence.entity.GenreEntity;
import com.epam.training.ticketservice.core.movie.persistence.entity.MovieEntity;
import com.epam.training.ticketservice.core.room.persistence.entity.RoomEntity;
import com.epam.training.ticketservice.core.screening.model.BasicScreeningDto;
import com.epam.training.ticketservice.core.screening.persistence.entity.ScreeningEntity;
import com.epam.training.ticketservice.core.user.persistence.entity.UserEntity;
import com.epam.training.ticketservice.core.user.persistence.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class TicketServiceImplTest {

    private TicketServiceImpl underTest;
    private UserRepository userRepository;
    private TicketPriceCalculator ticketPriceCalculator;
    private TicketRepository ticketRepository;

    private static final Currency HUF_CURRENCY = Currency.getInstance("HUF");

    private static final BookingDto BOOKING_DTO = BookingDto.builder()
        .screening(BasicScreeningDto.builder()
            .time(LocalDateTime.of(2021, 4, 28, 15, 12))
            .movieName("Test")
            .roomName("A1")
            .build())
        .seats(Set.of(SeatDto.of(1, 2), SeatDto.of(3, 4)))
        .build();
    private static final RoomEntity ROOM_ENTITY = RoomEntity.builder()
        .id(1)
        .columns(10)
        .name("A1")
        .rows(15)
        .build();
    private static final SeatEntity SEAT_ENTITY_1 = SeatEntity.builder()
        .id(new SeatId(1, 2))
        .build();
    private static final SeatEntity SEAT_ENTITY_2 = SeatEntity.builder()
        .id(new SeatId(3, 4))
        .build();
    private static final MovieEntity MOVIE_ENTITY = MovieEntity.builder()
        .id(1)
        .duration(100)
        .title("Test")
        .genreEntity(new GenreEntity(null, "Action")).build();

    private static final ScreeningEntity SCREENING_ENTITY = ScreeningEntity.builder()
        .movieEntity(MOVIE_ENTITY)
        .roomEntity(ROOM_ENTITY)
        .id(1)
        .startTime(LocalDateTime.of(2021, 4, 28, 15, 12))
        .endTime(LocalDateTime.of(2021, 4, 22, 11, 30))
        .build();
    private static final UserEntity USER_ENTITY = UserEntity.builder()
        .username("username")
        .role(UserEntity.Role.USER)
        .build();
    private static final TicketEntity TICKET_ENTITY = TicketEntity.builder()
        .id(null)
        .userEntity(USER_ENTITY)
        .price(5000.0)
        .currency("HUF")
        .screeningEntity(SCREENING_ENTITY)
        .seats(Set.of(SEAT_ENTITY_1, SEAT_ENTITY_2))
        .build();
    private static final TicketDto TICKET_DTO = TicketDto.builder()
        .username(USER_ENTITY.getUsername())
        .seats(BOOKING_DTO.getSeats())
        .screening(BOOKING_DTO.getScreening())
        .price(new Money(5000, HUF_CURRENCY))
        .build();

    private static final List<TicketDto> TICKET_DTO_LIST = List.of(TICKET_DTO);
    private static final Set<TicketEntity> TICKET_ENTITIES = Set.of(TICKET_ENTITY);


    @BeforeEach
    public void init() {
        ticketRepository = Mockito.mock(TicketRepository.class);
        ticketPriceCalculator = Mockito.mock(TicketPriceCalculator.class);
        userRepository = Mockito.mock(UserRepository.class);
        underTest = new TicketServiceImpl(userRepository, ticketPriceCalculator, ticketRepository);
    }

    @Test
    public void testBookShouldCallTicketPriceServiceAndReturnCratedTicketDtoWithPriceWhenInputIsValid()
        throws BookingException {
        // Given
        final TicketEntity newTicket = TicketEntity.builder()
            .id(null)
            .userEntity(USER_ENTITY)
            .build();
        final TicketEntity createdTicket = TicketEntity.builder()
            .id(null)
            .userEntity(USER_ENTITY)
            .build();
        Mockito.when(userRepository.findByUsername(USER_ENTITY.getUsername())).thenReturn(Optional.of(USER_ENTITY));
        Mockito.when(ticketRepository.save(newTicket)).thenReturn(newTicket);
        Mockito.when(ticketPriceCalculator.calculatePriceForTicket(newTicket, BOOKING_DTO, HUF_CURRENCY))
            .thenReturn(Optional.of(new Money(5000, HUF_CURRENCY)));
        // When
        TicketDto actual = underTest.book(BOOKING_DTO, USER_ENTITY.getUsername(), "HUF");

        // Then
        Assertions.assertEquals(TICKET_DTO, actual);
        Mockito.verify(userRepository).findByUsername(USER_ENTITY.getUsername());
        Mockito.verify(ticketPriceCalculator).calculatePriceForTicket(createdTicket, BOOKING_DTO, HUF_CURRENCY);
        Mockito.verify(ticketRepository).save(createdTicket);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verifyNoMoreInteractions(ticketRepository);
        Mockito.verifyNoMoreInteractions(ticketPriceCalculator);
    }

    @Test
    public void testBookShouldThrowBookingExceptionWhenUserRepositoryReturnEmptyUser() {
        // Given
        Mockito.when(userRepository.findByUsername(USER_ENTITY.getUsername())).thenReturn(Optional.empty());
        // When
        Assertions
            .assertThrows(BookingException.class, () -> underTest.book(BOOKING_DTO, USER_ENTITY.getUsername(), "HUF"));
        // Then
        Mockito.verify(userRepository).findByUsername(USER_ENTITY.getUsername());
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verifyNoMoreInteractions(ticketRepository);
        Mockito.verifyNoMoreInteractions(ticketPriceCalculator);
    }

    @Test
    public void testBookShouldThrowBookingExceptionWhenTicketPriceCalculatorThrowBookingException()
        throws BookingException {
        // Given
        final TicketEntity newTicket = TicketEntity.builder()
            .id(null)
            .userEntity(USER_ENTITY)
            .build();
        final TicketEntity createdTicket = TicketEntity.builder()
            .id(null)
            .userEntity(USER_ENTITY)
            .build();
        Mockito.when(userRepository.findByUsername(USER_ENTITY.getUsername())).thenReturn(Optional.of(USER_ENTITY));
        Mockito.when(ticketRepository.save(newTicket)).thenReturn(newTicket);
        Mockito.when(ticketPriceCalculator.calculatePriceForTicket(createdTicket, BOOKING_DTO, HUF_CURRENCY))
            .thenThrow(BookingException.class);
        // When
        Assertions
            .assertThrows(BookingException.class, () -> underTest.book(BOOKING_DTO, USER_ENTITY.getUsername(), "HUF"));
        // Then
        Mockito.verify(userRepository).findByUsername(USER_ENTITY.getUsername());
        Mockito.verify(ticketPriceCalculator).calculatePriceForTicket(createdTicket, BOOKING_DTO, HUF_CURRENCY);
        Mockito.verify(ticketRepository).save(createdTicket);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verifyNoMoreInteractions(ticketRepository);
        Mockito.verifyNoMoreInteractions(ticketPriceCalculator);
    }

    @Test
    public void testBookShouldThrowBookingExceptionWhenTicketPriceCalculatorReturnEmptyTicketPrice()
        throws BookingException {
        // Given
        final TicketEntity newTicket = TicketEntity.builder()
            .id(null)
            .userEntity(USER_ENTITY)
            .build();
        final TicketEntity createdTicket = TicketEntity.builder()
            .id(null)
            .userEntity(USER_ENTITY)
            .build();
        Mockito.when(userRepository.findByUsername(USER_ENTITY.getUsername())).thenReturn(Optional.of(USER_ENTITY));
        Mockito.when(ticketRepository.save(newTicket)).thenReturn(newTicket);
        Mockito.when(ticketPriceCalculator.calculatePriceForTicket(createdTicket, BOOKING_DTO, HUF_CURRENCY))
            .thenReturn(Optional.empty());
        // When
        Assertions
            .assertThrows(BookingException.class, () -> underTest.book(BOOKING_DTO, USER_ENTITY.getUsername(), "HUF"));
        // Then
        Mockito.verify(userRepository).findByUsername(USER_ENTITY.getUsername());
        Mockito.verify(ticketPriceCalculator).calculatePriceForTicket(createdTicket, BOOKING_DTO, HUF_CURRENCY);
        Mockito.verify(ticketRepository).save(createdTicket);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verifyNoMoreInteractions(ticketRepository);
        Mockito.verifyNoMoreInteractions(ticketPriceCalculator);
    }

    @Test
    public void testShowPriceShouldCallTicketPriceCalculatorAndReturnOptionalMoneyWhenInputIsValid()
        throws BookingException {
        // Given
        Money money = new Money(5000, HUF_CURRENCY);
        Mockito.when(ticketPriceCalculator.calculatePriceForBooking(BOOKING_DTO, HUF_CURRENCY))
            .thenReturn(Optional.of(money));

        // When
        Optional<Money> actual = underTest.showPrice(BOOKING_DTO, "HUF");
        // Then
        Assertions.assertEquals(Optional.of(money), actual);
        Mockito.verify(ticketPriceCalculator).calculatePriceForBooking(BOOKING_DTO, HUF_CURRENCY);
        Mockito.verifyNoMoreInteractions(ticketPriceCalculator);
    }

    @Test
    public void testShowPriceShouldThrowBookingExceptionWhenTicketPriceCalculatorThrows() throws BookingException {
        // Given
        Mockito.when(ticketPriceCalculator.calculatePriceForBooking(BOOKING_DTO, HUF_CURRENCY))
            .thenThrow(BookingException.class);

        // When
        Assertions
            .assertThrows(BookingException.class, () -> underTest.showPrice(BOOKING_DTO, "HUF"));

        // Then
        Mockito.verify(ticketPriceCalculator).calculatePriceForBooking(BOOKING_DTO, HUF_CURRENCY);
        Mockito.verifyNoMoreInteractions(ticketPriceCalculator);
    }

    @Test
    public void testGetTicketsByUsernameShouldTicketRepositoryAndReturnAListOfTicketDtos() {
        // Given
        Mockito.when(ticketRepository.findTicketEntitiesByUserEntityUsername(USER_ENTITY.getUsername()))
            .thenReturn(TICKET_ENTITIES);

        // When
        List<TicketDto> ticketDtos = underTest.getTicketsByUsername(USER_ENTITY.getUsername());
        // Then
        Assertions.assertEquals(ticketDtos, TICKET_DTO_LIST);
        Mockito.verify(ticketRepository).findTicketEntitiesByUserEntityUsername(USER_ENTITY.getUsername());
        Mockito.verifyNoMoreInteractions(ticketRepository);
    }
}
