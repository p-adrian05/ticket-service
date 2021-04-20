package com.epam.training.ticketservice.core.booking.persistence.repository;

import com.epam.training.ticketservice.core.booking.persistence.entity.TicketEntity;
import com.epam.training.ticketservice.core.booking.persistence.entity.TicketId;
import org.springframework.data.repository.CrudRepository;

public interface TicketRepository extends CrudRepository<TicketEntity,TicketId> {
}
