package com.epam.training.ticketservice.persistence;

import com.epam.training.ticketservice.persistence.entity.RoomEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface RoomRepository extends CrudRepository<RoomEntity,Integer> {

    boolean existsByName(String name);

    Optional<RoomEntity> findByName(String name);
}
