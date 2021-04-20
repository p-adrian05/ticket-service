package com.epam.training.ticketservice.core.movie.persistence.repository;

import com.epam.training.ticketservice.core.movie.persistence.entity.GenreEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface GenreRepository extends CrudRepository<GenreEntity,Integer> {

    Optional<GenreEntity> findGenreEntityByName(String name);
}
