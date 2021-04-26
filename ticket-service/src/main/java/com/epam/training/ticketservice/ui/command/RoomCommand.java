package com.epam.training.ticketservice.ui.command;

import com.epam.training.ticketservice.core.account.AccountService;

import com.epam.training.ticketservice.core.movie.model.MovieDto;
import com.epam.training.ticketservice.core.room.RoomService;
import com.epam.training.ticketservice.core.room.exceptions.RoomAlreadyExistsException;
import com.epam.training.ticketservice.core.room.exceptions.UnknownRoomException;
import com.epam.training.ticketservice.core.room.model.RoomDto;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

import java.util.List;
import java.util.stream.Collectors;

@ShellComponent
@RequiredArgsConstructor
public class RoomCommand {

    private final RoomService roomService;
    private final AccountService accountService;

    @ShellMethod(value = "Room List", key = "list rooms")
    public List<String> listRooms() {
        List<RoomDto> rooms = roomService.getRooms();
        if (rooms.size() == 0) {
            return List.of("There are no rooms at the moment");
        }
        return rooms.stream().map(RoomDto::toString).collect(Collectors.toList());
    }

    @ShellMethodAvailability("isAvailable")
    @ShellMethod(value = "Admin create Room", key = "create room")
    public RoomDto crateRoom(String name, int rowNum, int colNum) throws RoomAlreadyExistsException {
        RoomDto roomDto = RoomDto.builder()
            .name(name)
            .columns(colNum)
            .rows(rowNum).build();
        roomService.createRoom(roomDto);
        return roomDto;
    }

    @ShellMethodAvailability("isAvailable")
    @ShellMethod(value = "Admin delete Movie", key = "delete room")
    public void deleteRoom(String name) throws UnknownRoomException {
        roomService.deleteRoom(name);
    }

    @ShellMethodAvailability("isAvailable")
    @ShellMethod(value = "Admin update Room", key = "update room")
    public RoomDto updateRoom(String name, int rowNum, int colNum) throws UnknownRoomException {
        RoomDto roomDto = RoomDto.builder()
            .name(name)
            .columns(colNum)
            .rows(rowNum).build();
        roomService.updateRoom(roomDto);
        return roomDto;
    }

    private Availability isAvailable() {
        if (accountService.getSignedInUser() == null) {
            return Availability.unavailable("Not logged in");
        } else if (accountService.getSignedInUser().isPrivileged()) {
            return Availability.available();
        }
        return Availability.unavailable("You are not an admin user");
    }

}
