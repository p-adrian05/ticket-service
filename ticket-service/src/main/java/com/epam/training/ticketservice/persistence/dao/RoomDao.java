package com.epam.training.ticketservice.persistence.dao;

import com.epam.training.ticketservice.model.Movie;
import com.epam.training.ticketservice.model.Room;
import com.epam.training.ticketservice.persistence.exceptions.MovieAlreadyExistsException;
import com.epam.training.ticketservice.persistence.exceptions.RoomAlreadyExistsException;
import com.epam.training.ticketservice.persistence.exceptions.UnknownMovieException;
import com.epam.training.ticketservice.persistence.exceptions.UnknownRoomException;

import java.util.Collection;

public interface RoomDao  {

    void createRoom(Room room) throws RoomAlreadyExistsException;

    void updateRoom(Room room) throws UnknownRoomException;

    void deleteRoom(String name) throws UnknownRoomException;

    Collection<Room> readAllRooms();

}
