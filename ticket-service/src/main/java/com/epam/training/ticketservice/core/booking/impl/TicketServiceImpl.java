package com.epam.training.ticketservice.core.booking.impl;

import com.epam.training.ticketservice.core.booking.TicketPriceCalculator;
import com.epam.training.ticketservice.core.booking.TicketService;
import com.epam.training.ticketservice.core.booking.exceptions.BookingException;
import com.epam.training.ticketservice.core.booking.model.BookingDto;
import com.epam.training.ticketservice.core.booking.model.SeatDto;
import com.epam.training.ticketservice.core.booking.model.TicketDto;
import com.epam.training.ticketservice.core.booking.persistence.entity.TicketEntity;
import com.epam.training.ticketservice.core.booking.persistence.repository.TicketRepository;
import com.epam.training.ticketservice.core.finance.money.Money;
import com.epam.training.ticketservice.core.screening.model.BasicScreeningDto;
import com.epam.training.ticketservice.core.user.persistence.entity.UserEntity;
import com.epam.training.ticketservice.core.user.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Currency;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class TicketServiceImpl implements TicketService {

    private final UserRepository userRepository;
    private final TicketPriceCalculator ticketPriceCalculator;
    private final TicketRepository ticketRepository;

    @Override
    @Transactional
    public TicketDto book(BookingDto bookingDto, String username, String currency) throws BookingException {
        Objects.requireNonNull(bookingDto, "BookingDto cannot be null");
        Objects.requireNonNull(bookingDto.getScreening(), "BookingDto Screening cannot be null");
        Objects.requireNonNull(bookingDto.getSeats(), "BookingDto Seats cannot be null");
        Objects.requireNonNull(username, "Username cannot be null");
        Objects.requireNonNull(username, "Currency cannot be null");
        Currency bookingCurrency = Currency.getInstance(currency);
        Optional<UserEntity> accountEntity = userRepository.findByUsername(username);
        if (accountEntity.isEmpty()) {
            throw new BookingException(String.format("User not exists %s", username));
        }
        TicketEntity ticketEntity = TicketEntity
            .builder()
            .userEntity(accountEntity.get())
            .build();
        TicketEntity createdTicket = ticketRepository.save(ticketEntity);
        log.debug(String.format("Created empty Ticket: %s", ticketEntity));
        Optional<Money> ticketPrice =
            ticketPriceCalculator.calculatePriceForTicket(ticketEntity, bookingDto, bookingCurrency);
        if (ticketPrice.isPresent()) {
            createdTicket.setPrice(ticketPrice.get().getAmount());
            createdTicket.setCurrency(ticketPrice.get().getCurrency().toString());

            TicketDto resultTicketDto = TicketDto.builder()
                .screening(bookingDto.getScreening())
                .username(username)
                .price(ticketPrice.get())
                .seats(bookingDto.getSeats())
                .build();
            log.debug(String.format("Created ticket: %s", resultTicketDto));
            return resultTicketDto;
        }
        throw new BookingException("Failed to book ticket");
    }

    @Override
    public Optional<Money> showPrice(BookingDto bookingDto, String currency) throws BookingException {
        Objects.requireNonNull(bookingDto, "BookingDto cannot be null");
        Objects.requireNonNull(currency, "Currency cannot be null");
        return ticketPriceCalculator.calculatePriceForBooking(bookingDto, Currency.getInstance(currency));
    }

    @Override
    public List<TicketDto> getTicketsByUsername(String username) {
        Objects.requireNonNull(username, "Username cannot be null");
        return ticketRepository.findTicketEntitiesByUserEntityUsername(username).stream()
            .map((ticketEntity) -> convertTicketEntityToDto(ticketEntity, username))
            .collect(Collectors.toList());
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
            .price(new Money(ticketEntity.getPrice(), Currency.getInstance(ticketEntity.getCurrency())))
            .build();
    }

}
