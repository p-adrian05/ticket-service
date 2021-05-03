package com.epam.training.ticketservice.ui.command;

import com.epam.training.ticketservice.core.screening.ScreeningService;
import com.epam.training.ticketservice.core.screening.exceptions.ScreeningCreationException;
import com.epam.training.ticketservice.core.screening.exceptions.UnknownScreeningException;
import com.epam.training.ticketservice.core.screening.model.BasicScreeningDto;
import com.epam.training.ticketservice.core.screening.model.ScreeningDto;
import com.epam.training.ticketservice.ui.util.UserAvailability;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ShellComponent
@RequiredArgsConstructor
@Slf4j
public class ScreeningCommand {

    private final ScreeningService screeningService;
    private final UserAvailability userAvailability;

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
    public String crateScreening(String movieName, String roomName, LocalDateTime time) {
        BasicScreeningDto basicScreeningDto = BasicScreeningDto.builder()
            .roomName(roomName)
            .movieName(movieName)
            .time(time).build();
        try {
            screeningService.createScreening(basicScreeningDto);
            return basicScreeningDto.toString();
        } catch (ScreeningCreationException ex) {
            return ex.getMessage();
        }
    }

    @ShellMethodAvailability("isAvailable")
    @ShellMethod(value = "Admin delete Screening", key = "delete screening")
    public String deleteScreening(String movieName, String roomName, LocalDateTime time){
        BasicScreeningDto basicScreeningDto = BasicScreeningDto.builder()
            .roomName(roomName)
            .movieName(movieName)
            .time(time).build();
        try{
            screeningService.deleteScreening(basicScreeningDto);
            return "Successful deletion";
        }catch (UnknownScreeningException e){
            return e.getMessage();
        }

    }

    private Availability isAvailable() {
       return userAvailability.isAdminAvailable();
    }

}
