package dev.kienntt.demo.BE_Vinpearl.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dev.kienntt.demo.BE_Vinpearl.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "ticket")
@Getter
@Setter
public class Ticket extends BaseEntity {
    @Column(name = "name")
    private String name;

    @Column(name = "status")
    private Long status;

    @Column(name = "ticket_type_id")
    private Long ticketTypeId;
}
