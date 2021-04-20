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
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
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
            LocalDateTime newStart = screeningDto.getTime();
            LocalDateTime newEnd = screeningDto.getTime().plusMinutes(movieEntity.get().getDuration());
            if(isFreeToScreen(roomEntity.get().getScreenings(),newStart,newEnd)){
                log.debug("Creating new Screening : {}",screeningDto);
                int id =  screeningRepository.save(ScreeningEntity.builder()
                        .movieEntity(movieEntity.get())
                        .roomEntity(roomEntity.get())
                        .startTime(screeningDto.getTime())
                        .endTime(screeningDto.getTime().plusMinutes(movieEntity.get().getDuration()))
                        .build()).getId();
                log.debug("Created screen id is : {}",id);
            }
        }else{
            throw new ScreeningCreationException
                    (String.format("Movie or room not found. Movie name: %s, Room name: %s",screeningDto.getMovieName(),
                            screeningDto.getRoomName()));
        }
    }

     private boolean isFreeToScreen(List<ScreeningEntity> screenings,LocalDateTime newStart, LocalDateTime newEnd) throws ScreeningCreationException{
        if(screenings.size()==0){
            return true;
        }
        for(ScreeningEntity screeningEntity : screenings){
            if(!isOverlap(screeningEntity.getStartTime(),screeningEntity.getEndTime(),newStart,newEnd)){
                if(Math.abs(Duration.between(newStart,screeningEntity.getEndTime()).toMinutes())<=10 ||
                        Math.abs(Duration.between(newEnd,screeningEntity.getStartTime()).toMinutes())<=10 ){
                    throw new ScreeningCreationException("This would start in the break period after another screening in this room");
                }else{
                    return true;
                }
            }
        }
         throw new ScreeningCreationException("There is an overlapping screening");
    }

    private boolean isOverlap(LocalDateTime start, LocalDateTime end, LocalDateTime newStart, LocalDateTime newEnd){
        return newStart.isBefore(end) && newEnd.isAfter(start);
    }


    @Override
    @Transactional
    public void deleteScreening(ScreeningDto screeningDto) throws UnknownScreeningException {
       Optional<ScreeningEntity> screeningEntity = screeningRepository.
               findByMovieEntity_TitleAndAndRoomEntity_NameAndStartTime(screeningDto.getMovieName(),screeningDto.getRoomName(),screeningDto.getTime());
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
                .time(screeningEntity.getStartTime())
                .build();
    }

}
