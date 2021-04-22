package com.epam.training.ticketservice.core.room;

import com.epam.training.ticketservice.core.room.model.RoomDto;
import com.epam.training.ticketservice.core.room.exceptions.RoomAlreadyExistsException;
import com.epam.training.ticketservice.core.room.exceptions.UnknownRoomException;

import java.util.Collection;

public interface RoomService {

    void createRoom(RoomDto room) throws RoomAlreadyExistsException;

    void updateRoom(RoomDto room) throws UnknownRoomException;

    void deleteRoom(String name) throws UnknownRoomException;

    Collection<RoomDto> getRooms();

}
