package com.epam.training.ticketservice.core.booking.persistence.repository;

import com.epam.training.ticketservice.core.booking.persistence.entity.TicketEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;


import java.util.List;

public interface TicketRepository extends CrudRepository<TicketEntity, Integer> {

    @Query(value = "select t from TicketEntity t join fetch t.seats join fetch t.screeningEntity where t.userEntity.username =:username")
    List<TicketEntity> findTicketEntitiesByUserEntityUsername(@Param("username") String username);

}
