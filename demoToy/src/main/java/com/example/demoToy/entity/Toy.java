package com.example.demoToy.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Entity
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="toys")
public class Toy implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="name", nullable = false)
    private String name;

    @Column(name="price")
    private double price;

    @Column(name="stock")
    private int stock;

    @Column(name="description")
    private String description;

    @Column(name="image_url")
    private String imageUrl;

    @OneToMany(mappedBy = "toy", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<OrderItem> orderItems;

    @OneToMany(mappedBy = "toy", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<CartItem> cartItems;


}
