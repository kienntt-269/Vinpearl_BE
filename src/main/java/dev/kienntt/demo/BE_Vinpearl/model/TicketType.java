package dev.kienntt.demo.BE_Vinpearl.model;

import dev.kienntt.demo.BE_Vinpearl.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ticket_type")
@Getter
@Setter
public class TicketType extends BaseEntity {
    @Column(name = "name")
    private String name;

    @Column(name = "total_seat")
    private Long totalSeat;

    @Column(name = "price")
    private Long price;
}
