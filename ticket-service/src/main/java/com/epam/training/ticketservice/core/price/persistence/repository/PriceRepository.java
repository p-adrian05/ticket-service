package com.epam.training.ticketservice.core.price.persistence.repository;

import com.epam.training.ticketservice.core.price.persistence.entity.PriceEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PriceRepository extends CrudRepository<PriceEntity, Integer> {

    Optional<PriceEntity> findByName(String name);
}
