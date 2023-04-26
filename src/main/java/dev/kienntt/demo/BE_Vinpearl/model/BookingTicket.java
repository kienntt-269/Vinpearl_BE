package dev.kienntt.demo.BE_Vinpearl.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.kienntt.demo.BE_Vinpearl.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "booking_ticket")
@Getter
@Setter
public class BookingTicket extends BaseEntity {
    @Column(name = "payment_date")
    private Long paymentDate;

    @Column(name = "payment_amount")
    private Long paymentAmount;

    @Column(name = "payment_status")
    private Long paymentStatus;

    @Column(name = "description")
    private String description;

    @Column(name = "ticket_id")
    private Long ticketId;

    @Column(name = "passenger_id")
    private Long passengerId;

    @Column(name = "flight_id")
    private Long flightId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "flight_id", nullable = false, insertable = false, updatable = false)
    @JsonIgnore
    private Flight flight;

    @Column(name = "customer_id")
    private Long buyerId;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "passenger_id", referencedColumnName = "id", insertable = false, updatable = false)
    private List<Passenger> passengers;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "customer_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Customer buyer;
}
