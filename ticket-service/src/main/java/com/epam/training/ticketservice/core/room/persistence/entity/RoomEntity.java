package com.epam.training.ticketservice.core.room.persistence.entity;

import com.epam.training.ticketservice.core.price.persistence.entity.PriceEntity;
import com.epam.training.ticketservice.core.screening.persistence.entity.ScreeningEntity;
import lombok.*;

import javax.persistence.*;
import java.util.List;
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
    private Integer id;
    @Column(unique = true)
    private String name;
    @Column
    private int rows;
    @Column
    private int columns;

    @ManyToMany(fetch = FetchType.LAZY,mappedBy = "rooms")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<PriceEntity> roomPrices;

    @OneToMany(mappedBy = "roomEntity",orphanRemoval = true,fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<ScreeningEntity> screenings;

}
