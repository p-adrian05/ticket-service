package com.epam.training.ticketservice.persistence;

import com.epam.training.ticketservice.persistence.entity.MovieEntity;
import com.epam.training.ticketservice.persistence.entity.ScreeningEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ScreeningRepository extends CrudRepository<ScreeningEntity, Integer> {


    Optional<ScreeningEntity> findByMovieEntity_TitleAndAndRoomEntity_NameAndTime(String movieTitle, String roomName, LocalDateTime time);


    @Override
    @Query(value = "SELECT s FROM ScreeningEntity s join fetch s.movieEntity join fetch s.roomEntity")
    Iterable<ScreeningEntity> findAll();
}
