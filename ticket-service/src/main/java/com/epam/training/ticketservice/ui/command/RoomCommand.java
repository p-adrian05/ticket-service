package com.epam.training.ticketservice.ui.command;

import com.epam.training.ticketservice.core.room.RoomService;
import com.epam.training.ticketservice.core.room.exceptions.RoomAlreadyExistsException;
import com.epam.training.ticketservice.core.room.exceptions.UnknownRoomException;
import com.epam.training.ticketservice.core.room.model.RoomDto;
import com.epam.training.ticketservice.ui.util.UserAvailability;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

import java.util.List;
import java.util.stream.Collectors;

@ShellComponent
@RequiredArgsConstructor
@Slf4j
public class RoomCommand {

    private final RoomService roomService;
    private final UserAvailability userAvailability;

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
    public String crateRoom(String name, int rowNum, int colNum){
        RoomDto roomDto = RoomDto.builder()
            .name(name)
            .columns(colNum)
            .rows(rowNum).build();
        try {
            roomService.createRoom(roomDto);
            return roomDto.toString();
        } catch (RoomAlreadyExistsException e) {
            log.error("Error during creating room: "+e.getMessage());
            return e.getMessage();
        }
    }

    @ShellMethodAvailability("isAvailable")
    @ShellMethod(value = "Admin delete Movie", key = "delete room")
    public String deleteRoom(String name){
        try {
            roomService.deleteRoom(name);
            return "Successful deletion";
        } catch (UnknownRoomException e) {
            log.error("Error during deleting room: "+e.getMessage());
            return e.getMessage();
        }
    }

    @ShellMethodAvailability("isAvailable")
    @ShellMethod(value = "Admin update Room", key = "update room")
    public String updateRoom(String name, int rowNum, int colNum)  {
        RoomDto roomDto = RoomDto.builder()
            .name(name)
            .columns(colNum)
            .rows(rowNum).build();
        try {
            roomService.updateRoom(roomDto);
            return roomDto.toString();
        } catch (UnknownRoomException e) {
            log.error("Error during updating room: "+e.getMessage());
           return e.getMessage();
        }
    }

    private Availability isAvailable() {
        return userAvailability.isAdminAvailable();
    }
}
