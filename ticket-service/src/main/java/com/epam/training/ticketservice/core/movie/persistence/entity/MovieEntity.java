package com.epam.training.ticketservice.core.movie.persistence.entity;

import com.epam.training.ticketservice.core.price.persistence.entity.PriceEntity;
import com.epam.training.ticketservice.core.screening.persistence.entity.ScreeningEntity;
import lombok.*;

import javax.persistence.*;
import java.util.*;

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

    @Column(unique = true)
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


    public void addPrice(PriceEntity priceEntity) {
        moviePrices.add(priceEntity);
        priceEntity.getMovies().add(this);
    }

    public void removePrice(PriceEntity priceEntity) {
        moviePrices.remove(priceEntity);
        priceEntity.getMovies().remove(this);
    }

    @OneToMany(
            mappedBy = "movieEntity",
            orphanRemoval = true
    )
    private List<ScreeningEntity> screenings = new LinkedList<>();
}
