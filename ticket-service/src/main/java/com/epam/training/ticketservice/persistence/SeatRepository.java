package com.epam.training.ticketservice.persistence;

import com.epam.training.ticketservice.persistence.entity.SeatEntity;
import com.epam.training.ticketservice.persistence.entity.SeatId;
import org.springframework.data.repository.CrudRepository;

public interface SeatRepository extends CrudRepository<SeatEntity, SeatId> {
}
