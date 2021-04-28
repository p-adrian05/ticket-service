package com.training.epam.ticketservice.core.booking.impl;

import com.epam.training.ticketservice.core.booking.SeatService;
import com.epam.training.ticketservice.core.booking.exceptions.BookingException;
import com.epam.training.ticketservice.core.booking.impl.TicketServiceImpl;
import com.epam.training.ticketservice.core.booking.model.BookingDto;
import com.epam.training.ticketservice.core.booking.model.SeatDto;
import com.epam.training.ticketservice.core.booking.model.TicketDto;
import com.epam.training.ticketservice.core.booking.persistence.entity.SeatEntity;
import com.epam.training.ticketservice.core.booking.persistence.entity.SeatId;
import com.epam.training.ticketservice.core.booking.persistence.entity.TicketEntity;
import com.epam.training.ticketservice.core.booking.persistence.repository.TicketRepository;
import com.epam.training.ticketservice.core.movie.persistence.entity.GenreEntity;
import com.epam.training.ticketservice.core.movie.persistence.entity.MovieEntity;
import com.epam.training.ticketservice.core.price.persistence.entity.PriceEntity;
import com.epam.training.ticketservice.core.room.persistence.entity.RoomEntity;
import com.epam.training.ticketservice.core.screening.exceptions.ScreeningCreationException;
import com.epam.training.ticketservice.core.screening.model.BasicScreeningDto;
import com.epam.training.ticketservice.core.screening.persistence.entity.ScreeningEntity;
import com.epam.training.ticketservice.core.screening.persistence.repository.ScreeningRepository;
import com.epam.training.ticketservice.core.user.persistence.entity.UserEntity;
import com.epam.training.ticketservice.core.user.persistence.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class TicketServiceImplTest {


    private TicketServiceImpl underTest;
    private ScreeningRepository screeningRepository;
    private TicketRepository ticketRepository;
    private UserRepository userRepository;
    private SeatService seatService;

    private static final MovieEntity MOVIE_ENTITY = MovieEntity.builder()
        .id(1)
        .duration(100)
        .title("Test1 title")
        .moviePrices(new HashSet<>())
        .genreEntity(new GenreEntity(1, "Action")).build();

    private final static SeatEntity SEAT_ENTITY_1 = SeatEntity.builder()
        .id(new SeatId(1, 2))
        .build();
    private final static SeatEntity SEAT_ENTITY_2 = SeatEntity.builder()
        .id(new SeatId(3, 4))
        .build();
    private static final RoomEntity ROOM_ENTITY = RoomEntity.builder()
        .id(1)
        .columns(5)
        .name("A1")
        .roomPrices(new HashSet<>())
        .rows(5)
        .build();
    private final static ScreeningEntity SCREENING_ENTITY = ScreeningEntity
        .builder()
        .movieEntity(MOVIE_ENTITY)
        .roomEntity(ROOM_ENTITY)
        .seats(Set.of(SEAT_ENTITY_1, SEAT_ENTITY_2))
        .startTime(LocalDateTime.of(2021,4,28,15,12))
        .screeningPrices(new HashSet<>())
        .build();

    private final static PriceEntity PRICE_ENTITY = PriceEntity
        .builder()
        .value(1500)
        .name("Base")
        .currency("HUF")
        .build();

    private final static BookingDto BOOKING_DTO = BookingDto.builder()
        .screening(BasicScreeningDto.builder()
            .time(LocalDateTime.of(2021,4,28,15,12))
            .movieName("Test")
            .roomName("A1")
            .build())
        .seats(Set.of(SeatDto.of(1, 2), SeatDto.of(3, 4)))
        .build();

    private final static UserEntity USER_ENTITY = UserEntity.builder()
        .username("username")
        .role(UserEntity.Role.USER)
        .password("pass")
        .build();
    private static final TicketEntity TICKET_ENTITY = TicketEntity.builder()
        .id(null)
        .screeningEntity(SCREENING_ENTITY)
        .userEntity(USER_ENTITY)
        .price(1500)
        .build();

    @BeforeEach
    public void init() {
        screeningRepository = Mockito.mock(ScreeningRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        ticketRepository = Mockito.mock(TicketRepository.class);
        seatService = Mockito.mock(SeatService.class);
        underTest = new TicketServiceImpl(screeningRepository, userRepository, seatService, ticketRepository);
    }

    @Test
    public void testBookShouldCallSeatServiceAndTicketRepositoryWhenBookingInputIsValid() throws BookingException {
        // Given
        Mockito.when(screeningRepository
            .findByMovieEntity_TitleAndAndRoomEntity_NameAndStartTime(BOOKING_DTO.getScreening().getMovieName(),
                BOOKING_DTO.getScreening().getRoomName(), BOOKING_DTO.getScreening().getTime()))
            .thenReturn(Optional.of(SCREENING_ENTITY));
        Mockito.when(userRepository.findByUsername(USER_ENTITY.getUsername())).thenReturn(Optional.of(USER_ENTITY));
        Mockito.when(ticketRepository.save(TICKET_ENTITY)).thenReturn(TICKET_ENTITY);
        Mockito.when(seatService.calculateSeatPrice(BOOKING_DTO.getSeats())).thenReturn(TICKET_ENTITY.getPrice());
        // When
        TicketDto actual = underTest.book(BOOKING_DTO, USER_ENTITY.getUsername());

        // Then
        TicketDto expected = TicketDto.builder()
            .screening(BOOKING_DTO.getScreening())
            .username(USER_ENTITY.getUsername())
            .price((TICKET_ENTITY.getPrice())).seats(BOOKING_DTO.getSeats()).build();
        Assertions.assertEquals(expected,actual);
        Mockito.verify(screeningRepository)
            .findByMovieEntity_TitleAndAndRoomEntity_NameAndStartTime(BOOKING_DTO.getScreening().getMovieName(),
                BOOKING_DTO.getScreening().getRoomName(), BOOKING_DTO.getScreening().getTime());
        Mockito.verify(userRepository).findByUsername(USER_ENTITY.getUsername());
        Mockito.verify(ticketRepository).save(TICKET_ENTITY);
        Mockito.verify(ticketRepository).save(TICKET_ENTITY);
        Mockito.verifyNoMoreInteractions(screeningRepository);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verifyNoMoreInteractions(ticketRepository);
    }
    @Test
    public void testBookShouldThrowBookingExceptionWhenUserNotFound(){
        // Given
        Mockito.when(screeningRepository
            .findByMovieEntity_TitleAndAndRoomEntity_NameAndStartTime(BOOKING_DTO.getScreening().getMovieName(),
                BOOKING_DTO.getScreening().getRoomName(), BOOKING_DTO.getScreening().getTime()))
            .thenReturn(Optional.of(SCREENING_ENTITY));
        Mockito.when(userRepository.findByUsername(USER_ENTITY.getUsername())).thenReturn(Optional.empty());
        // When
        Assertions.assertThrows(BookingException.class, () -> underTest.book(BOOKING_DTO, USER_ENTITY.getUsername()));
        // Then
        Mockito.verify(screeningRepository)
            .findByMovieEntity_TitleAndAndRoomEntity_NameAndStartTime(BOOKING_DTO.getScreening().getMovieName(),
                BOOKING_DTO.getScreening().getRoomName(), BOOKING_DTO.getScreening().getTime());
        Mockito.verify(userRepository).findByUsername(USER_ENTITY.getUsername());
        Mockito.verifyNoMoreInteractions(screeningRepository);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verifyNoMoreInteractions(ticketRepository);
    }
    @Test
    public void testBookShouldThrowBookingExceptionWhenScreeningNotFound(){
        // Given
        Mockito.when(screeningRepository
            .findByMovieEntity_TitleAndAndRoomEntity_NameAndStartTime(BOOKING_DTO.getScreening().getMovieName(),
                BOOKING_DTO.getScreening().getRoomName(), BOOKING_DTO.getScreening().getTime()))
            .thenReturn(Optional.empty());
        // When
        Assertions.assertThrows(BookingException.class, () -> underTest.book(BOOKING_DTO, USER_ENTITY.getUsername()));
        // Then
        Mockito.verify(screeningRepository)
            .findByMovieEntity_TitleAndAndRoomEntity_NameAndStartTime(BOOKING_DTO.getScreening().getMovieName(),
                BOOKING_DTO.getScreening().getRoomName(), BOOKING_DTO.getScreening().getTime());
        Mockito.verifyNoMoreInteractions(screeningRepository);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verifyNoMoreInteractions(ticketRepository);
    }
    @Test
    public void testBookShouldThrowNullPointerExceptionWhenBookingDtoIsNull(){
        // When
        Assertions.assertThrows(NullPointerException.class, () -> underTest.book(null, USER_ENTITY.getUsername()));
    }
    @Test
    public void testBookShouldThrowNullPointerExceptionWhenUsernameIsNull(){
        // When
        Assertions.assertThrows(NullPointerException.class, () -> underTest.book(BOOKING_DTO, null));
    }
}
