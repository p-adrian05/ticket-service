package com.epam.training.ticketservice.core.screening.impl;

import com.epam.training.ticketservice.core.movie.model.MovieDto;
import com.epam.training.ticketservice.core.movie.persistence.entity.MovieEntity;
import com.epam.training.ticketservice.core.movie.persistence.repository.MovieRepository;
import com.epam.training.ticketservice.core.room.persistence.entity.RoomEntity;
import com.epam.training.ticketservice.core.room.persistence.repository.RoomRepository;
import com.epam.training.ticketservice.core.screening.exceptions.ScreeningCreationException;
import com.epam.training.ticketservice.core.screening.model.ScreeningDto;
import com.epam.training.ticketservice.core.screening.persistence.entity.ScreeningEntity;
import com.epam.training.ticketservice.core.screening.persistence.repository.ScreeningRepository;
import com.epam.training.ticketservice.core.screening.ScreeningService;
import com.epam.training.ticketservice.core.screening.model.BasicScreeningDto;

import com.epam.training.ticketservice.core.screening.exceptions.UnknownScreeningException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
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
    public void createScreening(BasicScreeningDto screeningDto) throws ScreeningCreationException {
        Objects.requireNonNull(screeningDto, "Screening cannot be null");
        Objects.requireNonNull(screeningDto.getTime(), "Screening time cannot be null");
        Optional<MovieEntity> movieEntity = movieRepository.findMovieEntityByTitle(screeningDto.getMovieName());
        Optional<RoomEntity> roomEntity = roomRepository.findByName(screeningDto.getRoomName());
        if (movieEntity.isPresent() && roomEntity.isPresent()) {
            LocalDateTime newStart = screeningDto.getTime();
            LocalDateTime newEnd = screeningDto.getTime().plusMinutes(movieEntity.get().getDuration());
            if (isFreeToScreen(screeningDto.getRoomName(), newStart, newEnd)) {
                log.debug("Creating new Screening : {}", screeningDto);
                ScreeningEntity createdScreening = screeningRepository.save(ScreeningEntity.builder()
                    .movieEntity(movieEntity.get())
                    .roomEntity(roomEntity.get())
                    .startTime(screeningDto.getTime())
                    .endTime(newEnd)
                    .build());
                log.debug("Created screen is : {}", createdScreening);
            }
        } else {
            throw new ScreeningCreationException(String.format("Movie or room not found. Movie name: %s, Room name: %s",
                screeningDto.getMovieName(),
                    screeningDto.getRoomName()));
        }
    }

    public boolean isFreeToScreen(String roomName, LocalDateTime newStart, LocalDateTime newEnd)
        throws ScreeningCreationException {
        List<ScreeningEntity> nearScreenings = screeningRepository
            .findScreeningEntitiesByRoomEntity_NameAndStartTimeAfterAndEndTimeBefore(roomName, newStart.minusDays(1),
                newEnd.plusDays(1));
        for (ScreeningEntity screeningEntity : nearScreenings) {
            if (!isOverlap(screeningEntity.getStartTime(), screeningEntity.getEndTime(), newStart, newEnd)) {
                if (Math.abs(Duration.between(newStart, screeningEntity.getEndTime()).toMinutes()) < 10
                    || Math.abs(Duration.between(newEnd, screeningEntity.getStartTime()).toMinutes()) < 10) {
                    throw new ScreeningCreationException(
                        "This would start in the break period after another screening in this room");
                }
            } else {
                throw new ScreeningCreationException("There is an overlapping screening");
            }
        }
        return true;
    }

    private boolean isOverlap(LocalDateTime start, LocalDateTime end, LocalDateTime newStart, LocalDateTime newEnd) {
        return newStart.isBefore(end) && newEnd.isAfter(start);
    }


    @Override
    @Transactional
    public void deleteScreening(BasicScreeningDto basicScreeningDto) throws UnknownScreeningException {
        Optional<ScreeningEntity> screeningEntity = screeningRepository
            .findByMovieEntity_TitleAndAndRoomEntity_NameAndStartTime(basicScreeningDto.getMovieName(),
                basicScreeningDto.getRoomName(), basicScreeningDto.getTime());
        if (screeningEntity.isPresent()) {
            screeningRepository.delete(screeningEntity.get());
            log.debug("Deleted Screening {}", screeningEntity.get());
        } else {
            throw new UnknownScreeningException(
                String.format("Screening not found : %s", basicScreeningDto));
        }

    }

    @Override
    public List<ScreeningDto> getScreenings() {
        return screeningRepository.findAll().stream()
            .map(this::convertEntityToDto).collect(Collectors.toList());
    }

    private ScreeningDto convertEntityToDto(ScreeningEntity screeningEntity) {
        return ScreeningDto.builder()
            .movieDto(MovieDto.builder()
                .duration(screeningEntity.getMovieEntity().getDuration())
                .genre(screeningEntity.getMovieEntity().getGenreEntity().getName())
                .title(screeningEntity.getMovieEntity().getTitle())
                .build())
            .roomName(screeningEntity.getRoomEntity().getName())
            .time(screeningEntity.getStartTime())
            .build();
    }

}
