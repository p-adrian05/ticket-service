package com.epam.training.ticketservice.persistence.dao.impl;

import com.epam.training.ticketservice.persistence.RoomRepository;
import com.epam.training.ticketservice.persistence.dao.RoomDao;
import com.epam.training.ticketservice.model.Room;
import com.epam.training.ticketservice.persistence.entity.RoomEntity;
import com.epam.training.ticketservice.persistence.exceptions.RoomAlreadyExistsException;
import com.epam.training.ticketservice.persistence.exceptions.UnknownRoomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Repository
@RequiredArgsConstructor
@Slf4j
public class RoomDaoImpl implements RoomDao {

    private final RoomRepository roomRepository;

    @Override
    public void create(Room room) throws RoomAlreadyExistsException {
        Objects.requireNonNull(room, "Room is a mandatory parameter");
        if(roomRepository.existsByName(room.getName())){
            throw new RoomAlreadyExistsException(String.format("Room already exists with name: %s",room.getName()));
        }
        log.debug("Creating new Room : {}",room);
        RoomEntity roomEntity = RoomEntity.builder()
                .columns(room.getColumns())
                .name(room.getName())
                .rows(room.getRows())
                .build();
        int id =  roomRepository.save(roomEntity).getId();
        log.debug("Created room id is : {}",id);
    }

    @Override
    public void update(Room room) throws UnknownRoomException {
        Objects.requireNonNull(room, "Room is a mandatory parameter");
        RoomEntity oldRoomEntity = queryRoom(room.getName());
        RoomEntity updatedRoomEntity = RoomEntity.builder()
                .columns(room.getColumns())
                .name(room.getName())
                .rows(room.getRows())
                .id(oldRoomEntity.getId())
                .build();
        roomRepository.save(updatedRoomEntity);
        log.debug("Updated Room entity: {}",updatedRoomEntity);
    }

    @Override
    public void delete(Room room) throws UnknownRoomException {
        Objects.requireNonNull(room, "Room is a mandatory parameter");
        RoomEntity roomEntity = queryRoom(room.getName());
        roomRepository.delete(roomEntity);
        log.debug("Deleted Room {}",roomEntity);
    }

    @Override
    public Collection<Room> readAll() {
        return StreamSupport.stream(roomRepository.findAll().spliterator(),true)
                .map(roomEntity -> Room.builder()
                        .columns(roomEntity.getColumns())
                        .name(roomEntity.getName())
                        .rows(roomEntity.getRows())
                        .build())
                .collect(Collectors.toList());
    }
    protected RoomEntity queryRoom(String name) throws UnknownRoomException {
        Optional<RoomEntity> roomEntityOptional= roomRepository.findByName(name);
        if(roomEntityOptional.isEmpty()){
            throw new UnknownRoomException(String.format("Room is not found with name: %s",name));
        }
        return roomEntityOptional.get();
    }
}
