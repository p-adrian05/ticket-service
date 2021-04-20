package com.epam.training.ticketservice.core.room.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomDto {

    private String name;

    private Integer rows;

    private Integer columns;

}
