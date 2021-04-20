package com.epam.training.ticketservice.core.booking.persistence.entity;

import com.epam.training.ticketservice.core.screening.persistence.entity.ScreeningEntity;
import com.epam.training.ticketservice.core.account.persistence.entity.AccountEntity;
import lombok.*;

import javax.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class TicketEntity {

    @EmbeddedId
    private TicketId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("screeningId")
    @JoinColumn(name = "screening_id")
    private ScreeningEntity screeningEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("seatId")
    @JoinColumns({
            @JoinColumn(name = "row_num", referencedColumnName = "row_num"),
            @JoinColumn(name = "col_num", referencedColumnName = "col_num")
    })
    private SeatEntity seatEntity;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="account_id")
    private AccountEntity accountEntity;

    @Column
    private Integer price;
}
