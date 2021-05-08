package com.training.epam.ticketservice.core.price.impl;


import com.epam.training.ticketservice.core.movie.persistence.entity.GenreEntity;
import com.epam.training.ticketservice.core.movie.persistence.entity.MovieEntity;
import com.epam.training.ticketservice.core.movie.persistence.repository.MovieRepository;
import com.epam.training.ticketservice.core.price.exceptions.AttachPriceException;
import com.epam.training.ticketservice.core.price.exceptions.UnknownPriceException;
import com.epam.training.ticketservice.core.price.impl.AttachPriceServiceImpl;
import com.epam.training.ticketservice.core.price.model.PriceDto;
import com.epam.training.ticketservice.core.price.persistence.entity.PriceEntity;
import com.epam.training.ticketservice.core.price.persistence.repository.PriceRepository;
import com.epam.training.ticketservice.core.room.persistence.entity.RoomEntity;
import com.epam.training.ticketservice.core.room.persistence.repository.RoomRepository;
import com.epam.training.ticketservice.core.screening.model.BasicScreeningDto;
import com.epam.training.ticketservice.core.screening.persistence.entity.ScreeningEntity;
import com.epam.training.ticketservice.core.screening.persistence.repository.ScreeningRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Currency;
import java.util.HashSet;
import java.util.Optional;


public class AttachPriceServiceImplTest {

    private AttachPriceServiceImpl underTest;
    private RoomRepository roomRepository;
    private ScreeningRepository screeningRepository;
    private MovieRepository movieRepository;
    private PriceRepository priceRepository;

    private static final RoomEntity ROOM_ENTITY = RoomEntity.builder()
        .id(1)
        .columns(10)
        .name("A1")
        .roomPrices(new HashSet<>())
        .rowsNumber(15)
        .build();
    private static final MovieEntity MOVIE_ENTITY = MovieEntity.builder()
        .id(1)
        .duration(100)
        .title("Test1 title")
        .moviePrices(new HashSet<>())
        .genreEntity(new GenreEntity(null, "Action")).build();
    public static final BasicScreeningDto SCREENING_DTO = BasicScreeningDto.builder()
        .movieName("TEST")
        .roomName("A1")
        .time(LocalDateTime.of(2021, 4, 22, 10, 30))
        .build();
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
    private static final ScreeningEntity SCREENING_ENTITY = ScreeningEntity.builder()
        .movieEntity(MOVIE_ENTITY)
        .roomEntity(ROOM_ENTITY)
        .screeningPrices(new HashSet<>())
        .id(1)
        .startTime(LocalDateTime.of(2021, 4, 22, 10, 30))
        .endTime(LocalDateTime.of(2021, 4, 22, 11, 30))
        .build();

    @BeforeEach
    public void init() {
        movieRepository = Mockito.mock(MovieRepository.class);
        roomRepository = Mockito.mock(RoomRepository.class);
        priceRepository = Mockito.mock(PriceRepository.class);
        screeningRepository = Mockito.mock(ScreeningRepository.class);
        underTest = new AttachPriceServiceImpl(priceRepository, roomRepository, screeningRepository, movieRepository);
    }

    @Test
    public void testAttachRoomShouldCallRoomRepositoryWhenInputIsValid()
        throws UnknownPriceException, AttachPriceException {
        // Given
        Mockito.when(roomRepository.findByName(ROOM_ENTITY.getName())).thenReturn(Optional.of(ROOM_ENTITY));
        Mockito.when(priceRepository.findByName(PRICE_DTO.getName())).thenReturn(Optional.of(PRICE_ENTITY));
        Mockito.when(roomRepository.save(ROOM_ENTITY)).thenReturn(ROOM_ENTITY);
        // When
        underTest.attachRoom(ROOM_ENTITY.getName(), PRICE_ENTITY.getName());
        // Then
        Assertions.assertTrue(ROOM_ENTITY.getRoomPrices().contains(PRICE_ENTITY));
        Mockito.verify(priceRepository).findByName(PRICE_DTO.getName());
        Mockito.verify(roomRepository).findByName(ROOM_ENTITY.getName());
        Mockito.verify(roomRepository).save(ROOM_ENTITY);
        Mockito.verifyNoMoreInteractions(priceRepository);
        Mockito.verifyNoMoreInteractions(roomRepository);
    }

    @Test
    public void testAttachRoomShouldThrowAttachPriceExceptionWhenRoomNameNotExists() {
        // Given
        Mockito.when(roomRepository.findByName(ROOM_ENTITY.getName())).thenReturn(Optional.empty());
        // When
        Assertions.assertThrows(AttachPriceException.class,
            () -> underTest.attachRoom(ROOM_ENTITY.getName(), PRICE_ENTITY.getName()));

        // Then
        Mockito.verify(roomRepository).findByName(ROOM_ENTITY.getName());
        Mockito.verifyNoMoreInteractions(priceRepository);
        Mockito.verifyNoMoreInteractions(roomRepository);
    }

    @Test
    public void testAttachRoomShouldThrowUnknownPriceExceptionWhenPriceNameNotExists() {
        // Given
        Mockito.when(roomRepository.findByName(ROOM_ENTITY.getName())).thenReturn(Optional.of(ROOM_ENTITY));
        Mockito.when(priceRepository.findByName(PRICE_DTO.getName())).thenReturn(Optional.empty());
        // When
        Assertions.assertThrows(UnknownPriceException.class,
            () -> underTest.attachRoom(ROOM_ENTITY.getName(), PRICE_ENTITY.getName()));

        // Then
        Mockito.verify(priceRepository).findByName(PRICE_DTO.getName());
        Mockito.verify(roomRepository).findByName(ROOM_ENTITY.getName());
        Mockito.verifyNoMoreInteractions(priceRepository);
        Mockito.verifyNoMoreInteractions(roomRepository);
    }

    @Test
    public void testAttachMovieShouldCallMovieRepositoryWhenInputIsValid()
        throws UnknownPriceException, AttachPriceException {
        // Given
        Mockito.when(movieRepository.findMovieEntityByTitle(MOVIE_ENTITY.getTitle()))
            .thenReturn(Optional.of(MOVIE_ENTITY));
        Mockito.when(priceRepository.findByName(PRICE_DTO.getName())).thenReturn(Optional.of(PRICE_ENTITY));
        Mockito.when(movieRepository.save(MOVIE_ENTITY)).thenReturn(MOVIE_ENTITY);
        // When
        underTest.attachMovie(MOVIE_ENTITY.getTitle(), PRICE_ENTITY.getName());
        // Then
        Assertions.assertTrue(MOVIE_ENTITY.getMoviePrices().contains(PRICE_ENTITY));
        Mockito.verify(priceRepository).findByName(PRICE_DTO.getName());
        Mockito.verify(movieRepository).findMovieEntityByTitle(MOVIE_ENTITY.getTitle());
        Mockito.verify(movieRepository).save(MOVIE_ENTITY);
        Mockito.verifyNoMoreInteractions(priceRepository);
        Mockito.verifyNoMoreInteractions(movieRepository);
    }

    @Test
    public void testAttachMovieShouldThrowAttachPriceExceptionWhenMovieTitleNotExists() {
        // Given
        Mockito.when(movieRepository.findMovieEntityByTitle(MOVIE_ENTITY.getTitle()))
            .thenReturn(Optional.empty());
        // When
        Assertions.assertThrows(AttachPriceException.class,
            () -> underTest.attachMovie(MOVIE_ENTITY.getTitle(), PRICE_ENTITY.getName()));

        // Then
        Mockito.verify(movieRepository).findMovieEntityByTitle(MOVIE_ENTITY.getTitle());
        Mockito.verifyNoMoreInteractions(priceRepository);
        Mockito.verifyNoMoreInteractions(movieRepository);
    }

    @Test
    public void testAttachMovieShouldThrowUnknownPriceExceptionWhenPriceNameNotExists() {
        // Given
        Mockito.when(movieRepository.findMovieEntityByTitle(MOVIE_ENTITY.getTitle()))
            .thenReturn(Optional.of(MOVIE_ENTITY));
        Mockito.when(priceRepository.findByName(PRICE_DTO.getName())).thenReturn(Optional.empty());
        // When
        Assertions.assertThrows(UnknownPriceException.class,
            () -> underTest.attachMovie(MOVIE_ENTITY.getTitle(), PRICE_ENTITY.getName()));

        // Then
        Mockito.verify(priceRepository).findByName(PRICE_DTO.getName());
        Mockito.verify(movieRepository).findMovieEntityByTitle(MOVIE_ENTITY.getTitle());
        Mockito.verifyNoMoreInteractions(priceRepository);
        Mockito.verifyNoMoreInteractions(roomRepository);
    }

    @Test
    public void testAttachScreeningShouldCallScreeningRepositoryWhenInputIsValid()
        throws UnknownPriceException, AttachPriceException {
        // Given
        Mockito.when(screeningRepository.findByMovieEntity_TitleAndAndRoomEntity_NameAndStartTime(
           SCREENING_DTO.getMovieName(),SCREENING_DTO.getRoomName(),SCREENING_DTO.getTime()))
            .thenReturn(Optional.of(SCREENING_ENTITY));
        Mockito.when(priceRepository.findByName(PRICE_DTO.getName())).thenReturn(Optional.of(PRICE_ENTITY));
        Mockito.when(screeningRepository.save(SCREENING_ENTITY)).thenReturn(SCREENING_ENTITY);
        // When
        underTest.attachScreening(SCREENING_DTO, PRICE_ENTITY.getName());
        // Then
        Assertions.assertTrue(SCREENING_ENTITY.getScreeningPrices().contains(PRICE_ENTITY));
        Mockito.verify(priceRepository).findByName(PRICE_DTO.getName());
        Mockito.verify(screeningRepository).findByMovieEntity_TitleAndAndRoomEntity_NameAndStartTime(
            SCREENING_DTO.getMovieName(),SCREENING_DTO.getRoomName(),SCREENING_DTO.getTime());
        Mockito.verify(screeningRepository).save(SCREENING_ENTITY);
        Mockito.verifyNoMoreInteractions(priceRepository);
        Mockito.verifyNoMoreInteractions(screeningRepository);
    }

    @Test
    public void testAttachScreeningShouldThrowAttachPriceExceptionWhenScreeningNotExists() {
        // Given
        Mockito.when(screeningRepository.findByMovieEntity_TitleAndAndRoomEntity_NameAndStartTime(
            SCREENING_DTO.getMovieName(),SCREENING_DTO.getRoomName(),SCREENING_DTO.getTime()))
            .thenReturn(Optional.empty());
        // When
        Assertions.assertThrows(AttachPriceException.class,
            () -> underTest.attachScreening(SCREENING_DTO, PRICE_ENTITY.getName()));

        // Then
        Mockito.verify(screeningRepository).findByMovieEntity_TitleAndAndRoomEntity_NameAndStartTime(
            SCREENING_DTO.getMovieName(),SCREENING_DTO.getRoomName(),SCREENING_DTO.getTime());
        Mockito.verifyNoMoreInteractions(priceRepository);
        Mockito.verifyNoMoreInteractions(screeningRepository);
    }

    @Test
    public void testAttachScreeningShouldThrowUnknownPriceExceptionWhenPriceNameNotExists() {
        // Given
        Mockito.when(screeningRepository.findByMovieEntity_TitleAndAndRoomEntity_NameAndStartTime(
            SCREENING_DTO.getMovieName(),SCREENING_DTO.getRoomName(),SCREENING_DTO.getTime()))
            .thenReturn(Optional.of(SCREENING_ENTITY));
        Mockito.when(priceRepository.findByName(PRICE_DTO.getName())).thenReturn(Optional.empty());
        // When
        Assertions.assertThrows(UnknownPriceException.class,
            () -> underTest.attachScreening(SCREENING_DTO, PRICE_ENTITY.getName()));

        // Then
        Mockito.verify(priceRepository).findByName(PRICE_DTO.getName());
        Mockito.verify(screeningRepository).findByMovieEntity_TitleAndAndRoomEntity_NameAndStartTime(
            SCREENING_DTO.getMovieName(),SCREENING_DTO.getRoomName(),SCREENING_DTO.getTime());
        Mockito.verifyNoMoreInteractions(priceRepository);
        Mockito.verifyNoMoreInteractions(screeningRepository);
    }

}
