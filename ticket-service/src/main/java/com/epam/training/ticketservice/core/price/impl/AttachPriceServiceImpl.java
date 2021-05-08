package com.epam.training.ticketservice.core.price.impl;

import com.epam.training.ticketservice.core.movie.persistence.entity.MovieEntity;
import com.epam.training.ticketservice.core.movie.persistence.repository.MovieRepository;
import com.epam.training.ticketservice.core.price.AttachPriceService;
import com.epam.training.ticketservice.core.price.exceptions.AttachPriceException;
import com.epam.training.ticketservice.core.price.exceptions.UnknownPriceException;
import com.epam.training.ticketservice.core.price.persistence.entity.PriceEntity;
import com.epam.training.ticketservice.core.price.persistence.repository.PriceRepository;
import com.epam.training.ticketservice.core.room.persistence.entity.RoomEntity;
import com.epam.training.ticketservice.core.room.persistence.repository.RoomRepository;
import com.epam.training.ticketservice.core.screening.model.BasicScreeningDto;
import com.epam.training.ticketservice.core.screening.persistence.entity.ScreeningEntity;
import com.epam.training.ticketservice.core.screening.persistence.repository.ScreeningRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

@RequiredArgsConstructor
@Slf4j
@Service
public class AttachPriceServiceImpl implements AttachPriceService {

    private final PriceRepository priceRepository;
    private final RoomRepository roomRepository;
    private final ScreeningRepository screeningRepository;
    private final MovieRepository movieRepository;


    @Transactional
    @Override
    public void attachRoom(String roomName, String priceName) throws AttachPriceException, UnknownPriceException {
        Objects.requireNonNull(roomName, "Room name cannot be null for price attaching");
        Objects.requireNonNull(priceName, "Price name cannot be null for price attaching");
        Optional<RoomEntity> roomEntity = roomRepository.findByName(roomName);
        if (roomEntity.isPresent()) {
            attachPrice(priceName, roomEntity.get()::addPrice);
            roomRepository.save(roomEntity.get());
            log.debug(String.format("Attached price %s to room: %s", priceName, roomEntity.get()));
        } else {
            throw new AttachPriceException(String.format("Failed to attach price %s to room: %s", priceName, roomName));
        }
    }

    @Transactional
    @Override
    public void attachMovie(String movieName, String priceName) throws AttachPriceException, UnknownPriceException {
        Objects.requireNonNull(movieName, "Movie name cannot be null for price attaching");
        Objects.requireNonNull(priceName, "Price name cannot be null for price attaching");
        Optional<MovieEntity> movieEntity = movieRepository.findMovieEntityByTitle(movieName);
        if (movieEntity.isPresent()) {
            attachPrice(priceName, movieEntity.get()::addPrice);
            movieRepository.save(movieEntity.get());
            log.debug(String.format("Attached price %s to movie: %s", priceName, movieEntity.get()));
        } else {
            throw new AttachPriceException(
                String.format("Failed to attach price %s to movie: %s", priceName, movieName));
        }
    }

    @Transactional
    @Override
    public void attachScreening(BasicScreeningDto basicScreeningDto, String priceName)
        throws AttachPriceException, UnknownPriceException {
        Objects.requireNonNull(basicScreeningDto, "ScreeningDto cannot be null for price attaching");
        Objects.requireNonNull(priceName, "Price name cannot be null for price attaching");
        Optional<ScreeningEntity> screeningEntity =
            screeningRepository.findByMovieEntity_TitleAndAndRoomEntity_NameAndStartTime(
                basicScreeningDto.getMovieName(), basicScreeningDto.getRoomName(), basicScreeningDto.getTime());
        if (screeningEntity.isPresent()) {
            attachPrice(priceName, screeningEntity.get()::addPrice);
            screeningRepository.save(screeningEntity.get());
            log.debug(String.format("Attached price %s to screening: %s", priceName, screeningEntity.get()));
        } else {
            throw new AttachPriceException(
                String.format("Failed to attach price %s to screening: %s", priceName, basicScreeningDto));
        }
    }

    private <T> void attachPrice(String priceName, Consumer<PriceEntity> addEntityConsumer)
        throws UnknownPriceException {
        Objects.requireNonNull(priceName, "Attaching price name cannot be null");
        Optional<PriceEntity> priceEntity = priceRepository.findByName(priceName);
        if (priceEntity.isEmpty()) {
            throw new UnknownPriceException(String.format("Price not found: %s", priceName));
        }
        addEntityConsumer.accept(priceEntity.get());
    }
}
