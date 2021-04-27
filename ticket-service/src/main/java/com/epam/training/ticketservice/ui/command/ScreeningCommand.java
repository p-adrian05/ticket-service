package com.epam.training.ticketservice.ui.command;

import com.epam.training.ticketservice.core.user.LoginService;
import com.epam.training.ticketservice.core.user.UserService;

import com.epam.training.ticketservice.core.screening.ScreeningService;
import com.epam.training.ticketservice.core.screening.exceptions.ScreeningCreationException;
import com.epam.training.ticketservice.core.screening.exceptions.UnknownScreeningException;
import com.epam.training.ticketservice.core.screening.model.CreateScreeningDto;
import com.epam.training.ticketservice.core.screening.model.ScreeningDto;
import com.epam.training.ticketservice.core.user.model.UserDto;
import com.epam.training.ticketservice.core.user.persistence.entity.UserEntity;
import lombok.RequiredArgsConstructor;

import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ShellComponent
@RequiredArgsConstructor
public class ScreeningCommand {

    private final ScreeningService screeningService;
    private final LoginService loginService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @ShellMethod(value = "Screening List", key = "list screenings")
    public List<String> listScreenings() {
        List<ScreeningDto> screenings = screeningService.getScreenings();
        if (screenings.size() == 0) {
            return List.of("There are no screenings");
        }
        return screenings.stream().map(ScreeningDto::toString).collect(Collectors.toList());
    }

    @ShellMethodAvailability("isAvailable")
    @ShellMethod(value = "Admin create Screening", key = "create screening")
    public String crateScreening(String movieName, String roomName, String time) {
        CreateScreeningDto createScreeningDto = CreateScreeningDto.builder()
            .roomName(roomName)
            .movieName(movieName)
            .time(LocalDateTime.parse(time, formatter)).build();
        try {
            screeningService.createScreening(createScreeningDto);
        } catch (ScreeningCreationException ex) {
            return ex.getMessage();
        }
        return createScreeningDto.toString();
    }

    @ShellMethodAvailability("isAvailable")
    @ShellMethod(value = "Admin delete Screening", key = "delete screening")
    public void deleteScreening(String movieName, String roomName, String time) throws UnknownScreeningException {
        CreateScreeningDto createScreeningDto = CreateScreeningDto.builder()
            .roomName(roomName)
            .movieName(movieName)
            .time(LocalDateTime.parse(time, formatter)).build();
        screeningService.deleteScreening(createScreeningDto);
    }

    private Availability isAvailable() {
        Optional<UserDto> loggedInUser = loginService.getLoggedInUser();
        if (loggedInUser.isEmpty()) {
            return Availability.unavailable("Not logged in");
        } else if (loggedInUser.get().getRole().equals(UserEntity.Role.ADMIN)) {
            return Availability.available();
        }
        return Availability.unavailable("You are not an admin user");
    }

}
