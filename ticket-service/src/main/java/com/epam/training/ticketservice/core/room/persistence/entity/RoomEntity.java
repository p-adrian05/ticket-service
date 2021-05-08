package com.epam.training.ticketservice.core.room.persistence.entity;

import com.epam.training.ticketservice.core.price.persistence.entity.PriceEntity;
import com.epam.training.ticketservice.core.screening.persistence.entity.ScreeningEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
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
    private int rowsNumber;
    @Column
    private int columns;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "room_prices",
        joinColumns = @JoinColumn(name = "room_id"),
        inverseJoinColumns = @JoinColumn(name = "price_id")
    )
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<PriceEntity> roomPrices;

    @OneToMany(mappedBy = "roomEntity",fetch = FetchType.LAZY,cascade = CascadeType.REMOVE)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<ScreeningEntity> screenings;

    public void addPrice(PriceEntity priceEntity) {
        this.getRoomPrices().add(priceEntity);
    }

}
