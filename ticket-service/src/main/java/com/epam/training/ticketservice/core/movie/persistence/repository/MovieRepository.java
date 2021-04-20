package com.epam.training.ticketservice.core.movie.persistence.repository;

import com.epam.training.ticketservice.core.movie.persistence.entity.MovieEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;
import java.util.Optional;

public interface MovieRepository extends JpaRepository<MovieEntity,Integer> {

    Optional<MovieEntity> findMovieEntityByTitle(String title);

    @Query(value = "SELECT m FROM MovieEntity m join fetch m.moviePrices")
    Optional<MovieEntity> findMovieEntityByTitleWithPrices(String title);

    boolean existsMovieEntityByTitle(String title);

    @Override
    @Query(value = "SELECT m FROM MovieEntity m join fetch m.genreEntity")
    List<MovieEntity> findAll();
}
