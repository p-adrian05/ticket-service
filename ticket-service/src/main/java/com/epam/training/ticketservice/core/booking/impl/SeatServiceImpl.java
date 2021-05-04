package com.epam.training.ticketservice.core.booking.impl;

import com.epam.training.ticketservice.core.booking.SeatService;
import com.epam.training.ticketservice.core.booking.exceptions.BookingException;
import com.epam.training.ticketservice.core.booking.model.SeatDto;
import com.epam.training.ticketservice.core.booking.persistence.entity.SeatEntity;
import com.epam.training.ticketservice.core.booking.persistence.entity.SeatId;
import com.epam.training.ticketservice.core.booking.persistence.entity.TicketEntity;

import com.epam.training.ticketservice.core.booking.persistence.repository.SeatRepository;

import com.epam.training.ticketservice.core.finance.money.Money;
import com.epam.training.ticketservice.core.price.persistence.entity.PriceEntity;
import com.epam.training.ticketservice.core.price.persistence.repository.PriceRepository;
import com.epam.training.ticketservice.core.room.persistence.entity.RoomEntity;

import com.epam.training.ticketservice.core.screening.persistence.entity.ScreeningEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.util.Currency;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SeatServiceImpl implements SeatService {

    private final PriceRepository priceRepository;
    private final SeatRepository seatRepository;

    @Override
    public boolean isFreeToSeat(Set<SeatDto> toBookSeats, ScreeningEntity screeningEntity) throws BookingException {
        Objects.requireNonNull(toBookSeats, "Seats cannot be null when checking availability for booking");
        Objects
            .requireNonNull(screeningEntity, "Screening cannot be null when checking seats availability for"
                + " booking");
        Objects.requireNonNull(screeningEntity.getRoomEntity(),
            "Screening room  entity cannot be null when checking seats availability for booking");
        RoomEntity roomEntity = screeningEntity.getRoomEntity();
        log.debug(String
            .format("Checking seats: %s availability for booking in Screening: %s", toBookSeats, screeningEntity));
        Set<SeatDto> bookedSeatsDto = convertSeatEntitiesToDto(screeningEntity.getSeats());
        for (SeatDto seatDto : toBookSeats) {
            if (bookedSeatsDto.contains(seatDto)) {
                throw new BookingException(String.format("Seat %s is already taken", seatDto));
            } else if (!isSeatExists(seatDto.getColumn(), roomEntity.getColumns())
                || !isSeatExists(seatDto.getRow(), roomEntity.getRowsNumber())) {
                throw new BookingException(String.format("Seat %s does not exist in this room", seatDto));
            }
        }
        return true;
    }

    private Set<SeatDto> convertSeatEntitiesToDto(Set<SeatEntity> seatEntities) {
        return seatEntities.stream().map(seatEntity ->
            SeatDto.of(seatEntity.getId().getRowNum(), seatEntity.getId().getColNum()))
            .collect(Collectors.toSet());
    }

    @Override
    public Optional<Money> bookSeatsToTicket(Set<SeatDto> seats, TicketEntity ticketEntity,
                                             ScreeningEntity screeningEntity) throws BookingException {
        Objects.requireNonNull(seats, "Seats cannot be null for booking seats");
        Objects.requireNonNull(ticketEntity, "Ticket entity cannot be null for booking seats");
        Objects.requireNonNull(screeningEntity,
            "Ticket's screening entity cannot be null for booking seats");
        Optional<PriceEntity> priceEntity = getBasePriceEntity();
        if (isFreeToSeat(seats, screeningEntity) && priceEntity.isPresent()) {
            ticketEntity.setScreeningEntity(screeningEntity);
            Set<SeatEntity> seatEntities = seats.stream().map(seatDto ->
                SeatEntity.builder()
                    .id(new SeatId(seatDto.getRow(), seatDto.getColumn()))
                    .ticketEntity(ticketEntity)
                    .screeningEntity(ticketEntity.getScreeningEntity())
                    .priceEntity(priceEntity.get()).build())
                .collect(Collectors.toSet());
            log.debug(
                String.format("Booked Seats: %s to Screening: %s", seatEntities, ticketEntity.getScreeningEntity()));
            seatRepository.saveAll(seatEntities);
            return Optional.of(calculateSeatPrice(seats));
        }
        return Optional.empty();
    }


    @Override
    public Money calculateSeatPrice(Set<SeatDto> seats) {
        Money price = getSeatPrice().multiply(seats.size());
        log.debug(String.format("Calculated price: %s for seats: %s", price, seats));
        return price;
    }

    private Money getSeatPrice() {
        Optional<PriceEntity> priceEntity = getBasePriceEntity();
        return priceEntity.map(entity -> new Money(entity.getValue(), Currency.getInstance(entity.getCurrency())))
            .orElseGet(() -> new Money(1500, Currency.getInstance("HUF")));
    }

    private Optional<PriceEntity> getBasePriceEntity() {
        return priceRepository.findByName("Base");
    }

    private boolean isSeatExists(int seatIndex, int max) {
        return seatIndex > 0 && seatIndex <= max;
    }


}
