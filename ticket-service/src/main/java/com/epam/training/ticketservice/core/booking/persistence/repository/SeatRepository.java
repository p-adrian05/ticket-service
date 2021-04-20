package com.epam.training.ticketservice.core.booking.persistence.repository;

import com.epam.training.ticketservice.core.booking.persistence.entity.SeatEntity;
import com.epam.training.ticketservice.core.booking.persistence.entity.SeatId;
import org.springframework.data.repository.CrudRepository;

public interface SeatRepository extends CrudRepository<SeatEntity, SeatId> {
}
