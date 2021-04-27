package com.epam.training.ticketservice.core.booking.impl;

import com.epam.training.ticketservice.core.booking.SeatService;
import com.epam.training.ticketservice.core.screening.model.BasicScreeningDto;
import com.epam.training.ticketservice.core.user.persistence.entity.UserEntity;
import com.epam.training.ticketservice.core.user.persistence.repository.UserRepository;
import com.epam.training.ticketservice.core.booking.TicketService;
import com.epam.training.ticketservice.core.booking.model.SeatDto;
import com.epam.training.ticketservice.core.booking.model.TicketDto;
import com.epam.training.ticketservice.core.booking.persistence.entity.TicketEntity;
import com.epam.training.ticketservice.core.price.persistence.entity.PriceEntity;
import com.epam.training.ticketservice.core.screening.persistence.entity.ScreeningEntity;
import com.epam.training.ticketservice.core.screening.persistence.repository.ScreeningRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class TicketServiceImpl implements TicketService {

    private final ScreeningRepository screeningRepository;
    private final UserRepository userRepository;
    private final SeatService seatService;

    @Override
    @Transactional
    public Optional<Integer> book(TicketDto ticketDto){
        Objects.requireNonNull(ticketDto, "Ticket cannot be null");
        Objects.requireNonNull(ticketDto.getScreening(), "Ticket Screening cannot be null");
        Objects.requireNonNull(ticketDto.getSeats(), "Ticket Seats cannot be null");
        Objects.requireNonNull(ticketDto.getUsername(), "Ticket username cannot be null");

        Optional<Integer> sumPrice = calculatePrice(ticketDto.getScreening(),ticketDto.getSeats());
        Optional<UserEntity> accountEntity = userRepository.findByUsername(ticketDto.getUsername());
        if (accountEntity.isEmpty() || sumPrice.isEmpty()) {
          return Optional.empty();
        }
        TicketEntity ticketEntity = TicketEntity
            .builder()
            .userEntity(accountEntity.get())
            .price(sumPrice.get()).build();
        seatService.bookSeatsToScreening(ticketDto.getSeats(),ticketDto.getScreening(),ticketEntity);
        return sumPrice;
    }


    @Override
    public Optional<Integer> showPrice(TicketDto ticket){
        if (seatService.isFreeToSeat(ticket.getSeats(),ticket.getScreening())) {
            return calculatePrice(ticket.getScreening(), ticket.getSeats());
        }
        return Optional.empty();
    }

    private Optional<Integer> calculatePrice(BasicScreeningDto screeningDto, Set<SeatDto> seats)  {
        Optional<ScreeningEntity> screeningEntity = screeningRepository
            .findByMovieEntity_TitleAndAndRoomEntity_NameAndStartTime(
                screeningDto.getMovieName(),
                screeningDto.getRoomName(),
                screeningDto.getTime());
        if(screeningEntity.isEmpty()){
            return Optional.empty();
        }
        int seatPrice =  seatService.calculateSeatPrice(seats);
        int moviePrice = mapPricesToValue(screeningEntity.get().getMovieEntity().getMoviePrices());
        int roomPrice = mapPricesToValue(screeningEntity.get().getRoomEntity().getRoomPrices());
        int screeningPrice = mapPricesToValue(screeningEntity.get().getScreeningPrices());
        return Optional.of(moviePrice + roomPrice + screeningPrice + seatPrice);
    }

    private int mapPricesToValue(Set<PriceEntity> prices) {
        Optional<Integer> priceValue = prices.stream().map(PriceEntity::getValue).reduce((Integer::sum));
        return priceValue.orElse(0);
    }

}
