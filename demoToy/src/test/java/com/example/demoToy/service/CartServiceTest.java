package com.example.demoToy.service;

import com.example.demoToy.dao.CartItemRepository;
import com.example.demoToy.dao.CartRepository;
import com.example.demoToy.dao.ToyRepository;
import com.example.demoToy.dao.UserRepository;
import com.example.demoToy.entity.*;
import com.example.demoToy.exception.InsufficientBalanceException;
import com.example.demoToy.exception.InsufficientStockException;
import com.example.demoToy.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@Transactional
@ExtendWith(MockitoExtension.class)

class CartServiceTest {


    @Mock
    private UserRepository userRepository;

    @Mock
    private ToyRepository toyRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @InjectMocks
    private CartService cartService;

    // If we try to add the toy to the card when it is not in stock, we expect an InsufficientStockException.
    @Test
    public void testAddItemToCart_ToyOutOfStock_ThrowsInsufficientStockException() {

        Userr user = new Userr();
        user.setId(1L);

        Toy toy = new Toy();
        toy.setId(1L);
        toy.setStock(0);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(toyRepository.findById(1L)).thenReturn(Optional.of(toy));

        assertThrows(InsufficientStockException.class, () -> cartService.addItemToCart(1L, 1L, 1));
    }

    // If there is no user whose cart we want to get, we expect it to throw NotFoundException.
    @Test
    public void testGetCart_UserNotFound_ThrowsNotFoundException() {

        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> cartService.getCart(2L));
    }

    // We expect the cart items we intend to get to be the same as the cart items of that user.
    @Test
    public void testGetCartItems_UserExists_ReturnsCartItems() {

        Userr user = new Userr();
        user.setId(1L);
        Cart cart = new Cart();
        List<CartItem> cartItems = new ArrayList<>();

        Toy toy1 = new Toy();
        toy1.setId(1L);
        toy1.setName("Toy 1");
        toy1.setPrice(10.0);

        CartItem cartItem1 = new CartItem();
        cartItem1.setId(1L);
        cartItem1.setToy(toy1);
        cartItem1.setQuantity(2);

        Toy toy2 = new Toy();
        toy2.setId(2L);
        toy2.setName("Toy 2");
        toy2.setPrice(20.0);

        CartItem cartItem2 = new CartItem();
        cartItem2.setId(2L);
        cartItem2.setToy(toy2);
        cartItem2.setQuantity(1);

        cartItems.add(cartItem1);
        cartItems.add(cartItem2);
        cart.setCartItems(cartItems);
        user.setCart(cart);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        List<CartItem> result = cartService.getCartItems(1L);
        assertEquals(cartItems, result);
    }




    // When we remove the item from the card, we expect the quantity to change by the same amount.
    @Test
    public void testRemoveItemFromCart_UserExists_RemovesItem() {
        Userr user = new Userr();
        user.setId(1L);
        Cart cart = new Cart();
        List<CartItem> cartItems = new ArrayList<>();
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setQuantity(3);

        Toy toy = new Toy();
        toy.setId(1L);
        toy.setPrice(10.0);
        toy.setStock(5);
        cartItem.setToy(toy);

        cartItems.add(cartItem);
        cart.setCartItems(cartItems);
        user.setCart(cart);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Cart result = cartService.removeItemFromCart(1L, 1L, 2);

        assertEquals(1, result.getCartItems().get(0).getQuantity());
    }


    // If we havent cart for buying items, we except NotFoundException

    @Test
    public void testBuyItemsInCart_CartNotFound_ThrowsNotFoundException() {

        Userr user = new Userr();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // CartService'in buyItemsInCart metodu çağrıldığında NotFoundException fırlatılmalıdır
        assertThrows(NotFoundException.class, () -> cartService.buyItemsInCart(1L));
    }


}