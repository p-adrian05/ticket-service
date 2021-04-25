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
    private Integer id;

    @Column(unique = true)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre")
    private GenreEntity genreEntity;

    @Column
    private int duration;

    @ManyToMany(fetch = FetchType.LAZY,mappedBy = "movies")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<PriceEntity> moviePrices;

    @OneToMany(mappedBy = "movieEntity", orphanRemoval = true,fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<ScreeningEntity> screenings = new LinkedList<>();

}
