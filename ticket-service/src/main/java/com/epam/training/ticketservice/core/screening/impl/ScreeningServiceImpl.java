package com.epam.training.ticketservice.core.screening.impl;

import com.epam.training.ticketservice.core.movie.persistence.entity.MovieEntity;
import com.epam.training.ticketservice.core.movie.persistence.repository.MovieRepository;
import com.epam.training.ticketservice.core.room.persistence.entity.RoomEntity;
import com.epam.training.ticketservice.core.room.persistence.repository.RoomRepository;
import com.epam.training.ticketservice.core.screening.exceptions.ScreeningCreationException;
import com.epam.training.ticketservice.core.screening.persistence.entity.ScreeningEntity;
import com.epam.training.ticketservice.core.screening.persistence.repository.ScreeningRepository;
import com.epam.training.ticketservice.core.screening.ScreeningService;
import com.epam.training.ticketservice.core.screening.model.ScreeningDto;

import com.epam.training.ticketservice.core.screening.exceptions.UnknownScreeningException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
@Slf4j
public class ScreeningServiceImpl implements ScreeningService {

    private final ScreeningRepository screeningRepository;
    private final RoomRepository roomRepository;
    private final MovieRepository movieRepository;

    @Override
    @Transactional
    public void createScreening(ScreeningDto screeningDto) throws ScreeningCreationException {
        Objects.requireNonNull(screeningDto, "Screening cannot be null");
        Optional<MovieEntity> movieEntity = movieRepository.findMovieEntityByTitle(screeningDto.getMovieName());
        Optional<RoomEntity> roomEntity = roomRepository.findByName(screeningDto.getRoomName());
        if(movieEntity.isPresent() && roomEntity.isPresent()){
            log.debug("Creating new Screening : {}",screeningDto);
            int id =  screeningRepository.save(ScreeningEntity.builder()
                    .movieEntity(movieEntity.get())
                    .roomEntity(roomEntity.get())
                    .time(screeningDto.getTime())
                    .build()).getId();
            log.debug("Created screen id is : {}",id);
        }else{
            throw new ScreeningCreationException
                    (String.format("Movie or room not found. Movie name: %s, Room name: %s",screeningDto.getMovieName(),
                            screeningDto.getRoomName()));
        }
    }


    @Override
    @Transactional
    public void deleteScreening(ScreeningDto screeningDto) throws UnknownScreeningException {
       Optional<ScreeningEntity> screeningEntity = screeningRepository.
               findByMovieEntity_TitleAndAndRoomEntity_NameAndTime(screeningDto.getMovieName(),screeningDto.getRoomName(),screeningDto.getTime());
       if(screeningEntity.isPresent()){
           screeningRepository.delete(screeningEntity.get());
           log.debug("Deleted Screening {}",screeningEntity);
       }else{
           throw new UnknownScreeningException(
                   String.format("Screening not found : %s",screeningDto));
       }

    }

    @Override
    public Collection<ScreeningDto> readAllScreenings() {
        return screeningRepository.findAll().stream()
                .map(this::convertEntityToDto).collect(Collectors.toList());
    }

    private ScreeningDto convertEntityToDto(ScreeningEntity screeningEntity) {
        return ScreeningDto.builder()
                .movieName(screeningEntity.getMovieEntity().getTitle())
                .roomName(screeningEntity.getRoomEntity().getName())
                .time(screeningEntity.getTime())
                .build();
    }

}
