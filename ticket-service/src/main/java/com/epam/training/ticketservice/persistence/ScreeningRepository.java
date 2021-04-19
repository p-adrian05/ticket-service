package com.epam.training.ticketservice.persistence;

import com.epam.training.ticketservice.persistence.entity.ScreeningEntity;
import org.springframework.data.repository.CrudRepository;

public interface ScreeningRepository extends CrudRepository<ScreeningEntity, Integer> {
}
