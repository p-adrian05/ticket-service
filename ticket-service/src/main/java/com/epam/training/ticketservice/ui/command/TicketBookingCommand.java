package com.epam.training.ticketservice.ui.command;

import com.epam.training.ticketservice.core.booking.TicketService;
import com.epam.training.ticketservice.core.booking.exceptions.BookingException;
import com.epam.training.ticketservice.core.booking.model.BookingDto;
import com.epam.training.ticketservice.core.booking.model.SeatDto;
import com.epam.training.ticketservice.core.booking.model.TicketDto;
import com.epam.training.ticketservice.core.finance.money.Money;
import com.epam.training.ticketservice.core.screening.model.BasicScreeningDto;
import com.epam.training.ticketservice.core.user.LoginService;
import com.epam.training.ticketservice.core.user.model.UserDto;
import com.epam.training.ticketservice.core.user.persistence.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Set;

@ShellComponent
@Slf4j
@RequiredArgsConstructor
public class TicketBookingCommand {

    private final TicketService ticketService;
    private final LoginService loginService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @ShellMethodAvailability("isAvailable")
    @ShellMethod(value = "User create booking", key = "book")
    public String crateScreening(String movieName, String roomName, String time, Set<SeatDto> seats) {
        BookingDto bookingDto = BookingDto.builder()
            .screening(BasicScreeningDto.builder()
                .roomName(roomName)
                .movieName(movieName)
                .time(LocalDateTime.parse(time, formatter))
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
            //log.error(e.getMessage());
            return e.getMessage();
        }
    }

    @ShellMethod(value = "show price for booking", key = "show price for")
    public String showPrice(String movieName, String roomName, String time, Set<SeatDto> seats) {
        Optional<Money> price;
        try {
            price = ticketService.showPrice(BookingDto.builder()
                .screening(BasicScreeningDto.builder()
                    .roomName(roomName)
                    .movieName(movieName)
                    .time(LocalDateTime.parse(time, formatter))
                    .build())
                .seats(seats)
                .build(), "HUF");
        } catch (BookingException e) {
            return e.getMessage();
        }
        if (price.isPresent()) {
            return String.format("The price for this booking would be %s", price.get());
        }
        return "Failed to show price";
    }

    private Availability isAvailable() {
        Optional<UserDto> loggedInUser = loginService.getLoggedInUser();
        if (loggedInUser.isEmpty()) {
            return Availability.unavailable("Not logged in");
        } else if (loggedInUser.get().getRole().equals(UserEntity.Role.USER)) {
            return Availability.available();
        }
        return Availability.unavailable("You are not a user");
    }

}
