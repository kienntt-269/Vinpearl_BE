package dev.kienntt.demo.BE_Vinpearl.model;

import dev.kienntt.demo.BE_Vinpearl.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "airport")
@Getter
@Setter
public class Airport extends BaseEntity {
    @Column(name = "name")
    private String name;

    @Column(name = "site_id")
    private Long siteId;

    @ManyToOne
    @JoinColumn(name="site_id", nullable=false, insertable = false, updatable = false)
    private Site site;

    @Transient
    private String creator;
}
