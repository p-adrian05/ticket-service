package com.epam.training.ticketservice.persistence;

import com.epam.training.ticketservice.persistence.entity.GenreEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

public interface GenreRepository extends CrudRepository<GenreEntity,Integer> {
}
