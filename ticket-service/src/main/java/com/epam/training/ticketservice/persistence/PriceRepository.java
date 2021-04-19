package com.epam.training.ticketservice.persistence;

import com.epam.training.ticketservice.persistence.entity.PriceEntity;
import org.springframework.data.repository.CrudRepository;

public interface PriceRepository extends CrudRepository<PriceEntity, Integer> {
}
