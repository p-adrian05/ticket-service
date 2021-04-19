package com.epam.training.ticketservice.persistence.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "movie")
public class MovieEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre")
    private GenreEntity genreEntity;

    @Column
    private int duration;

    @ManyToMany
    @JoinTable(name = "movie_prices",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "price_id")
    )
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<PriceEntity> moviePrices;



}
