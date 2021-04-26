package com.training.epam.ticketservice.core.room.impl;


import com.epam.training.ticketservice.core.movie.exceptions.MovieAlreadyExistsException;
import com.epam.training.ticketservice.core.price.persistence.repository.PriceRepository;
import com.epam.training.ticketservice.core.room.exceptions.RoomAlreadyExistsException;
import com.epam.training.ticketservice.core.room.exceptions.UnknownRoomException;
import com.epam.training.ticketservice.core.room.impl.RoomServiceImpl;
import com.epam.training.ticketservice.core.room.model.RoomDto;
import com.epam.training.ticketservice.core.room.persistence.entity.RoomEntity;
import com.epam.training.ticketservice.core.room.persistence.repository.RoomRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

public class RoomServiceImplTest {

    private static final RoomEntity ROOM_ENTITY_1 = RoomEntity.builder()
        .id(1)
        .columns(10)
        .name("A1")
        .rows(15)
        .build();
    private static final RoomEntity ROOM_ENTITY_2 = RoomEntity.builder()
        .id(2)
        .columns(13)
        .name("A2")
        .rows(11)
        .build();
    public static final RoomDto ROOM_DTO_1 = RoomDto.builder()
        .name("A1")
        .rows(15)
        .columns(10)
        .build();
    public static final RoomDto ROOM_DTO_2 = RoomDto.builder()
        .name("A2")
        .rows(11)
        .columns(13)
        .build();

    private RoomServiceImpl underTest;
    private RoomRepository roomRepository;

    @BeforeEach
    public void init() {
        roomRepository = Mockito.mock(RoomRepository.class);
        underTest = new RoomServiceImpl(roomRepository);
    }

    @Test
    public void testGetRoomsShouldCallRoomRepositoryAndReturnADtoList() {
        // Given
        Mockito.when(roomRepository.findAll()).thenReturn(List.of(ROOM_ENTITY_1, ROOM_ENTITY_2));
        List<RoomDto> expected = List.of(ROOM_DTO_1, ROOM_DTO_2);

        // When
        List<RoomDto> actual = underTest.getRooms();

        // Then
        Assertions.assertEquals(expected, actual);
        Mockito.verify(roomRepository).findAll();
        Mockito.verifyNoMoreInteractions(roomRepository);
    }

    @Test
    public void testCreateRoomShouldCallRoomRepositoryWhenTheRoomInputIsValidAndNotExistsInDatabaseByName()
        throws RoomAlreadyExistsException {
        // Given
        RoomEntity roomEntity = RoomEntity.builder()
            .id(null)
            .columns(10)
            .name("A1")
            .rows(15)
            .build();
        Mockito.when(roomRepository.existsByName(ROOM_DTO_1.getName())).thenReturn(false);
        Mockito.when(roomRepository.save(roomEntity)).thenReturn(ROOM_ENTITY_1);

        // When
        underTest.createRoom(ROOM_DTO_1);

        // Then
        Mockito.verify(roomRepository).existsByName(ROOM_DTO_1.getName());
        Mockito.verify(roomRepository).save(roomEntity);
        Mockito.verifyNoMoreInteractions(roomRepository);
    }

    @Test
    public void testCreateRoomShouldThrowRoomAlreadyExistsExceptionWhenRoomAlreadyExistsInDatabaseByName() {
        // Given
        Mockito.when(roomRepository.existsByName(ROOM_DTO_1.getName())).thenReturn(true);
        // When
        Assertions.assertThrows(RoomAlreadyExistsException.class, () -> underTest.createRoom(ROOM_DTO_1));
        // Then
        Mockito.verify(roomRepository).existsByName(ROOM_DTO_1.getName());
        Mockito.verifyNoMoreInteractions(roomRepository);
    }

    @Test
    public void testCreateRoomShouldThrowNullPointerExistsExceptionWhenRoomNameIsNull() {
        // Given
        RoomDto roomDto = RoomDto.builder()
            .name(null)
            .rows(15)
            .columns(10)
            .build();
        // When
        Assertions.assertThrows(NullPointerException.class, () -> underTest.createRoom(roomDto));
        // Then
        Mockito.verifyNoMoreInteractions(roomRepository);
    }

    @Test
    public void testCreateRoomShouldThrowNullPointerExistsExceptionWhenRoomInputIsNull() {
        // Given
        RoomDto roomDto = null;
        // When
        Assertions.assertThrows(NullPointerException.class, () -> underTest.createRoom(roomDto));
        // Then
        Mockito.verifyNoMoreInteractions(roomRepository);
    }

    @Test
    public void testCreateRoomShouldThrowNullPointerExistsExceptionWhenRoomColumnsIsNull() {
        // Given
        RoomDto roomDto = RoomDto.builder()
            .name("A1")
            .rows(15)
            .columns(null)
            .build();
        // When
        Assertions.assertThrows(NullPointerException.class, () -> underTest.createRoom(roomDto));
        // Then
        Mockito.verifyNoMoreInteractions(roomRepository);
    }

    @Test
    public void testUpdateRoomShouldCallRoomRepositoryWhenTheRoomInputIsValidAndExistsInDatabaseByName()
        throws UnknownRoomException {
        // Given
        Mockito.when(roomRepository.findByName(ROOM_DTO_1.getName())).thenReturn(Optional.ofNullable(ROOM_ENTITY_1));
        Mockito.when(roomRepository.save(ROOM_ENTITY_1)).thenReturn(ROOM_ENTITY_1);

        // When
        underTest.updateRoom(ROOM_DTO_1);

        // Then
        Mockito.verify(roomRepository).findByName(ROOM_DTO_1.getName());
        Mockito.verify(roomRepository).save(ROOM_ENTITY_1);
        Mockito.verifyNoMoreInteractions(roomRepository);
    }

    @Test
    public void testUpdateRoomShouldThrowUnknownRoomExceptionWhenRoomNotExistsInDatabaseByName() {
        // Given
        Mockito.when(roomRepository.findByName(ROOM_DTO_1.getName())).thenReturn(Optional.empty());
        // When
        Assertions.assertThrows(UnknownRoomException.class, () -> underTest.updateRoom(ROOM_DTO_1));
        // Then
        Mockito.verify(roomRepository).findByName(ROOM_DTO_1.getName());
        Mockito.verifyNoMoreInteractions(roomRepository);
    }

    @Test
    public void testDeleteRoomShouldCallRoomRepositoryWhenRoomExistsInDatabaseByName() throws UnknownRoomException {
        // Given
        Mockito.when(roomRepository.findByName(ROOM_DTO_1.getName())).thenReturn(Optional.ofNullable(ROOM_ENTITY_1));
        // When
        underTest.deleteRoom((ROOM_DTO_1.getName()));
        // Then
        Mockito.verify(roomRepository).findByName(ROOM_DTO_1.getName());
        Mockito.verify(roomRepository).delete(ROOM_ENTITY_1);
        Mockito.verifyNoMoreInteractions(roomRepository);
    }

    @Test
    public void testDeleteRoomShouldThrowUnknownRoomExceptionWhenRoomNotExistsInDatabaseByName() {
        // Given
        Mockito.when(roomRepository.findByName(ROOM_DTO_1.getName())).thenReturn(Optional.empty());
        // When
        Assertions.assertThrows(UnknownRoomException.class, () -> underTest.deleteRoom(ROOM_DTO_1.getName()));
        // Then
        Mockito.verify(roomRepository).findByName(ROOM_DTO_1.getName());
        Mockito.verifyNoMoreInteractions(roomRepository);
    }
}
