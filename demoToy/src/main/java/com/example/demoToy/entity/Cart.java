package com.example.demoToy.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name="cards")
public class Cart implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    @JsonIgnore
    private Userr cartOwner;

    @Column(name="cart_total")
    private Double cartTotal;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL)
    private List<CartItem> cartItems;

    public void recalculateCartTotal() {
        double total = 0.0;
        for (CartItem item : cartItems) {
            total += item.getToy().getPrice() * item.getQuantity();
        }
        this.cartTotal = total;
    }
    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
        recalculateCartTotal();
    }


}
