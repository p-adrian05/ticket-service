package com.epam.training.ticketservice.service;

import com.epam.training.ticketservice.model.Movie;

import java.util.Collection;

public interface MovieService {

    int createMovie(Movie movie);

    void updateMovie(Movie movie);

    void deleteMovie(String title);

    Collection<Movie> getMovies();

}
