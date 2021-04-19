package com.epam.training.ticketservice.persistence.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "room")
public class RoomEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column
    private String name;
    @Column
    private int rows;
    @Column
    private int columns;

    @ManyToMany
    @JoinTable(name = "room_prices",
            joinColumns = @JoinColumn(name = "room_id"),
            inverseJoinColumns = @JoinColumn(name = "price_id")
    )
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<PriceEntity> roomPrices;

}
