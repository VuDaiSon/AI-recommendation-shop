package com.example.recommendershop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID orderId;

    @Column(name = "date")
    @CreationTimestamp
    private Date date;

    @Column(name = "totalValue")
    private Double totalValue;

    @Column(name = "status")
    private String status;

    @Column(name = "address")
    private String address;

    @Column(name = "number")
    private String number;

    @Column(name = "receiver")
    private String receiver;
    @Column(name = "shippingFee")
    private Integer shippingFee;
    @Column(name = "paymentMethod")
    private String paymentMethod;
    @OneToOne
    @JoinColumn(name = "cartId")
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

}
