package com.epam.training.ticketservice.core.movie.impl;

import com.epam.training.ticketservice.core.movie.MovieService;
import com.epam.training.ticketservice.core.movie.model.MovieDto;
import com.epam.training.ticketservice.core.movie.persistence.entity.GenreEntity;
import com.epam.training.ticketservice.core.movie.persistence.entity.MovieEntity;
import com.epam.training.ticketservice.core.movie.persistence.repository.GenreRepository;
import com.epam.training.ticketservice.core.movie.persistence.repository.MovieRepository;
import com.epam.training.ticketservice.core.movie.exceptions.MovieAlreadyExistsException;
import com.epam.training.ticketservice.core.movie.exceptions.UnknownMovieException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;

    private final GenreRepository genreRepository;

    @Override
    @Transactional
    public void createMovie(MovieDto movie) throws MovieAlreadyExistsException {
        Objects.requireNonNull(movie, "Movie cannot be null");
        Objects.requireNonNull(movie.getTitle(), "Movie title cannot be null");
        Objects.requireNonNull(movie.getDuration(), "Movie duration cannot be null");
        if(movieRepository.existsMovieEntityByTitle(movie.getTitle())){
            throw new MovieAlreadyExistsException(String.format("Movie already exists with title: %s",movie.getTitle()));
        }
        log.debug("Creating new Movie : {}",movie);
        MovieEntity movieEntity = MovieEntity.builder()
                .genreEntity(queryGenre(movie.getGenre()))
                .duration(movie.getDuration())
                .title(movie.getTitle())
                .build();
        MovieEntity createdMovie = movieRepository.save(movieEntity);
        log.debug("Created movie is : {}",createdMovie);
    }

    @Override
    @Transactional
    public void updateMovie(MovieDto movie) throws UnknownMovieException {
        Objects.requireNonNull(movie, "Movie cannot be null");
        Objects.requireNonNull(movie.getTitle(), "Movie title cannot be null");
        Objects.requireNonNull(movie.getDuration(), "Movie duration cannot be null");
        Optional<MovieEntity> oldMovieEntity = movieRepository.findMovieEntityByTitle(movie.getTitle());
        if(oldMovieEntity.isEmpty()){
            throw new UnknownMovieException(String.format("Movie cannot found: %s",movie));
        }
        MovieEntity updatedMovieEntity = MovieEntity.builder()
                .genreEntity(queryGenre(movie.getGenre()))
                .duration(movie.getDuration())
                .title(movie.getTitle())
                .id(oldMovieEntity.get().getId())
                .build();
        movieRepository.save(updatedMovieEntity);
        log.debug("Updated Movie entity: {}",updatedMovieEntity);
    }

    @Override
    @Transactional
    public void deleteMovie(String title) throws UnknownMovieException {
        Optional<MovieEntity> movieEntity = movieRepository.findMovieEntityByTitle(title);
        if(movieEntity.isEmpty()){
            throw new UnknownMovieException(String.format("Movie cannot found by title %s",title));
        }
        movieRepository.delete(movieEntity.get());
        log.debug("Deleted Movie {}",movieEntity.get());
    }

    @Override
    public List<MovieDto> getMovies() {
        return movieRepository.findAll().stream().map(this::convertEntityToDto).collect(Collectors.toList());
    }

//    public void addPrice(String priceName,String movieName) throws UnknownMovieException, UnknownPriceException {
//        PriceEntity priceEntity = entityQuery.queryPrice(priceName);
//        MovieEntity movieEntity = entityQuery.queryMovieWithPrices(movieName);
//        movieEntity.addPrice(priceEntity);
//        movieRepository.save(movieEntity);
//    }

    private GenreEntity queryGenre(String name){
        Objects.requireNonNull(name, "Genre cannot be null");
        Optional<GenreEntity> genreEntityOptional = genreRepository.findGenreEntityByName(name);
        return genreEntityOptional.orElseGet(() -> genreRepository.save(GenreEntity.builder().name(name).build()));
    }

    private MovieDto convertEntityToDto(MovieEntity movieEntity) {
        return MovieDto.builder()
                .duration(movieEntity.getDuration())
                .genre(movieEntity.getGenreEntity().getName())
                .title(movieEntity.getTitle())
                .build();
    }
}
