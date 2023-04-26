package dev.kienntt.demo.BE_Vinpearl.model;

import dev.kienntt.demo.BE_Vinpearl.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "comment")
@Getter
@Setter
public class Comment extends BaseEntity {
    @Column(name = "content")
    private String name;

    @Column(name = "post_id")
    private Long postId;

    @ManyToOne
    @JoinColumn(name="post_id", nullable=false, insertable = false, updatable = false)
    private Post post;

    @Column(name = "customer_id")
    private Long customerId;

    @ManyToOne
    @JoinColumn(name="customer_id", nullable=false, insertable = false, updatable = false)
    private Customer customer;
}
