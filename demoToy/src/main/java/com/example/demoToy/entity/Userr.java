package com.example.demoToy.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="userrs")
public class Userr implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="name",nullable = false)
    private String name;

    @Column(name="surname",nullable = false)
    private String surname;

    @Column(name="email",unique = true)
    private String email;

    @Column(name="password",nullable = false)
    private String password;

    @Column(name="phone_number")
    private String phoneNumber;

    @OneToMany(mappedBy = "user")
    private List<Order> orders;

    @OneToOne(mappedBy = "cartOwner", cascade = CascadeType.ALL)
    private Cart cart;

    @OneToOne(mappedBy = "walletOwner", cascade = CascadeType.ALL)
    private Wallet wallet;

    @Column(name="birth_date")
    @Temporal(TemporalType.DATE)
    private LocalDate birthdate;

    @PostPersist
    public void createCart() {
        if (cart == null) {
            cart = new Cart();
            cart.setCartOwner(this);
            this.setCart(cart);
        }
    }


}
