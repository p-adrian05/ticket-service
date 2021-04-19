package com.epam.training.ticketservice.persistence;

import com.epam.training.ticketservice.persistence.entity.MovieEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface MovieRepository extends CrudRepository<MovieEntity,Integer> {

    Optional<MovieEntity> findMovieEntityByTitle(String title);
    boolean existsMovieEntityByTitle(String title);

    @Override
    @Query(value = "SELECT m FROM MovieEntity m join fetch m.genreEntity")
    Iterable<MovieEntity> findAll();
}
