package com.epam.training.ticketservice.ui.command;

import com.epam.training.ticketservice.core.booking.model.SeatDto;
import com.epam.training.ticketservice.core.price.PriceService;
import com.epam.training.ticketservice.core.price.exceptions.AttachPriceException;
import com.epam.training.ticketservice.core.price.exceptions.PriceAlreadyExistsException;
import com.epam.training.ticketservice.core.price.exceptions.UnknownPriceException;
import com.epam.training.ticketservice.core.price.model.PriceDto;
import com.epam.training.ticketservice.core.screening.model.BasicScreeningDto;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Currency;
import java.util.Set;

@ShellComponent
@RequiredArgsConstructor
public class PriceCommand {

    private final PriceService priceService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    //@ShellMethodAvailability("isAvailable")
    @ShellMethod(value = "Admin update base price", key = "update base price")
    public String updateBasePrice(int price) {
        try {
            priceService.updatePrice(PriceDto.builder()
                .name("Base")
                .value(price)
                .currency(Currency.getInstance("HUF"))
                .build());
        } catch (UnknownPriceException e) {
            return e.getMessage();
        }

        return String.format("Successful update, new base price is %s HUF", price);
    }

    //@ShellMethodAvailability("isAvailable")
    @ShellMethod(value = "Admin create price component", key = "create price component")
    public String createPrice(String name, int price) {
        PriceDto priceDto = PriceDto.builder()
            .name(name)
            .value(price)
            .currency(Currency.getInstance("HUF"))
            .build();
        try {
            priceService.createPrice(priceDto);
        } catch (PriceAlreadyExistsException e) {
            return e.getMessage();
        }
        return String.format("Successful creation, new price component is %s HUF", priceDto);
    }
    //@ShellMethodAvailability("isAvailable")
    @ShellMethod(value = "Admin attach price component to room", key = "attach price component to room")
    public String attachPriceToRoom(String name, String roomName) {
        try {
            priceService.attachRoom(roomName, name);
        } catch (UnknownPriceException | AttachPriceException e) {
            return e.getMessage();
        }
        return "Successful attach";
    }
    //@ShellMethodAvailability("isAvailable")
    @ShellMethod(value = "Admin attach price component to screening", key = "attach price component to screening")
    public String attachPriceToScreening(String name, String movieName, String roomName, String time) {
        try {
            priceService.attachScreening(BasicScreeningDto.builder()
                .time(LocalDateTime.parse(time, formatter))
                .movieName(movieName)
                .roomName(roomName)
                .build(), name);
        } catch (UnknownPriceException | AttachPriceException e) {
            return e.getMessage();
        }
        return "Successful attach";
    }
    //@ShellMethodAvailability("isAvailable")
    @ShellMethod(value = "Admin attach price component to movie", key = "attach price component to movieEntity")
    public String attachPriceToMovie(String name, String movieName) {
        try {
            priceService.attachMovie(movieName, name);
        } catch (UnknownPriceException | AttachPriceException e) {
            return e.getMessage();
        }
        return "Successful attach";
    }

}
