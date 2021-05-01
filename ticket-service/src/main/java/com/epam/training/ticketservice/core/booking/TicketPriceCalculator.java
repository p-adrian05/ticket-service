package com.epam.training.ticketservice.core.booking;

import com.epam.training.ticketservice.core.booking.exceptions.BookingException;
import com.epam.training.ticketservice.core.booking.model.BookingDto;
import com.epam.training.ticketservice.core.booking.persistence.entity.TicketEntity;
import com.epam.training.ticketservice.core.finance.money.Money;

import java.util.Currency;
import java.util.Optional;

public interface TicketPriceCalculator {

    Optional<Money> calculatePriceForBooking(BookingDto bookingDto,Currency currency) throws BookingException;

    Optional<Money> calculatePriceForTicket(TicketEntity ticketEntity, BookingDto bookingDto,Currency currency) throws BookingException;
}

