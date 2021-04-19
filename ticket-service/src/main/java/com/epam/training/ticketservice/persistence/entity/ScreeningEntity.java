package com.epam.training.ticketservice.persistence.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "screening")
public class ScreeningEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    private MovieEntity movieEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private RoomEntity roomEntity;

    @Column
    private LocalDateTime time;

    @ManyToMany
    @JoinTable(name = "screening_prices",
            joinColumns = @JoinColumn(name = "screening_id"),
            inverseJoinColumns = @JoinColumn(name = "price_id")
    )
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<PriceEntity> screeningPrices;
}
