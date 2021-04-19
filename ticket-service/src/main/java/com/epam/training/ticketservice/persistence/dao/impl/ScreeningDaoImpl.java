package com.epam.training.ticketservice.persistence.dao.impl;

import com.epam.training.ticketservice.model.Room;
import com.epam.training.ticketservice.persistence.ScreeningRepository;
import com.epam.training.ticketservice.persistence.dao.ScreeningDao;
import com.epam.training.ticketservice.model.Screening;
import com.epam.training.ticketservice.persistence.entity.MovieEntity;
import com.epam.training.ticketservice.persistence.entity.RoomEntity;
import com.epam.training.ticketservice.persistence.entity.ScreeningEntity;
import com.epam.training.ticketservice.persistence.exceptions.UnknownMovieException;
import com.epam.training.ticketservice.persistence.exceptions.UnknownRoomException;
import com.epam.training.ticketservice.persistence.exceptions.UnknownScreeningException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.query.EscapeCharacter;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
@Repository
@Slf4j
public class ScreeningDaoImpl implements ScreeningDao {


   private final ScreeningRepository screeningRepository;

   private final EntityQuery entityQuery;

    @Override
    public void createScreening(Screening screening) throws UnknownMovieException, UnknownRoomException {
        Objects.requireNonNull(screening, "Screening is a mandatory parameter");
        MovieEntity movieEntity = entityQuery.queryMovie(screening.getMovieName());
        RoomEntity roomEntity = entityQuery.queryRoom(screening.getMovieName());
        log.debug("Creating new Screening : {}",screening);
       int id =  screeningRepository.save(ScreeningEntity.builder()
                                    .movieEntity(movieEntity)
                                    .roomEntity(roomEntity)
                                    .time(screening.getTime())
                                    .build()).getId();
        log.debug("Created screen id is : {}",id);
    }


    @Override
    public void deleteScreening(Screening screening) throws UnknownScreeningException {
       ScreeningEntity screeningEntity = entityQuery.queryScreening(screening);
       screeningRepository.delete(screeningEntity);
        log.debug("Deleted Screening {}",screeningEntity);
    }

    @Override
    public Collection<Screening> readAllScreenings() {
        return StreamSupport.stream(screeningRepository.findAll().spliterator(), true)
                .map(screeningEntity -> Screening.builder()
                        .movieName(screeningEntity.getMovieEntity().getTitle())
                        .roomName(screeningEntity.getRoomEntity().getName())
                        .time(screeningEntity.getTime())
                        .build())
                .collect(Collectors.toList());
    }

}
