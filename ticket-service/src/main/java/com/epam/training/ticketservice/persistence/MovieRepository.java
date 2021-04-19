package com.epam.training.ticketservice.persistence;

import com.epam.training.ticketservice.persistence.entity.MovieEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

public interface MovieRepository extends CrudRepository<MovieEntity,Integer> {
}
