package com.epam.training.ticketservice.ui.command;

import com.epam.training.ticketservice.core.booking.TicketService;
import com.epam.training.ticketservice.core.booking.exceptions.BookingException;
import com.epam.training.ticketservice.core.booking.model.BookingDto;
import com.epam.training.ticketservice.core.booking.model.SeatDto;
import com.epam.training.ticketservice.core.booking.model.TicketDto;
import com.epam.training.ticketservice.core.finance.money.Money;
import com.epam.training.ticketservice.core.screening.model.BasicScreeningDto;
import com.epam.training.ticketservice.core.user.LoginService;
import com.epam.training.ticketservice.ui.util.UserAvailability;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@ShellComponent
@Slf4j
@RequiredArgsConstructor
public class TicketBookingCommand {

    private final TicketService ticketService;
    private final UserAvailability userAvailability;
    private final LoginService loginService;

    @ShellMethodAvailability("isAvailable")
    @ShellMethod(value = "User create booking", key = "book")
    public String crateBooking(String movieName, String roomName, LocalDateTime time, Set<SeatDto> seats) {
        BookingDto bookingDto = BookingDto.builder()
            .screening(BasicScreeningDto.builder()
                .roomName(roomName)
                .movieName(movieName)
                .time(time)
                .build())
            .seats(seats)
            .build();
        try {
            TicketDto ticketDto =
                ticketService.book(bookingDto, loginService.getLoggedInUser().get().getUsername(), "HUF");
            return String.format("Seats booked: %s; the price for this booking is %s",
                SeatDto.seatsToString(ticketDto.getSeats()),
                ticketDto.getPrice());
        } catch (BookingException e) {
            return e.getMessage();
        }
    }

    @ShellMethod(value = "show price for booking", key = "show price for")
    public String showPrice(String movieName, String roomName, LocalDateTime time, Set<SeatDto> seats) {
        Optional<Money> price;
        try {
            price = ticketService.showPrice(BookingDto.builder()
                .screening(BasicScreeningDto.builder()
                    .roomName(roomName)
                    .movieName(movieName)
                    .time(time)
                    .build())
                .seats(seats)
                .build(), "HUF");
            if (price.isPresent()) {
                return String.format("The price for this booking would be %s", price.get());
            }
        } catch (BookingException e) {
            return e.getMessage();
        }
        return "Failed to show price";
    }

    private Availability isAvailable() {
       return userAvailability.isUserAvailable();
    }

}
