package com.epam.training.ticketservice.core.booking.impl;

import com.epam.training.ticketservice.core.account.persistence.entity.AccountEntity;
import com.epam.training.ticketservice.core.account.persistence.repository.AccountRepository;
import com.epam.training.ticketservice.core.booking.TicketService;
import com.epam.training.ticketservice.core.booking.exceptions.SeatAlreadyBookedException;
import com.epam.training.ticketservice.core.booking.exceptions.TicketCreateException;
import com.epam.training.ticketservice.core.booking.model.SeatDto;
import com.epam.training.ticketservice.core.booking.model.TicketDto;
import com.epam.training.ticketservice.core.booking.persistence.entity.SeatEntity;
import com.epam.training.ticketservice.core.booking.persistence.entity.SeatId;
import com.epam.training.ticketservice.core.booking.persistence.entity.TicketEntity;
import com.epam.training.ticketservice.core.booking.persistence.repository.TicketRepository;
import com.epam.training.ticketservice.core.price.persistence.entity.PriceEntity;
import com.epam.training.ticketservice.core.screening.persistence.entity.ScreeningEntity;
import com.epam.training.ticketservice.core.screening.persistence.repository.ScreeningRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TicketServiceImpl implements TicketService {

    private final ScreeningRepository screeningRepository;
    private final AccountRepository accountRepository;
    private final TicketRepository ticketRepository;

    @Override
    @Transactional
    public void createTicket(TicketDto ticketDto) throws TicketCreateException {
        Objects.requireNonNull(ticketDto, "Ticket cannot be null");
        Objects.requireNonNull(ticketDto.getScreening(), "Ticket Screening cannot be null");
        Optional<ScreeningEntity> screeningEntity = screeningRepository.
                findByMovieEntity_TitleAndAndRoomEntity_NameAndStartTime(
                        ticketDto.getScreening().getMovieName(),
                        ticketDto.getScreening().getRoomName(),
                        ticketDto.getScreening().getTime());
        Optional<AccountEntity> accountEntity = accountRepository.findByUsername(ticketDto.getUsername());
        if(screeningEntity.isEmpty()){
            throw new TicketCreateException(String.format("Screening not found for booking ticket : %s",ticketDto.getScreening()));
        }
        if(accountEntity.isEmpty()){
            throw new TicketCreateException(String.format("Account not found by username for booking ticket : %s",ticketDto.getUsername()));
        }
        if(isFreeToSeat(screeningEntity.get().getSeats(),ticketDto.getSeats())){
            TicketEntity ticketEntity = TicketEntity.builder()
                    .accountEntity(accountEntity.get())
                    .price(calculatePrice(screeningEntity.get()))
                    .build();
            TicketEntity savedTicketEntity = ticketRepository.save(ticketEntity);
            Set<SeatEntity> seats = ticketDto.getSeats().stream().map(ticket->
                    new SeatEntity(new SeatId(ticket.getRow(),ticket.getColumn()),savedTicketEntity,screeningEntity.get()))
                    .collect(Collectors.toSet());
            savedTicketEntity.setSeats(seats);
            ticketRepository.save(savedTicketEntity);
        }
    }

    public int showPrice(TicketDto ticket) throws SeatAlreadyBookedException {
        Optional<ScreeningEntity> screeningEntity = screeningRepository.
                findByMovieEntity_TitleAndAndRoomEntity_NameAndStartTime(
                        ticket.getScreening().getMovieName(),
                        ticket.getScreening().getRoomName(),
                        ticket.getScreening().getTime());
        if (screeningEntity.isPresent()) {
            if (isFreeToSeat(screeningEntity.get().getSeats(), ticket.getSeats())) {
                return calculatePrice(screeningEntity.get());
            }
        }
        return 0;
    }
    private int calculatePrice(ScreeningEntity screeningEntity){
        int moviePrice = mapPricesToValue(screeningEntity.getMovieEntity().getMoviePrices());
        int roomPrice = mapPricesToValue(screeningEntity.getMovieEntity().getMoviePrices());
        int screeningPrice = mapPricesToValue(screeningEntity.getScreeningPrices());
        return moviePrice+roomPrice+screeningPrice;
    }
    private int mapPricesToValue(Set<PriceEntity> prices){
       Optional<Integer> priceValue = prices.stream().map(PriceEntity::getValue).reduce((Integer::sum));
        return priceValue.orElse(0);
    }
    private boolean isFreeToSeat(List<SeatEntity> bookedSeats, List<SeatDto> toBookSeats) throws SeatAlreadyBookedException {
        Set<SeatDto> bookedSeatsDto = bookedSeats.stream().map(seatEntity ->
                new SeatDto(seatEntity.getId().getRowNum(),seatEntity.getId().getColNum()))
                .collect(Collectors.toSet());
        for(SeatDto seatDto : toBookSeats){
            if(bookedSeatsDto.contains(seatDto)){
                throw new SeatAlreadyBookedException(String.format("Seat is already booked: %s",seatDto));
            }
        }
        return true;
    }



}
