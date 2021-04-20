package com.epam.training.ticketservice.core.room.persistence.repository;

import com.epam.training.ticketservice.core.room.persistence.entity.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<RoomEntity,Integer> {

    boolean existsByName(String name);

    Optional<RoomEntity> findByName(String name);
}
