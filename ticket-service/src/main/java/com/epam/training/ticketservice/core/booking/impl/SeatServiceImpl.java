package com.epam.training.ticketservice.core.booking.impl;

import com.epam.training.ticketservice.core.booking.SeatService;
import com.epam.training.ticketservice.core.booking.model.SeatDto;
import com.epam.training.ticketservice.core.booking.persistence.entity.SeatEntity;
import com.epam.training.ticketservice.core.booking.persistence.entity.SeatId;
import com.epam.training.ticketservice.core.booking.persistence.entity.TicketEntity;

import com.epam.training.ticketservice.core.booking.persistence.repository.TicketRepository;
import com.epam.training.ticketservice.core.price.persistence.entity.PriceEntity;
import com.epam.training.ticketservice.core.price.persistence.repository.PriceRepository;
import com.epam.training.ticketservice.core.room.persistence.entity.RoomEntity;
import com.epam.training.ticketservice.core.screening.model.BasicScreeningDto;
import com.epam.training.ticketservice.core.screening.persistence.entity.ScreeningEntity;
import com.epam.training.ticketservice.core.screening.persistence.repository.ScreeningRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SeatServiceImpl implements SeatService {

    private final ScreeningRepository screeningRepository;
    private final TicketRepository ticketRepository;
    private final PriceRepository priceRepository;

    @Override
    public boolean isFreeToSeat(Set<SeatDto> toBookSeats, BasicScreeningDto screeningDto) {
        Optional<ScreeningEntity> screeningEntity = queryScreeningEntity(screeningDto);
        if (screeningEntity.isEmpty()) {
            return false;
        }
        return isFreeToSeat(toBookSeats, screeningEntity.get());
    }

    private boolean isFreeToSeat(Set<SeatDto> toBookSeats, ScreeningEntity screeningEntity) {
        RoomEntity roomEntity = screeningEntity.getRoomEntity();
        Set<SeatDto> bookedSeatsDto = convertSeatEntitiesToDto(screeningEntity.getSeats());
        for (SeatDto seatDto : toBookSeats) {
            if (bookedSeatsDto.contains(seatDto)) {
                return false;
            } else if (!isSeatExists(seatDto.getColumn(), roomEntity.getColumns())
                || !isSeatExists(seatDto.getRow(), roomEntity.getRows())) {
                return false;
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
    public void bookSeatsToScreening(Set<SeatDto> toBookSeats, BasicScreeningDto screeningDto,
                                     TicketEntity ticketEntity) {
        Optional<ScreeningEntity> screeningEntity = queryScreeningEntity(screeningDto);
        if (screeningEntity.isPresent()) {
            if (isFreeToSeat(toBookSeats, screeningEntity.get())) {
                PriceEntity priceEntity = getSeatPriceEntity();
                Set<SeatEntity> seatEntities = toBookSeats.stream().map(seatDto ->
                    SeatEntity.builder()
                        .id(new SeatId(seatDto.getRow(), seatDto.getColumn()))
                        .ticketEntity(ticketEntity)
                        .screeningEntity(screeningEntity.get())
                        .priceEntity(priceEntity).build())
                    .collect(Collectors.toSet());
                ticketEntity.setSeats(seatEntities);
                ticketRepository.save(ticketEntity);
            }
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

    private Optional<ScreeningEntity> queryScreeningEntity(BasicScreeningDto screeningDto) {
        return screeningRepository
            .findByMovieEntity_TitleAndAndRoomEntity_NameAndStartTime(
                screeningDto.getMovieName(),
                screeningDto.getRoomName(),
                screeningDto.getTime());
    }

}
