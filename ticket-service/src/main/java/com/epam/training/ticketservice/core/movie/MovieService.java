package com.epam.training.ticketservice.core.movie;

import com.epam.training.ticketservice.core.movie.exceptions.MovieAlreadyExistsException;
import com.epam.training.ticketservice.core.movie.exceptions.UnknownMovieException;
import com.epam.training.ticketservice.core.movie.model.MovieDto;

import java.util.List;

public interface MovieService {

    void createMovie(MovieDto movie) throws MovieAlreadyExistsException;

    void updateMovie(MovieDto movie) throws UnknownMovieException;

    void deleteMovie(String title) throws UnknownMovieException;

    List<MovieDto> getMovies();

}
