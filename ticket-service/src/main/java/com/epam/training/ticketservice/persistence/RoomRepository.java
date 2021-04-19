package com.epam.training.ticketservice.persistence;

import com.epam.training.ticketservice.persistence.entity.RoomEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

public interface RoomRepository extends CrudRepository<RoomEntity,Integer> {
}
