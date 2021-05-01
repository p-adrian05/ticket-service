package com.epam.training.ticketservice.core.booking.impl;

import com.epam.training.ticketservice.core.booking.SeatService;
import com.epam.training.ticketservice.core.booking.exceptions.BookingException;
import com.epam.training.ticketservice.core.booking.model.BookingDto;
import com.epam.training.ticketservice.core.booking.persistence.repository.TicketRepository;
import com.epam.training.ticketservice.core.finance.bank.Bank;
import com.epam.training.ticketservice.core.finance.money.Money;
import com.epam.training.ticketservice.core.price.persistence.repository.PriceRepository;
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
import org.springframework.beans.propertyeditors.CurrencyEditor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.lang.reflect.MalformedParameterizedTypeException;
import java.util.Currency;
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
    private final Bank bank;

    @Override
    @Transactional
    public TicketDto book(BookingDto bookingDto, String username, String currency) throws BookingException {
        Objects.requireNonNull(bookingDto, "BookingDto cannot be null");
        Objects.requireNonNull(bookingDto.getScreening(), "BookingDto Screening cannot be null");
        Objects.requireNonNull(bookingDto.getSeats(), "BookingDto Seats cannot be null");
        Objects.requireNonNull(username, "Username cannot be null");
        Objects.requireNonNull(username, "Currency cannot be null");
        Currency bookingCurrency = Currency.getInstance(currency);
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
            throw new BookingException(String.format("User not exists %s", username));
        }

        TicketEntity ticketEntity = TicketEntity
            .builder()
            .userEntity(accountEntity.get())
            .screeningEntity(screeningEntity.get())
            .build();
        TicketEntity cratedTicket = ticketRepository.save(ticketEntity);
        Optional<Money> seatPrice = seatService.bookSeatsToTicket(bookingDto.getSeats(), cratedTicket);
        if (seatPrice.isPresent()) {
            Money screeningPrice = calculatePriceForScreening(screeningEntity.get().prices(), bookingCurrency);
            Money aggregatedPrice = screeningPrice.add(seatPrice.get(), bank).to(bookingCurrency, bank);
            cratedTicket.setPrice(screeningPrice.getAmount());
            cratedTicket.setCurrency(screeningPrice.getCurrency().toString());
            ticketRepository.save(cratedTicket);
            return TicketDto.builder()
                .screening(bookingDto.getScreening())
                .price(aggregatedPrice)
                .username(username)
                .seats(bookingDto.getSeats()).build();
        }
        throw new BookingException(String.format("Failed to book ticket"));
    }

    @Override
    public Optional<Money> showPrice(BookingDto bookingDto, String currency) throws BookingException {
        Optional<ScreeningEntity> screeningEntity = screeningRepository
            .findByMovieEntity_TitleAndAndRoomEntity_NameAndStartTime(
                bookingDto.getScreening().getMovieName(),
                bookingDto.getScreening().getRoomName(),
                bookingDto.getScreening().getTime());
        if (screeningEntity.isPresent()) {
            if (seatService.isFreeToSeat(bookingDto.getSeats(), screeningEntity.get())) {
                Money seatPrice = seatService.calculateSeatPrice(bookingDto.getSeats());
                Money screeningPrice =
                    calculatePriceForScreening(screeningEntity.get().prices(), Currency.getInstance(currency));
                Money aggregatedPrice = seatPrice.add(screeningPrice, bank).to(currency, bank);
                return Optional.of(aggregatedPrice);
            }
        }
        return Optional.empty();
    }

    private Money calculatePriceForScreening(List<PriceEntity> screeningPrices, Currency currency) {
        Money aggregatedPrice = new Money(0D, currency);
        for (PriceEntity priceEntity : screeningPrices) {
            aggregatedPrice = aggregatedPrice
                .add(new Money(priceEntity.getValue(), Currency.getInstance(priceEntity.getCurrency())), bank);
        }
        return aggregatedPrice;
    }

    @Override
    public List<TicketDto> getTicketsByUsername(String username) {
        Set<TicketEntity> ticketEntities =
            new HashSet<>(ticketRepository.findTicketEntitiesByUserEntityUsername(username));
        return ticketEntities.stream().map((ticketEntity) -> convertTicketEntityToDto(ticketEntity, username))
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
