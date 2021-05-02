package com.epam.training.ticketservice.core.booking.impl;

import com.epam.training.ticketservice.core.booking.SeatService;
import com.epam.training.ticketservice.core.booking.TicketPriceCalculator;
import com.epam.training.ticketservice.core.booking.exceptions.BookingException;
import com.epam.training.ticketservice.core.booking.model.BookingDto;
import com.epam.training.ticketservice.core.booking.persistence.entity.TicketEntity;
import com.epam.training.ticketservice.core.finance.bank.Bank;
import com.epam.training.ticketservice.core.finance.money.Money;
import com.epam.training.ticketservice.core.price.persistence.entity.PriceEntity;
import com.epam.training.ticketservice.core.screening.model.BasicScreeningDto;
import com.epam.training.ticketservice.core.screening.persistence.entity.ScreeningEntity;
import com.epam.training.ticketservice.core.screening.persistence.repository.ScreeningRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Currency;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

@RequiredArgsConstructor
@Service
@Slf4j
public class TicketPriceServiceImpl implements TicketPriceCalculator {

    private final Bank bank;
    private final ScreeningRepository screeningRepository;
    private final SeatService seatService;

    @Override
    @Transactional
    public Optional<Money> calculatePriceForBooking(BookingDto bookingDto, Currency currency) throws BookingException {
        Objects.requireNonNull(bookingDto, "BookingDto cannot be when for calculating price");
        Objects.requireNonNull(currency, "Currency cannot be null when for calculating price");
        ScreeningEntity screeningEntity = queryScreening(bookingDto.getScreening());
        log.debug(String.format("Calculating price for booking %s", bookingDto));
        if (seatService.isFreeToSeat(bookingDto.getSeats(), screeningEntity)) {
            Money price =
                calculateAggregatedPrice(screeningEntity, () -> seatService.calculateSeatPrice(bookingDto.getSeats()),
                    currency).multiply(bookingDto.getSeats().size());
            log.debug(String.format("Calculated price: %s for booking:  %s", price, bookingDto));
            return Optional.of(price);
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public Optional<Money> calculatePriceForTicket(TicketEntity ticketEntity, BookingDto bookingDto, Currency currency)
        throws BookingException {
        Objects.requireNonNull(ticketEntity, "TicketEntity cannot be null when calculating price");
        Objects.requireNonNull(bookingDto, "BookingDto cannot be when calculating price");
        Objects.requireNonNull(currency, "Currency cannot be null when calculating price");
        ScreeningEntity screeningEntity = queryScreening(bookingDto.getScreening());
        Optional<Money> seatPrice =
            seatService.bookSeatsToTicket(bookingDto.getSeats(), ticketEntity, screeningEntity);
        if (seatPrice.isPresent()) {
            Money price =
                calculateAggregatedPrice(screeningEntity, seatPrice::get, currency)
                    .multiply(bookingDto.getSeats().size());
            log.debug(
                String.format("Calculated price: %s for ticket: %s with booking: %s", price, ticketEntity, bookingDto));
            return Optional.of(price);
        }
        return Optional.empty();
    }

    private ScreeningEntity queryScreening(BasicScreeningDto basicScreeningDto)
        throws BookingException {
        Objects.requireNonNull(basicScreeningDto, "BasicScreeningDto cannot be null");
        Objects.requireNonNull(basicScreeningDto.getMovieName(), "BasicScreeningDto movie name cannot be null");
        Objects.requireNonNull(basicScreeningDto.getRoomName(), "BasicScreeningDto room name cannot be null");
        Objects.requireNonNull(basicScreeningDto.getTime(), "BasicScreeningDto time cannot be null");
        Optional<ScreeningEntity> screeningEntity = screeningRepository
            .findByMovieEntity_TitleAndAndRoomEntity_NameAndStartTime(
                basicScreeningDto.getMovieName(),
                basicScreeningDto.getRoomName(),
                basicScreeningDto.getTime());
        return screeningEntity.orElseThrow(
            () -> new BookingException(String.format("Screening not exists %s", basicScreeningDto)));
    }

    private Money calculatePriceForScreening(List<PriceEntity> screeningPrices, Currency currency) {
        Money aggregatedPrice = new Money(0D, currency);
        for (PriceEntity priceEntity : screeningPrices) {
            aggregatedPrice = aggregatedPrice
                .add(new Money(priceEntity.getValue(), Currency.getInstance(priceEntity.getCurrency())), bank);
        }
        return aggregatedPrice;
    }

    private Money calculateAggregatedPrice(ScreeningEntity screeningEntity, Supplier<Money> seatPriceSupplier,
                                           Currency currency) {
        Money seatPrice = seatPriceSupplier.get();
        Money screeningPrice = calculatePriceForScreening(screeningEntity.prices(), currency);
        return seatPrice.add(screeningPrice, bank).to(currency, bank);
    }

}
