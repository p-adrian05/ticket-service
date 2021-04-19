package com.epam.training.ticketservice.persistence.dao.impl;

import com.epam.training.ticketservice.persistence.RoomRepository;
import com.epam.training.ticketservice.persistence.dao.RoomDao;
import com.epam.training.ticketservice.model.Room;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
@NoArgsConstructor
@AllArgsConstructor
public class RoomDaoImpl implements RoomDao {

    private RoomRepository roomRepository;

    @Override
    public int create(Room room) {
        return 0;
    }

    @Override
    public void update(Room objroomect) {

    }

    @Override
    public void delete(Room room) {

    }

    @Override
    public Collection<Room> readAll() {
        return null;
    }

    @Override
    public void deleteByName(String name) {

    }
}
