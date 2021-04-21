package com.training.epam.ticketservice.core.movie.impl;

import com.epam.training.ticketservice.core.movie.exceptions.MovieAlreadyExistsException;
import com.epam.training.ticketservice.core.movie.exceptions.UnknownMovieException;
import com.epam.training.ticketservice.core.movie.impl.MovieServiceImpl;
import com.epam.training.ticketservice.core.movie.model.MovieDto;
import com.epam.training.ticketservice.core.movie.persistence.entity.GenreEntity;
import com.epam.training.ticketservice.core.movie.persistence.entity.MovieEntity;
import com.epam.training.ticketservice.core.movie.persistence.repository.GenreRepository;
import com.epam.training.ticketservice.core.movie.persistence.repository.MovieRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

public class MovieServiceImplTest {

    private static final GenreEntity GENRE_ENTITY1 = new GenreEntity(1,"Action");
    private static final GenreEntity GENRE_ENTITY2 = new GenreEntity(2,"Drama");

    private static final MovieEntity MOVIE_ENTITY_1 = MovieEntity.builder()
            .id(1)
            .duration(100)
            .title("Test1 title")
            .genreEntity(GENRE_ENTITY1).build();
    private static final MovieEntity MOVIE_ENTITY_2 = MovieEntity.builder()
            .id(2)
            .duration(123)
            .title("Test2 title")
            .genreEntity(GENRE_ENTITY2).build();

    public static final MovieDto MOVIE_DTO_1 = MovieDto.builder()
            .duration(100)
            .title("Test1 title")
            .genre("Action")
            .build();
    public static final MovieDto MOVIE_DTO_2 = MovieDto.builder()
            .duration(123)
            .title("Test2 title")
            .genre("Drama")
            .build();

    private MovieServiceImpl underTest;
    private MovieRepository movieRepository;
    private GenreRepository genreRepository;

    @BeforeEach
    public void init() {
        movieRepository = Mockito.mock(MovieRepository.class);
        genreRepository = Mockito.mock(GenreRepository.class);
        underTest = new MovieServiceImpl(movieRepository,genreRepository);
    }

    @Test
    public void testGetMoviesShouldCallMovieRepositoryAndReturnADtoList() {
        // Given
        Mockito.when(movieRepository.findAll()).thenReturn(List.of(MOVIE_ENTITY_1, MOVIE_ENTITY_2));
        List<MovieDto> expected = List.of(MOVIE_DTO_1,MOVIE_DTO_2);

        // When
        List<MovieDto> actual = underTest.getMovies();

        // Then
        Assertions.assertEquals(expected, actual);
        Mockito.verify(movieRepository).findAll();
        Mockito.verifyNoMoreInteractions(movieRepository);
    }

    @Test
    public void testCreateMovieShouldCallMovieRepositoryWhenTheInputMovieIsValid() throws MovieAlreadyExistsException {
        // Given
         MovieEntity MOVIE_ENTITY_TO_SAVE = MovieEntity.builder()
                .id(null)
                .duration(100)
                .title("Test1 title")
                .genreEntity(GENRE_ENTITY1).build();
        Mockito.when(movieRepository.existsMovieEntityByTitle(MOVIE_DTO_1.getTitle())).thenReturn(false);
        Mockito.when(movieRepository.save(MOVIE_ENTITY_TO_SAVE)).thenReturn(MOVIE_ENTITY_1);
        Mockito.when(genreRepository.findGenreEntityByName(MOVIE_DTO_1.getGenre())).thenReturn(Optional.of(GENRE_ENTITY1));
        // When
        underTest.createMovie(MOVIE_DTO_1);

        // Then
        Mockito.verify(movieRepository).existsMovieEntityByTitle(MOVIE_DTO_1.getTitle());
        Mockito.verify(genreRepository).findGenreEntityByName(MOVIE_DTO_1.getGenre());
        Mockito.verify(movieRepository).save(MOVIE_ENTITY_TO_SAVE);
        Mockito.verifyNoMoreInteractions(movieRepository);
        Mockito.verifyNoMoreInteractions(genreRepository);
    }
    @Test
    public void testCreateMovieShouldThrowMovieAlreadyExistsExceptionWhenMovieAlreadyExistsInDatabaseByTitle()  {
        // Given
        Mockito.when(movieRepository.existsMovieEntityByTitle(MOVIE_DTO_1.getTitle())).thenReturn(true);
        // When
        Assertions.assertThrows(MovieAlreadyExistsException.class, () -> underTest.createMovie(MOVIE_DTO_1));
        // Then
        Mockito.verify(movieRepository).existsMovieEntityByTitle(MOVIE_DTO_1.getTitle());
        Mockito.verifyNoMoreInteractions(movieRepository);
        Mockito.verifyNoMoreInteractions(genreRepository);
    }
    @Test
    public void testCreateMovieShouldThrowNullPointerExistsExceptionWhenTitleIsNull()  {
        // Given
         MovieDto movieDto = MovieDto.builder()
                .duration(100)
                .title(null)
                .genre("Action")
                .build();
        // When
        Assertions.assertThrows(NullPointerException.class, () -> underTest.createMovie(movieDto));
        // Then
        Mockito.verifyNoMoreInteractions(movieRepository);
        Mockito.verifyNoMoreInteractions(genreRepository);
    }
    @Test
    public void testCreateMovieShouldThrowNullPointerExistsExceptionWhenDurationIsNull()  {
        // Given
        MovieDto movieDto = MovieDto.builder()
                .duration(null)
                .title("Test title1")
                .genre("Action")
                .build();
        // When
        Assertions.assertThrows(NullPointerException.class, () -> underTest.createMovie(movieDto));
        // Then
        Mockito.verifyNoMoreInteractions(movieRepository);
        Mockito.verifyNoMoreInteractions(genreRepository);
    }
    @Test
    public void testCreateMovieShouldThrowNullPointerExistsExceptionWhenGenreIsNull()  {
        // Given
        MovieDto movieDto = MovieDto.builder()
                .duration(100)
                .title("Test title1")
                .genre(null)
                .build();
        Mockito.when(movieRepository.existsMovieEntityByTitle(MOVIE_DTO_1.getTitle())).thenReturn(false);
        // When
        Assertions.assertThrows(NullPointerException.class, () -> underTest.createMovie(movieDto));
        // Then
        Mockito.verify(movieRepository).existsMovieEntityByTitle(movieDto.getTitle());
        Mockito.verifyNoMoreInteractions(movieRepository);
        Mockito.verifyNoMoreInteractions(genreRepository);
    }
    @Test
    public void testDeleteMovieShouldCallMovieRepositoryWhenMovieTitleIsExistsInDatabase() throws UnknownMovieException {
        // Given
        Mockito.when(movieRepository.findMovieEntityByTitle("Test1 title")).thenReturn(Optional.of(MOVIE_ENTITY_1));
        // When
        underTest.deleteMovie("Test1 title");
        // Then
        Mockito.verify(movieRepository).findMovieEntityByTitle(("Test1 title"));
        Mockito.verify(movieRepository).delete((MOVIE_ENTITY_1));
        Mockito.verifyNoMoreInteractions(movieRepository);
    }
    @Test
    public void testDeleteMovieShouldThrowUnknownMovieExceptionWhenMovieTitleIsNotExistsInDatabase(){
        // Given
        Mockito.when(movieRepository.findMovieEntityByTitle("Test1 title")).thenReturn(Optional.empty());
        // When
        Assertions.assertThrows(UnknownMovieException.class, () -> underTest.deleteMovie("Test1 title"));
        // Then
        Mockito.verify(movieRepository).findMovieEntityByTitle(("Test1 title"));
        Mockito.verifyNoMoreInteractions(movieRepository);
    }
    @Test
    public void testDeleteMovieShouldThrowUnknownMovieExceptionWhenMovieTitleIsNull(){
        // Given
        Mockito.when(movieRepository.findMovieEntityByTitle(null)).thenReturn(Optional.empty());
        // When
        Assertions.assertThrows(UnknownMovieException.class, () -> underTest.deleteMovie(null));
        // Then
        Mockito.verify(movieRepository).findMovieEntityByTitle((null));
        Mockito.verifyNoMoreInteractions(movieRepository);
    }
    @Test
    public void testUpdateMovieShouldCallMovieRepositoryWhenTheInputMovieIsExistsInDatabase() throws UnknownMovieException {
        // Given
        Mockito.when(movieRepository.findMovieEntityByTitle(MOVIE_DTO_1.getTitle())).thenReturn(Optional.of(MOVIE_ENTITY_1));
        Mockito.when(movieRepository.save(MOVIE_ENTITY_1)).thenReturn(MOVIE_ENTITY_1);
        Mockito.when(genreRepository.findGenreEntityByName(MOVIE_DTO_1.getGenre())).thenReturn(Optional.of(GENRE_ENTITY1));
        // When
        underTest.updateMovie(MOVIE_DTO_1);

        // Then
        Mockito.verify(movieRepository).findMovieEntityByTitle(MOVIE_DTO_1.getTitle());
        Mockito.verify(genreRepository).findGenreEntityByName(MOVIE_DTO_1.getGenre());
        Mockito.verify(movieRepository).save(MOVIE_ENTITY_1);
        Mockito.verifyNoMoreInteractions(movieRepository);
        Mockito.verifyNoMoreInteractions(genreRepository);
    }
    @Test
    public void testUpdateMovieShouldThrowUnknownMovieExceptionWhenTheInputMovieIsNotExistsInDatabase(){
        // Given
        Mockito.when(movieRepository.findMovieEntityByTitle(MOVIE_DTO_1.getTitle())).thenReturn(Optional.empty());
        // When
        Assertions.assertThrows(UnknownMovieException.class, () -> underTest.updateMovie(MOVIE_DTO_1));
        // Then
        Mockito.verify(movieRepository).findMovieEntityByTitle(MOVIE_DTO_1.getTitle());
        Mockito.verifyNoMoreInteractions(movieRepository);
        Mockito.verifyNoMoreInteractions(genreRepository);
    }
    @Test
    public void testUpdateMovieShouldThrowNullPointerExceptionWhenTheMovieTitleIsNull()  {
        // Given
        MovieDto movieDto = MovieDto.builder()
                .duration(100)
                .title(null)
                .genre("Action")
                .build();
        // When
        Assertions.assertThrows(NullPointerException.class, () -> underTest.updateMovie(movieDto));
        // Then
        Mockito.verifyNoMoreInteractions(movieRepository);
        Mockito.verifyNoMoreInteractions(genreRepository);
    }
    @Test
    public void testUpdateMovieShouldThrowNullPointerExceptionWhenTheMovieDurationIsNull()  {
        // Given
        MovieDto movieDto = MovieDto.builder()
                .duration(null)
                .title("Test title1")
                .genre("Action")
                .build();
        // When
        Assertions.assertThrows(NullPointerException.class, () -> underTest.updateMovie(movieDto));
        // Then
        Mockito.verifyNoMoreInteractions(movieRepository);
        Mockito.verifyNoMoreInteractions(genreRepository);
    }
    @Test
    public void testUpdateMovieShouldThrowNullPointerExceptionWhenTheMovieGenreIsNull()  {
        // Given
        MovieDto movieDto = MovieDto.builder()
                .duration(100)
                .title("Test title1")
                .genre(null)
                .build();
        Mockito.when(movieRepository.findMovieEntityByTitle(movieDto.getTitle())).thenReturn(Optional.of(MOVIE_ENTITY_1));
        // When
        Assertions.assertThrows(NullPointerException.class, () -> underTest.updateMovie(movieDto));
        // Then
        Mockito.verify(movieRepository).findMovieEntityByTitle(movieDto.getTitle());
        Mockito.verifyNoMoreInteractions(movieRepository);
        Mockito.verifyNoMoreInteractions(genreRepository);
    }

}
