package com.epam.training.ticketservice.core.booking.impl;

import com.epam.training.ticketservice.core.booking.SeatService;
import com.epam.training.ticketservice.core.booking.exceptions.BookingException;
import com.epam.training.ticketservice.core.booking.model.BookingDto;
import com.epam.training.ticketservice.core.booking.persistence.repository.TicketRepository;
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

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TicketServiceImpl implements TicketService {

    private final ScreeningRepository screeningRepository;
    private final UserRepository userRepository;
    private final SeatService seatService;
    private final TicketRepository ticketRepository;

    @Override
    @Transactional
    public TicketDto book(BookingDto bookingDto, String username) throws BookingException {
        Objects.requireNonNull(bookingDto, "BookingDto cannot be null");
        Objects.requireNonNull(bookingDto.getScreening(), "BookingDto Screening cannot be null");
        Objects.requireNonNull(bookingDto.getSeats(), "BookingDto Seats cannot be null");
        Objects.requireNonNull(username, "Username cannot be null");
        Optional<ScreeningEntity> screeningEntity = screeningRepository
            .findByMovieEntity_TitleAndAndRoomEntity_NameAndStartTime(
                bookingDto.getScreening().getMovieName(),
                bookingDto.getScreening().getRoomName(),
                bookingDto.getScreening().getTime());
        if (screeningEntity.isEmpty()) {
            throw new BookingException(String.format("Screening not exists %s", bookingDto.getScreening()));
        }
        Optional<UserEntity> accountEntity = userRepository.findByUsername(username);
        if (accountEntity.isEmpty()) {
            throw new BookingException(String.format("User not exists %s", bookingDto.getScreening()));
        }
        TicketEntity ticketEntity = TicketEntity
            .builder()
            .userEntity(accountEntity.get())
            .screeningEntity(screeningEntity.get())
            .price(calculatePrice(screeningEntity.get(), bookingDto.getSeats())).build();
        TicketEntity createdTicket = ticketRepository.save(ticketEntity);
        seatService.bookSeatsToTicket(bookingDto, ticketEntity);

        return TicketDto.builder()
            .screening(bookingDto.getScreening())
            .price(createdTicket.getPrice())
            .username(username)
            .seats(bookingDto.getSeats()).build();
    }


    @Override
    public Optional<Integer> showPrice(BookingDto bookingDto) throws BookingException {
        Optional<ScreeningEntity> screeningEntity = screeningRepository
            .findByMovieEntity_TitleAndAndRoomEntity_NameAndStartTime(
                bookingDto.getScreening().getMovieName(),
                bookingDto.getScreening().getRoomName(),
                bookingDto.getScreening().getTime());
        if (screeningEntity.isPresent()) {
            if (seatService.isFreeToSeat(bookingDto.getSeats(), screeningEntity.get())) {
                return Optional.of(calculatePrice(screeningEntity.get(), bookingDto.getSeats()));
            }
        }
        return Optional.empty();
    }

    public List<TicketDto> getTicketsByUsername(String username) {
        Set<TicketEntity> ticketEntities =
            new HashSet<>(ticketRepository.findTicketEntitiesByUserEntityUsername(username));
        return ticketEntities.stream().map((ticketEntity) -> convertTicketEntityToDto(ticketEntity, username))
            .collect(Collectors.toList());
    }

    private Integer calculatePrice(ScreeningEntity screeningEntity, Set<SeatDto> seats) {
        int seatPrice = seatService.calculateSeatPrice(seats);
        int moviePrice = mapPricesToValue(screeningEntity.getMovieEntity().getMoviePrices());
        int roomPrice = mapPricesToValue(screeningEntity.getRoomEntity().getRoomPrices());
        int screeningPrice = mapPricesToValue(screeningEntity.getScreeningPrices());
        return moviePrice + roomPrice + screeningPrice + seatPrice;
    }

    private int mapPricesToValue(Set<PriceEntity> prices) {
        Optional<Integer> priceValue = prices.stream().map(PriceEntity::getValue).reduce((Integer::sum));
        return priceValue.orElse(0);
    }

    private TicketDto convertTicketEntityToDto(TicketEntity ticketEntity, String username) {
        return TicketDto.builder()
            .seats(ticketEntity.getSeats().stream()
                .map(seatEntity -> SeatDto.of(seatEntity.getId().getRowNum(), seatEntity.getId().getColNum())).collect(
                    Collectors.toSet()))
            .screening(BasicScreeningDto.builder()
                .movieName(ticketEntity.getScreeningEntity().getMovieEntity().getTitle())
                .roomName(ticketEntity.getScreeningEntity().getRoomEntity().getName())
                .time(ticketEntity.getScreeningEntity().getStartTime())
                .build())
            .username(username)
            .price(ticketEntity.getPrice())
            .build();
    }

}
