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
    private Integer id;
    @Column(unique = true)
    private String name;
    @Column
    private Integer value;
    @Column
    private String currency;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "movie_prices",
            joinColumns = @JoinColumn(name = "price_id"),
            inverseJoinColumns = @JoinColumn(name = "movie_id")
    )
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<MovieEntity> movies;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "room_prices",
            joinColumns = @JoinColumn(name = "price_id"),
            inverseJoinColumns = @JoinColumn(name = "room_id")
    )
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<RoomEntity> rooms;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "screening_prices",
            joinColumns = @JoinColumn(name = "price_id"),
            inverseJoinColumns = @JoinColumn(name = "screening_id")
    )
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<ScreeningEntity> screenings;

    public void addRoom(RoomEntity roomEntity) {
        this.getRooms().add(roomEntity);
    }
    public void addScreening(ScreeningEntity screeningEntity) {
        this.getScreenings().add(screeningEntity);
    }
    public void addMovie(MovieEntity movieEntity) {
        this.getMovies().add(movieEntity);
    }
}
