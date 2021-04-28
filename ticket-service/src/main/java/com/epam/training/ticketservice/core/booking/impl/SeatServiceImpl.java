package com.epam.training.ticketservice.core.booking.impl;

import com.epam.training.ticketservice.core.booking.SeatService;
import com.epam.training.ticketservice.core.booking.exceptions.BookingException;
import com.epam.training.ticketservice.core.booking.model.BookingDto;
import com.epam.training.ticketservice.core.booking.model.SeatDto;
import com.epam.training.ticketservice.core.booking.persistence.entity.SeatEntity;
import com.epam.training.ticketservice.core.booking.persistence.entity.SeatId;
import com.epam.training.ticketservice.core.booking.persistence.entity.TicketEntity;

import com.epam.training.ticketservice.core.booking.persistence.repository.SeatRepository;

import com.epam.training.ticketservice.core.price.persistence.entity.PriceEntity;
import com.epam.training.ticketservice.core.price.persistence.repository.PriceRepository;
import com.epam.training.ticketservice.core.room.persistence.entity.RoomEntity;

import com.epam.training.ticketservice.core.screening.persistence.entity.ScreeningEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;



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
        RoomEntity roomEntity = screeningEntity.getRoomEntity();
        Set<SeatDto> bookedSeatsDto = convertSeatEntitiesToDto(screeningEntity.getSeats());
        for (SeatDto seatDto : toBookSeats) {
            if (bookedSeatsDto.contains(seatDto)) {
               throw new BookingException(String.format("Seat is %s is already taken",seatDto));
            } else if (!isSeatExists(seatDto.getColumn(), roomEntity.getColumns())
                || !isSeatExists(seatDto.getRow(), roomEntity.getRows())) {
                throw new BookingException(String.format("Seat %s does not exist in this room",seatDto));
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
    public void bookSeatsToTicket(BookingDto bookingDto,TicketEntity ticketEntity) throws BookingException {
        if (isFreeToSeat(bookingDto.getSeats(), ticketEntity.getScreeningEntity())) {
                PriceEntity priceEntity = getSeatPriceEntity();
                Set<SeatEntity> seatEntities = bookingDto.getSeats().stream().map(seatDto ->
                    SeatEntity.builder()
                        .id(new SeatId(seatDto.getRow(), seatDto.getColumn()))
                        .ticketEntity(ticketEntity)
                        .screeningEntity(ticketEntity.getScreeningEntity())
                        .priceEntity(priceEntity).build())
                    .collect(Collectors.toSet());
                seatRepository.saveAll(seatEntities);
            }
    }


    @Override
    public int calculateSeatPrice(Set<SeatDto> seats) {
        return calculateSeatPrice(seats, getSeatPriceEntity());
    }

    private int calculateSeatPrice(Set<SeatDto> seats, PriceEntity priceEntity) {
        return priceEntity.getValue() * seats.size();
    }

    private PriceEntity getSeatPriceEntity() {
        return priceRepository.findByName("Base")
            .orElseGet(
                () -> priceRepository.save(PriceEntity.builder().value(1500).name("Base").currency("HUF").build()));
    }

    private boolean isSeatExists(int seatIndex, int max) {
        return seatIndex > 0 && seatIndex <= max;
    }


}
