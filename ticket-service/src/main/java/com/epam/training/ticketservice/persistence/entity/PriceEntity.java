package com.epam.training.ticketservice.persistence.entity;

import com.epam.training.ticketservice.model.Room;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "price")
public class PriceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column
    private String name;
    @Column
    private int value;

    @ManyToMany(mappedBy = "moviePrices")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<MovieEntity> movies;

    @ManyToMany(mappedBy = "roomPrices")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<RoomEntity> rooms;

    @ManyToMany(mappedBy = "screeningPrices")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<ScreeningEntity> screenings;

}
