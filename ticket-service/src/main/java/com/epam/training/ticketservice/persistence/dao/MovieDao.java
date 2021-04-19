package com.epam.training.ticketservice.persistence.dao;

import com.epam.training.ticketservice.model.Movie;
import com.epam.training.ticketservice.persistence.exceptions.MovieAlreadyExistsException;
import com.epam.training.ticketservice.persistence.exceptions.UnknownMovieException;

import java.util.Collection;

public interface MovieDao  {

    void createMovie(Movie movie) throws MovieAlreadyExistsException;

    void updateMovie(Movie movie) throws UnknownMovieException;

    void deleteMovie(String title) throws UnknownMovieException;

    Collection<Movie> readAllMovies();

}

