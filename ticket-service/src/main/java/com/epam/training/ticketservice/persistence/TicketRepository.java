package com.epam.training.ticketservice.persistence;

import com.epam.training.ticketservice.persistence.entity.RoomEntity;
import com.epam.training.ticketservice.persistence.entity.TicketEntity;
import com.epam.training.ticketservice.persistence.entity.TicketId;
import org.springframework.data.repository.CrudRepository;

public interface TicketRepository extends CrudRepository<TicketEntity,TicketId> {
}
