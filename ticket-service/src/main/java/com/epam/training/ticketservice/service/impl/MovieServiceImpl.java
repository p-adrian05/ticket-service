package com.epam.training.ticketservice.service.impl;

import com.epam.training.ticketservice.persistence.dao.MovieDao;
import com.epam.training.ticketservice.service.MovieService;
import com.epam.training.ticketservice.model.Movie;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@NoArgsConstructor
@AllArgsConstructor
public class MovieServiceImpl implements MovieService {

    private MovieDao movieDao;

    @Override
    public int createMovie(Movie movie) {
        return 0;
    }

    @Override
    public void updateMovie(Movie movie) {

    }

    @Override
    public void deleteMovie(String title) {

    }

    @Override
    public Collection<Movie> getMovies() {
        return null;
    }
}
