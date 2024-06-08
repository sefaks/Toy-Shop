package com.example.demoToy.service;

import com.example.demoToy.dao.*;
import com.example.demoToy.entity.*;
import com.example.demoToy.exception.InsufficientBalanceException;
import com.example.demoToy.exception.InsufficientStockException;
import com.example.demoToy.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {


    @Autowired
    private final CartRepository cartRepository;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final ToyRepository toyRepository;

    @Autowired
    private final OrderRepository orderRepository;

    @Autowired
    private final CartItemRepository cartItemRepository;

    public Cart addItemToCart(Long userId, Long toyId, Integer quantity) {
        Userr user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        Toy toy = toyRepository.findById(toyId)
                .orElseThrow(() -> new NotFoundException("Toy not found with id: " + toyId));

        if (toy.getStock() < quantity ||  toy.getStock()==0) {
            throw new InsufficientStockException("Not enough stock available for toy with id: " + toyId);
        }


        Cart cart = user.getCart();
        if (cart == null) {
            cart = new Cart();
            cart.setCartOwner(user);
            cartRepository.save(cart);
        }

        CartItem existingCartItem = cart.getCartItems().stream()
                .filter(item -> item.getToy().getId().equals(toyId))
                .findFirst().orElse(null);

        if (existingCartItem != null) {
            existingCartItem.setQuantity(existingCartItem.getQuantity() + quantity);
        } else {
            CartItem newCartItem = new CartItem();
            newCartItem.setToy(toy);
            newCartItem.setQuantity(quantity);
            newCartItem.setCart(cart);
            cart.getCartItems().add(newCartItem);
        }

        cart.setCartItems(cart.getCartItems());
        cartRepository.save(cart);

        return cart;
    }

    public Cart getCart(Long userId) {

        Userr user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        Cart cart = user.getCart();

        if(user.getCart() == null) {
            cart = new Cart();
            cart.setCartOwner(user);
            cartRepository.save(cart);
        }

        return cart;

        }


    public List<CartItem> getCartItems(Long userId) {

        Userr user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        Cart cart = user.getCart();

        List<CartItem> cartItems = cart.getCartItems();

        return cartItems;
    }

    public Cart removeItemFromCart(Long userId, Long cartItemId, Integer quantity) {
        Userr user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        Cart cart = user.getCart();

        if (cart == null) {
            cart = new Cart();
            cart.setCartOwner(user);
            cartRepository.save(cart);
        }

        CartItem cartItemToRemove = cart.getCartItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst().orElse(null);

        if (cartItemToRemove != null) {
            if (quantity == null || quantity >= cartItemToRemove.getQuantity()) {
                cart.getCartItems().remove(cartItemToRemove); // Tamamen kaldır
            } else {
                cartItemToRemove.setQuantity(cartItemToRemove.getQuantity() - quantity);
            }

            cart.recalculateCartTotal();
            cartRepository.save(cart);

            return cart;
        } else {
            throw new NotFoundException("Cart item not found with id: " + cartItemId);
        }
    }

    @Transactional(rollbackFor = {InsufficientBalanceException.class, InsufficientStockException.class, RuntimeException.class})
    public Cart buyItemsInCart(Long userId) {
        Userr user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        Cart cart = user.getCart();
        if (cart == null) {
            throw new NotFoundException("Cart not found for the user");
        }

        if (cart.getCartItems().isEmpty()) {
            throw new NotFoundException("Cart is empty");
        }

        double totalCost = cart.getCartTotal();
        double userBalance = user.getWallet().getBudget();

        if (totalCost > userBalance) {
            throw new InsufficientBalanceException("User does not have enough balance to complete the purchase");
        }

        for (CartItem cartItem : cart.getCartItems()) {
            Toy toy = cartItem.getToy();
            int purchasedQuantity = cartItem.getQuantity();
            int currentStock = toy.getStock();

            if (purchasedQuantity > currentStock) {
                throw new InsufficientStockException("Not enough stock available for toy with id: " + toy.getId());
            }

            toy.setStock(currentStock - purchasedQuantity);
        }

        Order order = new Order();
        order.setUser(user);
        order.setCreationDate(LocalDateTime.now());

        List<OrderItem> orderItems = new ArrayList<>(); // bu for yukarı alınacaktır todo
        for (CartItem cartItem : cart.getCartItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setToy(cartItem.getToy());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setOrder(order);
            orderItems.add(orderItem);
        }

        order.setOrderItems(orderItems);

        user.getWallet().setBudget(userBalance - totalCost);

        cartItemRepository.deleteAll(cart.getCartItems());

        cart.getCartItems().clear();
        cart.recalculateCartTotal();
        cartRepository.save(cart);

        userRepository.save(user);
        orderRepository.save(order);

        return cart;
    }





    public boolean validateUserBudget(Long userId, Long cost) {

        Userr user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        if(user.getWallet().getBudget() < cost){
            return false;
        }else{
            return true;
        }



    }

    }




