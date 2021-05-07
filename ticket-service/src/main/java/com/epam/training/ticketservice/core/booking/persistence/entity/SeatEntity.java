package com.epam.training.ticketservice.core.booking.persistence.entity;

import com.epam.training.ticketservice.core.price.persistence.entity.PriceEntity;
import com.epam.training.ticketservice.core.screening.persistence.entity.ScreeningEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class SeatEntity {

    @EmbeddedId
    private SeatId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id")
    private TicketEntity ticketEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("screeningId")
    @JoinColumn(name = "screening_id")
    private ScreeningEntity screeningEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "price_id")
    private PriceEntity priceEntity;

}

