package com.epam.training.ticketservice.persistence.dao.impl;

import com.epam.training.ticketservice.persistence.GenreRepository;
import com.epam.training.ticketservice.persistence.MovieRepository;
import com.epam.training.ticketservice.persistence.dao.MovieDao;
import com.epam.training.ticketservice.model.Movie;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
@NoArgsConstructor
@AllArgsConstructor
public class MovieDaoImpl implements MovieDao {

    private MovieRepository movieRepository;

    private GenreRepository genreRepository;


    @Override
    public int create(Movie movie) {
        return 0;
    }

    @Override
    public void update(Movie movie) {

    }

    @Override
    public void delete(Movie movie) {

    }

    @Override
    public Collection<Movie> readAll() {
        return null;
    }

    @Override
    public void deleteByTitle(String title) {

    }
}
