package com.training.epam.ticketservice.core.screening.impl;

import com.epam.training.ticketservice.core.movie.persistence.entity.GenreEntity;
import com.epam.training.ticketservice.core.movie.persistence.entity.MovieEntity;
import com.epam.training.ticketservice.core.movie.persistence.repository.MovieRepository;

import com.epam.training.ticketservice.core.room.persistence.entity.RoomEntity;
import com.epam.training.ticketservice.core.room.persistence.repository.RoomRepository;
import com.epam.training.ticketservice.core.screening.exceptions.ScreeningCreationException;
import com.epam.training.ticketservice.core.screening.exceptions.UnknownScreeningException;
import com.epam.training.ticketservice.core.screening.impl.ScreeningServiceImpl;
import com.epam.training.ticketservice.core.screening.model.ScreeningDto;
import com.epam.training.ticketservice.core.screening.persistence.entity.ScreeningEntity;
import com.epam.training.ticketservice.core.screening.persistence.repository.ScreeningRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class ScreeningServiceImplTest {

    private static final RoomEntity ROOM_ENTITY = RoomEntity.builder()
            .id(1)
            .columns(10)
            .name("A1")
            .rows(15)
            .build();
    private static final MovieEntity MOVIE_ENTITY = MovieEntity.builder()
            .id(1)
            .duration(60)
            .title("Test title")
            .genreEntity(new GenreEntity(1,"Action")).build();
    private static final ScreeningEntity SCREENING_ENTITY_1 = ScreeningEntity.builder()
            .movieEntity(MOVIE_ENTITY)
            .roomEntity(ROOM_ENTITY)
            .id(null)
            .startTime(LocalDateTime.of(2021,4,22,10,30))
            .endTime(LocalDateTime.of(2021,4,22,11,30))
            .build();
    private static final ScreeningEntity SCREENING_ENTITY_2 = ScreeningEntity.builder()
            .movieEntity(MOVIE_ENTITY)
            .roomEntity(ROOM_ENTITY)
            .id(null)
            .startTime(LocalDateTime.of(2021,4,22,14,30))
            .endTime(LocalDateTime.of(2021,4,22,15,30))
            .build();

    public static final ScreeningDto SCREENING_DTO_1 = ScreeningDto.builder()
            .movieName("Test title")
            .roomName("A1")
            .time(LocalDateTime.of(2021,4,22,10,30))
            .build();
    public static final ScreeningDto SCREENING_DTO_2 = ScreeningDto.builder()
            .movieName("Test title")
            .roomName("A1")
            .time(LocalDateTime.of(2021,4,22,14,30))
            .build();

    private static final String CREATION_EXCEPTION_MESSAGE_1 = "There is an overlapping screening";
    private static final String CREATION_EXCEPTION_MESSAGE_2 = "This would start in the break period after another screening in this room";

    private ScreeningServiceImpl underTest;
    private RoomRepository roomRepository;
    private ScreeningRepository screeningRepository;
    private MovieRepository movieRepository;

    @BeforeEach
    public void init() {
        roomRepository = Mockito.mock(RoomRepository.class);
        screeningRepository = Mockito.mock(ScreeningRepository.class);
        movieRepository = Mockito.mock(MovieRepository.class);
        underTest = new ScreeningServiceImpl(screeningRepository,roomRepository,movieRepository);
    }

    @Test
    public void testGetScreeningShouldCallScreeningRepositoryAndReturnADtoList() {
        // Given
        Mockito.when(screeningRepository.findAll()).thenReturn(List.of(SCREENING_ENTITY_1,SCREENING_ENTITY_2));
        List<ScreeningDto> expected = List.of(SCREENING_DTO_1,SCREENING_DTO_2);

        // When
        List<ScreeningDto> actual = underTest.getScreenings();

        // Then
        Assertions.assertEquals(expected, actual);
        Mockito.verify(screeningRepository).findAll();
        Mockito.verifyNoMoreInteractions(screeningRepository);
    }
    @Test
    public void testDeleteScreeningShouldCallScreeningRepositoryWhenScreeningIsExists() throws UnknownScreeningException {
        // Given
        Mockito.when(screeningRepository
                .findByMovieEntity_TitleAndAndRoomEntity_NameAndStartTime(SCREENING_DTO_1.getMovieName(),
                        SCREENING_DTO_1.getRoomName(),SCREENING_DTO_1.getTime()))
                .thenReturn(Optional.of(SCREENING_ENTITY_1));
        // When
        underTest.deleteScreening(SCREENING_DTO_1);
        // Then
        Mockito.verify(screeningRepository).findByMovieEntity_TitleAndAndRoomEntity_NameAndStartTime(SCREENING_DTO_1.getMovieName(),
                SCREENING_DTO_1.getRoomName(),SCREENING_DTO_1.getTime());
        Mockito.verify(screeningRepository).delete((SCREENING_ENTITY_1));
        Mockito.verifyNoMoreInteractions(screeningRepository);
    }
    @Test
    public void testDeleteScreeningShouldThrowUnknownScreeningExceptionWhenScreeningIsNotExists(){
        // Given
        Mockito.when(screeningRepository
                .findByMovieEntity_TitleAndAndRoomEntity_NameAndStartTime(SCREENING_DTO_1.getMovieName(),
                        SCREENING_DTO_1.getRoomName(),SCREENING_DTO_1.getTime()))
                .thenReturn(Optional.empty());
        // When
        Assertions.assertThrows(UnknownScreeningException.class, () -> underTest.deleteScreening(SCREENING_DTO_1));
        // Then
        Mockito.verify(screeningRepository).findByMovieEntity_TitleAndAndRoomEntity_NameAndStartTime(SCREENING_DTO_1.getMovieName(),
                SCREENING_DTO_1.getRoomName(),SCREENING_DTO_1.getTime());
        Mockito.verifyNoMoreInteractions(movieRepository);
    }

    @Test
    public void testCreateScreeningShouldCallScreeningRepositoryWhenScreeningInputIsValid() throws ScreeningCreationException {
        // Given
        Mockito.when(movieRepository.findMovieEntityByTitle(SCREENING_DTO_1.getMovieName())).thenReturn(Optional.of(MOVIE_ENTITY));
        Mockito.when(roomRepository.findByName(SCREENING_DTO_1.getRoomName())).thenReturn(Optional.of(ROOM_ENTITY));
        Mockito.when(screeningRepository.save(SCREENING_ENTITY_1)).thenReturn(SCREENING_ENTITY_1);
        // When
        underTest.createScreening(SCREENING_DTO_1);
        // Then
        Mockito.verify(movieRepository).findMovieEntityByTitle(SCREENING_DTO_1.getMovieName());
        Mockito.verify(roomRepository).findByName(SCREENING_DTO_1.getRoomName());
        Mockito.verify(screeningRepository).save(SCREENING_ENTITY_1);
        Mockito.verifyNoMoreInteractions(movieRepository);
        Mockito.verifyNoMoreInteractions(roomRepository);
    }
    @Test
    public void testCreateScreeningShouldThrowScreeningCreationExceptionWhenScreeningMovieTitleIsNotFound()  {
        // Given
        Mockito.when(movieRepository.findMovieEntityByTitle(SCREENING_DTO_1.getMovieName())).thenReturn(Optional.empty());
        Mockito.when(roomRepository.findByName(SCREENING_DTO_1.getRoomName())).thenReturn(Optional.of(ROOM_ENTITY));
        // When
        Assertions.assertThrows(ScreeningCreationException.class, () -> underTest.createScreening(SCREENING_DTO_1));
        // Then
        Mockito.verify(movieRepository).findMovieEntityByTitle(SCREENING_DTO_1.getMovieName());
        Mockito.verify(roomRepository).findByName(SCREENING_DTO_1.getRoomName());
        Mockito.verifyNoMoreInteractions(movieRepository);
        Mockito.verifyNoMoreInteractions(screeningRepository);
        Mockito.verifyNoMoreInteractions(roomRepository);
    }
    @Test
    public void testCreateScreeningShouldThrowScreeningCreationExceptionWhenScreeningRoomNameIsNotFound()  {
        // Given
        Mockito.when(movieRepository.findMovieEntityByTitle(SCREENING_DTO_1.getMovieName())).thenReturn(Optional.of(MOVIE_ENTITY));
        Mockito.when(roomRepository.findByName(SCREENING_DTO_1.getRoomName())).thenReturn(Optional.empty());
        // When
        Assertions.assertThrows(ScreeningCreationException.class, () -> underTest.createScreening(SCREENING_DTO_1));
        // Then
        Mockito.verify(movieRepository).findMovieEntityByTitle(SCREENING_DTO_1.getMovieName());
        Mockito.verify(roomRepository).findByName(SCREENING_DTO_1.getRoomName());
        Mockito.verifyNoMoreInteractions(movieRepository);
        Mockito.verifyNoMoreInteractions(screeningRepository);
        Mockito.verifyNoMoreInteractions(roomRepository);
    }
    @Test
    public void testCreateScreeningShouldThrowScreeningCreationExceptionWhenScreeningRoomNameAndMovieTitleAreNotFound()  {
        // Given
        Mockito.when(movieRepository.findMovieEntityByTitle(SCREENING_DTO_1.getMovieName())).thenReturn(Optional.empty());
        Mockito.when(roomRepository.findByName(SCREENING_DTO_1.getRoomName())).thenReturn(Optional.empty());
        // When
        Assertions.assertThrows(ScreeningCreationException.class, () -> underTest.createScreening(SCREENING_DTO_1));
        // Then
        Mockito.verify(movieRepository).findMovieEntityByTitle(SCREENING_DTO_1.getMovieName());
        Mockito.verify(roomRepository).findByName(SCREENING_DTO_1.getRoomName());
        Mockito.verifyNoMoreInteractions(movieRepository);
        Mockito.verifyNoMoreInteractions(screeningRepository);
        Mockito.verifyNoMoreInteractions(roomRepository);
    }
    @ParameterizedTest
    @ValueSource(ints = {40,41,45,50,55,59})
    public void testIsFreeToScreenShouldReturnTrueWhenScreeningIsNotOverlappingWithAnotherOne(int minutes) throws ScreeningCreationException {
        // Given
        ScreeningEntity SCREENING_ENTITY = ScreeningEntity.builder()
                .movieEntity(MOVIE_ENTITY)
                .roomEntity(ROOM_ENTITY)
                .id(null)
                .startTime(LocalDateTime.of(2021,4,22,10,30))
                .endTime(LocalDateTime.of(2021,4,22,11,30))
                .build();
        LocalDateTime start = LocalDateTime.of(2021,4,22,11,minutes);
        LocalDateTime end = LocalDateTime.of(2021,4,22,13,40);
        Mockito.when(screeningRepository
                .findScreeningEntitiesByRoomEntity_NameAndStartTimeAfterAndEndTimeBefore(
                       "A1", start.minusDays(1),end.plusDays(1)))
                .thenReturn(List.of(SCREENING_ENTITY));
        // When
      boolean result = underTest.isFreeToScreen("A1",start,end);
        // Then
        Assertions.assertTrue(result);
        Mockito.verify(screeningRepository).findScreeningEntitiesByRoomEntity_NameAndStartTimeAfterAndEndTimeBefore(
                "A1", start.minusDays(1),end.plusDays(1));
        Mockito.verifyNoMoreInteractions(screeningRepository);
    }
    @ParameterizedTest
    @ValueSource(ints = {0,10,15,20,25,29})
    public void testIsFreeToScreenShouldThrowScreeningCreationExceptionWhenScreeningIsOverlappingWithAnotherOne(int minute) {
        // Given
        // Given
        ScreeningEntity SCREENING_ENTITY = ScreeningEntity.builder()
                .movieEntity(MOVIE_ENTITY)
                .roomEntity(ROOM_ENTITY)
                .id(null)
                .startTime(LocalDateTime.of(2021,4,22,10,30))
                .endTime(LocalDateTime.of(2021,4,22,11,30))
                .build();
        LocalDateTime start = LocalDateTime.of(2021,4,22,11,minute);
        LocalDateTime end = LocalDateTime.of(2021,4,22,12,0);
        Mockito.when(screeningRepository
                .findScreeningEntitiesByRoomEntity_NameAndStartTimeAfterAndEndTimeBefore(
                        "A1", start.minusDays(1),end.plusDays(1)))
                .thenReturn(List.of(SCREENING_ENTITY));
        // When
        Exception ex =  Assertions.assertThrows(ScreeningCreationException.class, () ->   underTest.isFreeToScreen("A1",start,end),
                CREATION_EXCEPTION_MESSAGE_1);
        // Then
        Assertions.assertEquals(CREATION_EXCEPTION_MESSAGE_1,ex.getMessage());
        Mockito.verify(screeningRepository).findScreeningEntitiesByRoomEntity_NameAndStartTimeAfterAndEndTimeBefore(
                "A1", start.minusDays(1),end.plusDays(1));
        Mockito.verifyNoMoreInteractions(screeningRepository);
    }
    @ParameterizedTest
    @ValueSource(ints = {30,31,32,33,34,35,38,39})
    public void testIsFreeToScreenShouldThrowScreeningCreationExceptionWhenScreeningIsOverlappingWithAnotherOneWithBreakPeriod(int minute) {
        // Given
        ScreeningEntity SCREENING_ENTITY = ScreeningEntity.builder()
                .movieEntity(MOVIE_ENTITY)
                .roomEntity(ROOM_ENTITY)
                .id(null)
                .startTime(LocalDateTime.of(2021,4,22,10,30))
                .endTime(LocalDateTime.of(2021,4,22,11,30))
                .build();
        LocalDateTime start = LocalDateTime.of(2021,4,22,11,minute);
        LocalDateTime end = LocalDateTime.of(2021,4,22,14,15);
        Mockito.when(screeningRepository
                .findScreeningEntitiesByRoomEntity_NameAndStartTimeAfterAndEndTimeBefore(
                        "A1", start.minusDays(1),end.plusDays(1)))
                .thenReturn(List.of(SCREENING_ENTITY));
        // When
        Exception ex = Assertions.assertThrows(ScreeningCreationException.class, () ->
                underTest.isFreeToScreen("A1",start,end));
        // Then
        Assertions.assertEquals(CREATION_EXCEPTION_MESSAGE_2,ex.getMessage());
        Mockito.verify(screeningRepository).findScreeningEntitiesByRoomEntity_NameAndStartTimeAfterAndEndTimeBefore(
                "A1", start.minusDays(1),end.plusDays(1));
        Mockito.verifyNoMoreInteractions(screeningRepository);
    }
}
