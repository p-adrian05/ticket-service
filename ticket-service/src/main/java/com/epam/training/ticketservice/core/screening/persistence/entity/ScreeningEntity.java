package com.epam.training.ticketservice.core.screening.persistence.entity;

import com.epam.training.ticketservice.core.booking.persistence.entity.SeatEntity;
import com.epam.training.ticketservice.core.movie.persistence.entity.MovieEntity;
import com.epam.training.ticketservice.core.room.persistence.entity.RoomEntity;
import com.epam.training.ticketservice.core.price.persistence.entity.PriceEntity;
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
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    private MovieEntity movieEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private RoomEntity roomEntity;

    @Column
    private LocalDateTime startTime;

    @Column
    private LocalDateTime endTime;

    @ManyToMany(fetch = FetchType.LAZY,mappedBy = "screenings")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<PriceEntity> screeningPrices;

    @OneToMany(mappedBy = "screeningEntity", orphanRemoval = true,fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<SeatEntity> seats;
}
