package com.epam.training.ticketservice.core.screening.persistence.repository;

import com.epam.training.ticketservice.core.screening.persistence.entity.ScreeningEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ScreeningRepository extends JpaRepository<ScreeningEntity, Integer> {


    Optional<ScreeningEntity> findByMovieEntity_TitleAndAndRoomEntity_NameAndTime(String movieTitle, String roomName, LocalDateTime time);


    @Override
    @Query(value = "SELECT s FROM ScreeningEntity s join fetch s.movieEntity join fetch s.roomEntity")
    List<ScreeningEntity> findAll();
}
