package com.epam.training.ticketservice.persistence;

import com.epam.training.ticketservice.persistence.entity.GenreEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface GenreRepository extends CrudRepository<GenreEntity,Integer> {

    Optional<GenreEntity> findGenreEntityByName(String name);
}
