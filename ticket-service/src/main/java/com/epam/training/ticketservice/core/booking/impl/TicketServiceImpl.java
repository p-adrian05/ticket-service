package com.epam.training.ticketservice.core.booking.impl;

import com.epam.training.ticketservice.core.account.persistence.entity.AccountEntity;
import com.epam.training.ticketservice.core.account.persistence.repository.AccountRepository;
import com.epam.training.ticketservice.core.booking.TicketService;
import com.epam.training.ticketservice.core.booking.exceptions.SeatBookingException;
import com.epam.training.ticketservice.core.booking.exceptions.TicketCreateException;
import com.epam.training.ticketservice.core.booking.model.SeatDto;
import com.epam.training.ticketservice.core.booking.model.TicketDto;
import com.epam.training.ticketservice.core.booking.persistence.entity.SeatEntity;
import com.epam.training.ticketservice.core.booking.persistence.entity.SeatId;
import com.epam.training.ticketservice.core.booking.persistence.entity.TicketEntity;
import com.epam.training.ticketservice.core.booking.persistence.repository.TicketRepository;
import com.epam.training.ticketservice.core.booking.exceptions.UndefinedSeatPriceException;
import com.epam.training.ticketservice.core.price.persistence.entity.PriceEntity;
import com.epam.training.ticketservice.core.price.persistence.repository.PriceRepository;
import com.epam.training.ticketservice.core.room.persistence.entity.RoomEntity;
import com.epam.training.ticketservice.core.screening.persistence.entity.ScreeningEntity;
import com.epam.training.ticketservice.core.screening.persistence.repository.ScreeningRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TicketServiceImpl implements TicketService {

    private final ScreeningRepository screeningRepository;
    private final AccountRepository accountRepository;
    private final TicketRepository ticketRepository;
    private final PriceRepository priceRepository;

    @Override
    @Transactional
    public int book(TicketDto ticketDto) throws TicketCreateException {
        Objects.requireNonNull(ticketDto, "Ticket cannot be null");
        Objects.requireNonNull(ticketDto.getScreening(), "Ticket Screening cannot be null");
        Objects.requireNonNull(ticketDto.getSeats(), "Ticket Seats cannot be null");
        Objects.requireNonNull(ticketDto.getUsername(), "Ticket username cannot be null");
        Optional<ScreeningEntity> screeningEntity = screeningRepository
            .findByMovieEntity_TitleAndAndRoomEntity_NameAndStartTime(
                ticketDto.getScreening().getMovieName(),
                ticketDto.getScreening().getRoomName(),
                ticketDto.getScreening().getTime());
        Optional<AccountEntity> accountEntity = accountRepository.findByUsername(ticketDto.getUsername());
        if (screeningEntity.isEmpty()) {
            throw new TicketCreateException(
                String.format("Screening not found for booking ticket : %s", ticketDto.getScreening()));
        }
        if (accountEntity.isEmpty()) {
            throw new TicketCreateException(
                String.format("Account not found by username for booking ticket : %s", ticketDto.getUsername()));
        }
        if (isFreeToSeat(screeningEntity.get().getSeats(), ticketDto.getSeats(),
            screeningEntity.get().getRoomEntity())) {
            TicketEntity ticketEntity = TicketEntity.builder().accountEntity(accountEntity.get()).build();
            return bookTicketWithSeats(ticketDto.getSeats(), ticketEntity, screeningEntity.get()).getPrice();
        }
        throw new TicketCreateException("Failed to create ticket");
    }

    private TicketEntity bookTicketWithSeats(Set<SeatDto> seats, TicketEntity ticketEntity,
                                             ScreeningEntity screeningEntity)
        throws UndefinedSeatPriceException {
        PriceEntity priceEntity = getSeatPrice();
        Set<SeatEntity> seatEntities = seats.stream().map(seatDto ->
            SeatEntity.builder()
                .id(new SeatId(seatDto.getRow(), seatDto.getColumn()))
                .ticketEntity(ticketEntity)
                .screeningEntity(screeningEntity)
                .priceEntity(priceEntity).build())
            .collect(Collectors.toSet());
        ticketEntity.setPrice(calculatePrice(screeningEntity, seats));
        ticketEntity.setSeats(seatEntities);
        return ticketRepository.save(ticketEntity);
    }

    private PriceEntity getSeatPrice() throws UndefinedSeatPriceException {
        return priceRepository.findByName("Base")
            .orElseThrow(() -> new UndefinedSeatPriceException("Undefined seat price"));
    }

    public int showPrice(TicketDto ticket) throws SeatBookingException, UndefinedSeatPriceException {
        Optional<ScreeningEntity> screeningEntity = screeningRepository
            .findByMovieEntity_TitleAndAndRoomEntity_NameAndStartTime(
                ticket.getScreening().getMovieName(),
                ticket.getScreening().getRoomName(),
                ticket.getScreening().getTime());
        if (screeningEntity.isPresent()) {
            if (isFreeToSeat(screeningEntity.get().getSeats(), ticket.getSeats(),
                screeningEntity.get().getRoomEntity())) {
                return calculatePrice(screeningEntity.get(), ticket.getSeats());
            }
        }
        return -1;
    }

    private int calculatePrice(ScreeningEntity screeningEntity, Set<SeatDto> seats) throws UndefinedSeatPriceException {
        PriceEntity seatPriceEntity = getSeatPrice();
        int seatPrice = seatPriceEntity.getValue() * seats.size();
        int moviePrice = mapPricesToValue(screeningEntity.getMovieEntity().getMoviePrices());
        int roomPrice = mapPricesToValue(screeningEntity.getRoomEntity().getRoomPrices());
        int screeningPrice = mapPricesToValue(screeningEntity.getScreeningPrices());
        return moviePrice + roomPrice + screeningPrice + seatPrice;
    }

    private int mapPricesToValue(Set<PriceEntity> prices) {
        Optional<Integer> priceValue = prices.stream().map(PriceEntity::getValue).reduce((Integer::sum));
        return priceValue.orElse(0);
    }

    private boolean isFreeToSeat(Set<SeatEntity> bookedSeats, Set<SeatDto> toBookSeats, RoomEntity roomEntity)
        throws SeatBookingException {
        Set<SeatDto> bookedSeatsDto = bookedSeats.stream().map(seatEntity ->
            SeatDto.of(seatEntity.getId().getRowNum(), seatEntity.getId().getColNum()))
            .collect(Collectors.toSet());
        for (SeatDto seatDto : toBookSeats) {
            if (bookedSeatsDto.contains(seatDto)) {
                throw new SeatBookingException(String.format("Seat is already booked: %s", seatDto));
            } else if (!isSeatExists(seatDto.getColumn(), roomEntity.getColumns())
                || !isSeatExists(seatDto.getRow(), roomEntity.getRows())) {
                throw new SeatBookingException(String.format("Seat not exists in the room %s", seatDto));
            }
        }
        return true;
    }

    private boolean isSeatExists(int seatIndex, int max) {
        return seatIndex > 0 && seatIndex <= max;
    }


}
