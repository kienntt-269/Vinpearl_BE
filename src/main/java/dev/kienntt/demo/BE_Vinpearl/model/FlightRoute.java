package dev.kienntt.demo.BE_Vinpearl.model;

import dev.kienntt.demo.BE_Vinpearl.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "flight_route")
@Getter
@Setter
public class FlightRoute extends BaseEntity {
    @Column(name = "start_flight")
    private String startFlight;

    @Column(name = "end_flight")
    private String endFlight;
}
