package com.epam.training.ticketservice.core.price.impl;

import com.epam.training.ticketservice.core.movie.persistence.repository.MovieRepository;
import com.epam.training.ticketservice.core.price.PriceService;
import com.epam.training.ticketservice.core.price.exceptions.AttachPriceException;
import com.epam.training.ticketservice.core.price.exceptions.PriceAlreadyExistsException;
import com.epam.training.ticketservice.core.price.exceptions.UnknownPriceException;
import com.epam.training.ticketservice.core.price.model.PriceDto;
import com.epam.training.ticketservice.core.price.persistence.entity.PriceEntity;
import com.epam.training.ticketservice.core.price.persistence.repository.PriceRepository;
import com.epam.training.ticketservice.core.room.persistence.repository.RoomRepository;
import com.epam.training.ticketservice.core.screening.model.CreateScreeningDto;
import com.epam.training.ticketservice.core.screening.persistence.entity.ScreeningEntity;
import com.epam.training.ticketservice.core.screening.persistence.repository.ScreeningRepository;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@RequiredArgsConstructor
@Service
@Slf4j
public class PriceServiceImpl implements PriceService {

    private final PriceRepository priceRepository;
    private final RoomRepository roomRepository;
    private final ScreeningRepository screeningRepository;
    private final MovieRepository movieRepository;

    @Override
    @Transactional
    public void createPrice(PriceDto priceDto) throws PriceAlreadyExistsException {
        Objects.requireNonNull(priceDto, "Price cannot be null");
        Objects.requireNonNull(priceDto.getName(), "Price name cannot be null");
        Objects.requireNonNull(priceDto.getValue(), "Price value cannot be null");
        Objects.requireNonNull(priceDto.getCurrency(), "Currency value cannot be null");
        if (priceRepository.existsByName(priceDto.getName())) {
            throw new PriceAlreadyExistsException(
                String.format("Price already exists by name: %s", priceDto.getName()));
        }
        log.debug("Creating new Price : {}", priceDto);
        PriceEntity priceEntity = PriceEntity.builder()
            .currency(priceDto.getCurrency().getCurrencyCode())
            .name(priceDto.getName())
            .value(priceDto.getValue())
            .build();
        PriceEntity createdPrice = priceRepository.save(priceEntity);
        log.debug("Created Price is : {}", createdPrice);
    }

    @Transactional
    @Override
    public void updatePrice(PriceDto priceDto) throws UnknownPriceException {
        Objects.requireNonNull(priceDto, "Price cannot be null");
        Objects.requireNonNull(priceDto.getName(), "Price name cannot be null");
        Objects.requireNonNull(priceDto.getValue(), "Price value cannot be null");
        PriceEntity priceEntity = getPriceEntity(priceDto.getName());
        log.debug("Price before update: {}", priceEntity);
        priceEntity.setValue(priceDto.getValue());
        priceRepository.save(priceEntity);
        log.debug("Updated Price is : {}", priceEntity);
    }

    @Transactional
    @Override
    public void attachRoom(String roomName, String priceName) throws AttachPriceException, UnknownPriceException {
        PriceEntity priceEntity = getPriceEntity(priceName);
        attachPrice(roomName, roomRepository::findByName, priceEntity::addRoom);
        priceRepository.save(priceEntity);
    }

    @Transactional
    @Override
    public void attachMovie(String movieName, String priceName) throws AttachPriceException, UnknownPriceException {
        PriceEntity priceEntity = getPriceEntity(priceName);
        attachPrice(movieName, movieRepository::findMovieEntityByTitle, priceEntity::addMovie);
        priceRepository.save(priceEntity);
    }

    @Transactional
    @Override
    public void attachScreening(CreateScreeningDto createScreeningDto, String priceName)
        throws AttachPriceException, UnknownPriceException {
        PriceEntity priceEntity = getPriceEntity(priceName);
        Optional<ScreeningEntity> screeningEntityOptional =
            screeningRepository.findByMovieEntity_TitleAndAndRoomEntity_NameAndStartTime(
                createScreeningDto.getMovieName(), createScreeningDto.getRoomName(), createScreeningDto.getTime());
        ScreeningEntity screeningEntity = screeningEntityOptional.orElseThrow(
            () -> new AttachPriceException(String.format("Failed to attach price to %s", createScreeningDto)));
        priceEntity.addScreening(screeningEntity);
        priceRepository.save(priceEntity);
    }

    private <T> void attachPrice(String entityName, Function<String, Optional<T>> query, Consumer<T> addEntityConsumer)
        throws AttachPriceException {
        Objects.requireNonNull(entityName, "Attaching entity name cannot be null");
        Optional<T> entity = query.apply(entityName);
        if (entity.isEmpty()) {
            throw new AttachPriceException(String.format("Failed to attach price to %s", entity));
        }
        addEntityConsumer.accept(entity.get());
    }

    private PriceEntity getPriceEntity(String name) throws UnknownPriceException {
        Objects.requireNonNull(name, "Price name cannot be null");
        Optional<PriceEntity> priceEntity = priceRepository.findByName(name);
        if (priceEntity.isEmpty()) {
            throw new UnknownPriceException(String.format("Price not found: %s", name));
        }
        return priceEntity.get();
    }
}
