package com.epam.training.ticketservice.core.room.impl;


import com.epam.training.ticketservice.core.room.RoomService;
import com.epam.training.ticketservice.core.room.model.RoomDto;
import com.epam.training.ticketservice.core.room.persistence.entity.RoomEntity;
import com.epam.training.ticketservice.core.room.persistence.repository.RoomRepository;
import com.epam.training.ticketservice.core.room.exceptions.RoomAlreadyExistsException;
import com.epam.training.ticketservice.core.room.exceptions.UnknownRoomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    @Override
    @Transactional
    public void createRoom(RoomDto room) throws RoomAlreadyExistsException {
        Objects.requireNonNull(room, "Room cannot be null");
        Objects.requireNonNull(room.getName(), "Room title cannot be null");
        Objects.requireNonNull(room.getColumns(), "Room columns cannot be null");
        Objects.requireNonNull(room.getRows(), "Room rows cannot be null");
        if (roomRepository.existsByName(room.getName())) {
            throw new RoomAlreadyExistsException(String.format("Room already exists with name: %s", room.getName()));
        }
        log.debug("Creating new Room : {}", room);
        RoomEntity roomEntity = RoomEntity.builder()
            .columns(room.getColumns())
            .name(room.getName())
            .rowsNumber(room.getRows())
            .build();
        RoomEntity createdRoom = roomRepository.save(roomEntity);
        log.debug("Created room is : {}", createdRoom);
    }

    @Override
    @Transactional
    public void updateRoom(RoomDto room) throws UnknownRoomException {
        Objects.requireNonNull(room, "Room cannot be null");
        Objects.requireNonNull(room.getName(), "Room title cannot be null");
        Objects.requireNonNull(room.getColumns(), "Room columns cannot be null");
        Objects.requireNonNull(room.getRows(), "Room rows cannot be null");
        Optional<RoomEntity> oldRoomEntity = roomRepository.findByName(room.getName());
        if (oldRoomEntity.isEmpty()) {
            throw new UnknownRoomException(String.format("Room is not found: %s", room));
        }
        log.debug("Room entity before update: {}", oldRoomEntity.get());
        RoomEntity updatedRoomEntity = RoomEntity.builder()
            .columns(room.getColumns())
            .name(room.getName())
            .rowsNumber(room.getRows())
            .id(oldRoomEntity.get().getId())
            .build();
        roomRepository.save(updatedRoomEntity);
        log.debug("Updated Room entity: {}", updatedRoomEntity);
    }

    @Override
    @Transactional
    public void deleteRoom(String name) throws UnknownRoomException {
        Optional<RoomEntity> roomEntity = roomRepository.findByName(name);
        if (roomEntity.isEmpty()) {
            throw new UnknownRoomException(String.format("Room is not found with name:  %s", name));
        }
        roomRepository.delete(roomEntity.get());
        log.debug("Deleted Room {}", roomEntity.get());
    }

    @Override
    public List<RoomDto> getRooms() {
        return roomRepository.findAll().stream()
            .map(this::convertEntityToDto).collect(Collectors.toList());
    }

    private RoomDto convertEntityToDto(RoomEntity roomEntity) {
        return RoomDto.builder()
            .columns(roomEntity.getColumns())
            .name(roomEntity.getName())
            .rows(roomEntity.getRowsNumber())
            .build();
    }

}
