package com.epam.training.ticketservice.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatId implements Serializable {

    private static final long serialVersionUID = 2L;
    @Column(name = "row_num")
    private int rowNum;
    @Column(name = "col_num")
    private int colNum;
}
