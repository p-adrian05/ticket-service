package com.epam.training.ticketservice.core.booking.persistence.entity;

import com.epam.training.ticketservice.core.account.persistence.entity.AccountEntity;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class TicketEntity {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Integer id;

    @OneToMany(mappedBy = "ticketEntity",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<SeatEntity> seats = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="account_id")
    private AccountEntity accountEntity;

    @Column
    private Integer price;
}
