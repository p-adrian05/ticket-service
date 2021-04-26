package com.training.epam.ticketservice.core.price.impl;


import com.epam.training.ticketservice.core.movie.persistence.entity.GenreEntity;
import com.epam.training.ticketservice.core.movie.persistence.entity.MovieEntity;
import com.epam.training.ticketservice.core.movie.persistence.repository.MovieRepository;
import com.epam.training.ticketservice.core.price.exceptions.AttachPriceException;
import com.epam.training.ticketservice.core.price.exceptions.PriceAlreadyExistsException;
import com.epam.training.ticketservice.core.price.exceptions.UnknownPriceException;
import com.epam.training.ticketservice.core.price.impl.PriceServiceImpl;
import com.epam.training.ticketservice.core.price.model.PriceDto;
import com.epam.training.ticketservice.core.price.persistence.entity.PriceEntity;
import com.epam.training.ticketservice.core.price.persistence.repository.PriceRepository;
import com.epam.training.ticketservice.core.room.persistence.entity.RoomEntity;
import com.epam.training.ticketservice.core.room.persistence.repository.RoomRepository;
import com.epam.training.ticketservice.core.screening.model.CreateScreeningDto;

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


public class PriceServiceImplTest {

    private PriceServiceImpl underTest;
    private RoomRepository roomRepository;
    private ScreeningRepository screeningRepository;
    private MovieRepository movieRepository;
    private PriceRepository priceRepository;

    private static final RoomEntity ROOM_ENTITY = RoomEntity.builder()
        .id(1)
        .columns(10)
        .name("A1")
        .rows(15)
        .build();
    private static final MovieEntity MOVIE_ENTITY = MovieEntity.builder()
        .id(1)
        .duration(100)
        .title("Test1 title")
        .genreEntity(new GenreEntity(null, "Action")).build();
    public static final CreateScreeningDto SCREENING_DTO = CreateScreeningDto.builder()
        .movieName("TEST")
        .roomName("A1")
        .time(LocalDateTime.of(2021, 4, 22, 10, 30))
        .build();
    private static final PriceEntity PRICE_ENTITY = PriceEntity.builder()
        .id(null)
        .name("Base")
        .currency("HUF")
        .value(1500)
        .rooms(new HashSet<>())
        .movies(new HashSet<>())
        .screenings(new HashSet<>())
        .build();
    private static final PriceDto PRICE_DTO = PriceDto.builder()
        .name("Base")
        .currency(Currency.getInstance("HUF"))
        .value(1500)
        .build();
    private static final ScreeningEntity SCREENING_ENTITY = ScreeningEntity.builder()
        .movieEntity(MOVIE_ENTITY)
        .roomEntity(ROOM_ENTITY)
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
        underTest = new PriceServiceImpl(priceRepository, roomRepository, screeningRepository, movieRepository);
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

    @Test
    public void testAttachRoomShouldCallPriceRepositoryWhenRoomNameAndPriceNameAreValid()
        throws UnknownPriceException, AttachPriceException {
        // Given
        Mockito.when(roomRepository.findByName(ROOM_ENTITY.getName())).thenReturn(Optional.of(ROOM_ENTITY));
        Mockito.when(priceRepository.findByName(PRICE_DTO.getName())).thenReturn(Optional.of(PRICE_ENTITY));
        Mockito.when(priceRepository.save(PRICE_ENTITY)).thenReturn(PRICE_ENTITY);
        // When
        underTest.attachRoom(ROOM_ENTITY.getName(), PRICE_ENTITY.getName());
        // Then
        Assertions.assertTrue(PRICE_ENTITY.getRooms().contains(ROOM_ENTITY));
        Mockito.verify(priceRepository).findByName(PRICE_DTO.getName());
        Mockito.verify(roomRepository).findByName(ROOM_ENTITY.getName());
        Mockito.verify(priceRepository).save(PRICE_ENTITY);
        Mockito.verifyNoMoreInteractions(priceRepository);
        Mockito.verifyNoMoreInteractions(roomRepository);
    }

    @Test
    public void testAttachRoomShouldThrowAttachPriceExceptionWhenRoomNameNotExists() {
        // Given
        Mockito.when(roomRepository.findByName(ROOM_ENTITY.getName())).thenReturn(Optional.empty());
        Mockito.when(priceRepository.findByName(PRICE_DTO.getName())).thenReturn(Optional.of(PRICE_ENTITY));
        // When
        Assertions.assertThrows(AttachPriceException.class,
            () -> underTest.attachRoom(ROOM_ENTITY.getName(), PRICE_ENTITY.getName()));

        // Then
        Mockito.verify(priceRepository).findByName(PRICE_DTO.getName());
        Mockito.verify(roomRepository).findByName(ROOM_ENTITY.getName());
        Mockito.verifyNoMoreInteractions(priceRepository);
        Mockito.verifyNoMoreInteractions(roomRepository);
    }

    @Test
    public void testAttachRoomShouldThrowUnknownPriceExceptionWhenPriceNameNotExists() {
        // Given
        Mockito.when(priceRepository.findByName(PRICE_DTO.getName())).thenReturn(Optional.empty());
        // When
        Assertions.assertThrows(UnknownPriceException.class,
            () -> underTest.attachRoom(ROOM_ENTITY.getName(), PRICE_ENTITY.getName()));

        // Then
        Mockito.verify(priceRepository).findByName(PRICE_DTO.getName());
        Mockito.verifyNoMoreInteractions(priceRepository);
        Mockito.verifyNoMoreInteractions(roomRepository);
    }

    @Test
    public void testAttachMovieShouldCallPriceRepositoryWhenMovieTitleAndPriceNameAreValid()
        throws UnknownPriceException, AttachPriceException {
        // Given
        Mockito.when(movieRepository.findMovieEntityByTitle(MOVIE_ENTITY.getTitle()))
            .thenReturn(Optional.of(MOVIE_ENTITY));
        Mockito.when(priceRepository.findByName(PRICE_DTO.getName())).thenReturn(Optional.of(PRICE_ENTITY));
        Mockito.when(priceRepository.save(PRICE_ENTITY)).thenReturn(PRICE_ENTITY);
        // When
        underTest.attachMovie(MOVIE_ENTITY.getTitle(), PRICE_ENTITY.getName());
        // Then
        Assertions.assertTrue(PRICE_ENTITY.getMovies().contains(MOVIE_ENTITY));
        Mockito.verify(priceRepository).findByName(PRICE_DTO.getName());
        Mockito.verify(movieRepository).findMovieEntityByTitle(MOVIE_ENTITY.getTitle());
        Mockito.verify(priceRepository).save(PRICE_ENTITY);
        Mockito.verifyNoMoreInteractions(priceRepository);
        Mockito.verifyNoMoreInteractions(movieRepository);
    }

    @Test
    public void testAttachMovieShouldThrowAttachPriceExceptionWhenMovieTitleNotExists() {
        // Given
        Mockito.when(movieRepository.findMovieEntityByTitle(MOVIE_ENTITY.getTitle()))
            .thenReturn(Optional.empty());
        Mockito.when(priceRepository.findByName(PRICE_DTO.getName())).thenReturn(Optional.of(PRICE_ENTITY));
        // When
        Assertions.assertThrows(AttachPriceException.class,
            () -> underTest.attachMovie(MOVIE_ENTITY.getTitle(), PRICE_ENTITY.getName()));

        // Then
        Mockito.verify(priceRepository).findByName(PRICE_DTO.getName());
        Mockito.verify(movieRepository).findMovieEntityByTitle(MOVIE_ENTITY.getTitle());
        Mockito.verifyNoMoreInteractions(priceRepository);
        Mockito.verifyNoMoreInteractions(movieRepository);
    }

    @Test
    public void testAttachMovieShouldThrowUnknownPriceExceptionWhenPriceNameNotExists() {
        // Given
        Mockito.when(priceRepository.findByName(PRICE_DTO.getName())).thenReturn(Optional.empty());
        // When
        Assertions.assertThrows(UnknownPriceException.class,
            () -> underTest.attachMovie(MOVIE_ENTITY.getTitle(), PRICE_ENTITY.getName()));

        // Then
        Mockito.verify(priceRepository).findByName(PRICE_DTO.getName());
        Mockito.verifyNoMoreInteractions(priceRepository);
        Mockito.verifyNoMoreInteractions(roomRepository);
    }

    @Test
    public void testAttachScreeningShouldCallPriceRepositoryWhenScreeningAndPriceNameAreValid()
        throws UnknownPriceException, AttachPriceException {
        // Given
        Mockito.when(screeningRepository.findByMovieEntity_TitleAndAndRoomEntity_NameAndStartTime(
           SCREENING_DTO.getMovieName(),SCREENING_DTO.getRoomName(),SCREENING_DTO.getTime()))
            .thenReturn(Optional.of(SCREENING_ENTITY));
        Mockito.when(priceRepository.findByName(PRICE_DTO.getName())).thenReturn(Optional.of(PRICE_ENTITY));
        Mockito.when(priceRepository.save(PRICE_ENTITY)).thenReturn(PRICE_ENTITY);
        // When
        underTest.attachScreening(SCREENING_DTO, PRICE_ENTITY.getName());
        // Then
        Assertions.assertTrue(PRICE_ENTITY.getScreenings().contains(SCREENING_ENTITY));
        Mockito.verify(priceRepository).findByName(PRICE_DTO.getName());
        Mockito.verify(screeningRepository).findByMovieEntity_TitleAndAndRoomEntity_NameAndStartTime(
            SCREENING_DTO.getMovieName(),SCREENING_DTO.getRoomName(),SCREENING_DTO.getTime());
        Mockito.verify(priceRepository).save(PRICE_ENTITY);
        Mockito.verifyNoMoreInteractions(priceRepository);
        Mockito.verifyNoMoreInteractions(screeningRepository);
    }

    @Test
    public void testAttachScreeningShouldThrowAttachPriceExceptionWhenScreeningNotExists() {
        // Given
        Mockito.when(screeningRepository.findByMovieEntity_TitleAndAndRoomEntity_NameAndStartTime(
            SCREENING_DTO.getMovieName(),SCREENING_DTO.getRoomName(),SCREENING_DTO.getTime()))
            .thenReturn(Optional.empty());
        Mockito.when(priceRepository.findByName(PRICE_DTO.getName())).thenReturn(Optional.of(PRICE_ENTITY));
        // When
        Assertions.assertThrows(AttachPriceException.class,
            () -> underTest.attachScreening(SCREENING_DTO, PRICE_ENTITY.getName()));

        // Then
        Mockito.verify(priceRepository).findByName(PRICE_DTO.getName());
        Mockito.verify(screeningRepository).findByMovieEntity_TitleAndAndRoomEntity_NameAndStartTime(
            SCREENING_DTO.getMovieName(),SCREENING_DTO.getRoomName(),SCREENING_DTO.getTime());
        Mockito.verifyNoMoreInteractions(priceRepository);
        Mockito.verifyNoMoreInteractions(screeningRepository);
    }

    @Test
    public void testAttachScreeningShouldThrowUnknownPriceExceptionWhenPriceNameNotExists() {
        // Given
        Mockito.when(priceRepository.findByName(PRICE_DTO.getName())).thenReturn(Optional.empty());
        // When
        Assertions.assertThrows(UnknownPriceException.class,
            () -> underTest.attachScreening(SCREENING_DTO, PRICE_ENTITY.getName()));

        // Then
        Mockito.verify(priceRepository).findByName(PRICE_DTO.getName());
        Mockito.verifyNoMoreInteractions(priceRepository);
        Mockito.verifyNoMoreInteractions(screeningRepository);
    }

}
