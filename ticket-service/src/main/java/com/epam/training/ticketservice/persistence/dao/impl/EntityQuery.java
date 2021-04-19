package com.epam.training.ticketservice.persistence.dao.impl;

import com.epam.training.ticketservice.model.Movie;
import com.epam.training.ticketservice.model.Screening;
import com.epam.training.ticketservice.persistence.MovieRepository;
import com.epam.training.ticketservice.persistence.RoomRepository;
import com.epam.training.ticketservice.persistence.ScreeningRepository;
import com.epam.training.ticketservice.persistence.entity.MovieEntity;
import com.epam.training.ticketservice.persistence.entity.RoomEntity;
import com.epam.training.ticketservice.persistence.entity.ScreeningEntity;
import com.epam.training.ticketservice.persistence.exceptions.UnknownMovieException;
import com.epam.training.ticketservice.persistence.exceptions.UnknownRoomException;
import com.epam.training.ticketservice.persistence.exceptions.UnknownScreeningException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
class EntityQuery {

    private final MovieRepository movieRepository;
    private final RoomRepository roomRepository;
    private final ScreeningRepository screeningRepository;

    public MovieEntity queryMovie(String title) throws UnknownMovieException {
      return queryMovieEntity(title,movieRepository::findMovieEntityByTitle);
    }

    public RoomEntity queryRoom(String name) throws UnknownRoomException {
        Optional<RoomEntity> roomEntityOptional= roomRepository.findByName(name);
        if(roomEntityOptional.isEmpty()){
            throw new UnknownRoomException(String.format("Room is not found with name: %s",name));
        }
        return roomEntityOptional.get();
    }

    public ScreeningEntity queryScreening(Screening screening) throws UnknownScreeningException {
        Optional<ScreeningEntity> screeningEntityOptional= screeningRepository.findByMovieEntity_TitleAndAndRoomEntity_NameAndTime(screening.getMovieName(),screening.getRoomName(),screening.getTime());
        if(screeningEntityOptional.isEmpty()){
            throw new UnknownScreeningException(String.format("Screening is not found: %s",screening));
        }
        return screeningEntityOptional.get();
    }

    private MovieEntity queryMovieEntity(String title, Function<String,Optional<MovieEntity>> query) throws UnknownMovieException {
        Optional<MovieEntity> movieEntityOptional = query.apply(title);
        if (movieEntityOptional.isEmpty()) {
            throw new UnknownMovieException(String.format("Movie not found with title: %s", title));
        }
        return movieEntityOptional.get();
    }
}
