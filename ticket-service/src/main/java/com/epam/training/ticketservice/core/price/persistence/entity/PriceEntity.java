package com.epam.training.ticketservice.core.price.persistence.entity;

import com.epam.training.ticketservice.core.movie.persistence.entity.MovieEntity;
import com.epam.training.ticketservice.core.room.persistence.entity.RoomEntity;
import com.epam.training.ticketservice.core.screening.persistence.entity.ScreeningEntity;
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
    @Column(unique = true)
    private String name;
    @Column
    private int value;
    @Column
    private String currency;

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
