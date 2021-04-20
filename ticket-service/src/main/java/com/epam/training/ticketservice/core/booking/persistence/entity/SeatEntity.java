package com.epam.training.ticketservice.core.booking.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class SeatEntity {

    @EmbeddedId
    private SeatId id;

}

