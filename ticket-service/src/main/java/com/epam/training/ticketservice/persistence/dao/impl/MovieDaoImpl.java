package com.epam.training.ticketservice.persistence.dao.impl;

import com.epam.training.ticketservice.persistence.GenreRepository;
import com.epam.training.ticketservice.persistence.MovieRepository;
import com.epam.training.ticketservice.persistence.dao.MovieDao;
import com.epam.training.ticketservice.model.Movie;
import com.epam.training.ticketservice.persistence.entity.GenreEntity;
import com.epam.training.ticketservice.persistence.entity.MovieEntity;
import com.epam.training.ticketservice.persistence.exceptions.MovieAlreadyExistsException;
import com.epam.training.ticketservice.persistence.exceptions.UnknownMovieException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import java.util.stream.StreamSupport;

@Repository
@RequiredArgsConstructor
@Slf4j
public class MovieDaoImpl implements MovieDao {

    private final MovieRepository movieRepository;

    private final GenreRepository genreRepository;

    private final EntityQuery entityQuery;


    @Override
    public void createMovie(Movie movie) throws MovieAlreadyExistsException {
        Objects.requireNonNull(movie, "Movie is a mandatory parameter");
        if(movieRepository.existsMovieEntityByTitle(movie.getTitle())){
            throw new MovieAlreadyExistsException(String.format("Movie already exists with title: %s",movie.getTitle()));
        }
        log.debug("Creating new Movie : {}",movie);
        MovieEntity movieEntity = MovieEntity.builder()
                .genreEntity(queryGenre(movie.getGenre()))
                .duration(movie.getDuration())
                .title(movie.getTitle())
                .build();
        int id =  movieRepository.save(movieEntity).getId();
        log.debug("Created movie id is : {}",id);
    }

    @Override
    public void updateMovie(Movie movie) throws UnknownMovieException {
        Objects.requireNonNull(movie, "Movie is a mandatory parameter");
        MovieEntity oldMovieEntity = entityQuery.queryMovie(movie.getTitle());
        MovieEntity updatedMovieEntity = MovieEntity.builder()
                .genreEntity(queryGenre(movie.getGenre()))
                .duration(movie.getDuration())
                .title(movie.getTitle())
                .id(oldMovieEntity.getId())
                .build();
        movieRepository.save(updatedMovieEntity);
        log.debug("Updated Movie entity: {}",updatedMovieEntity);
    }

    @Override
    public void deleteMovie(String title) throws UnknownMovieException {
        MovieEntity movieEntity = entityQuery.queryMovie(title);
        movieRepository.delete(movieEntity);
        log.debug("Deleted Movie {}",movieEntity);
    }

    @Override
    public Collection<Movie> readAllMovies() {
        return StreamSupport.stream(movieRepository.findAll().spliterator(),true)
                .map(movieEntity -> Movie.builder().duration(movieEntity.getDuration())
                                    .genre(movieEntity.getGenreEntity().getName())
                                    .title(movieEntity.getTitle()).build())
                .collect(Collectors.toList());
    }

    protected GenreEntity queryGenre(String name){
        Optional<GenreEntity> genreEntityOptional = genreRepository.findGenreEntityByName(name);
        return genreEntityOptional.orElseGet(() -> genreRepository.save(GenreEntity.builder().name(name).build()));
    }
}
