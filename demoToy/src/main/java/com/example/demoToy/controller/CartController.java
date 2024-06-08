package com.example.demoToy.controller;

import com.example.demoToy.entity.Cart;
import com.example.demoToy.entity.CartItem;
import com.example.demoToy.entity.Userr;
import com.example.demoToy.service.CartService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
public class CartController {


    @Autowired
    private final CartService cartService;
    private static final Logger logger = LoggerFactory.getLogger(CartController.class);


    //adding items to the cart
    @PostMapping("/add-item")
    public ResponseEntity<Cart> addItemToCart(HttpSession session,
                                              @RequestParam Long toyId,
                                              @RequestParam Integer quantity) {

        Userr user = (Userr) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Kullanıcı oturum açmamışsa yetkisiz erişim
        }

        logger.info("Adding item with toyId {} to cart of user with userId {} with quantity {}", toyId, user.getId(), quantity);
        Cart updatedCart = cartService.addItemToCart(user.getId(), toyId, quantity);

        logger.info("Item with toyId {} added to cart of user with userId {} with quantity {}", toyId, user.getId(), quantity);

        return ResponseEntity.ok(updatedCart);
    }

    //getting user-cart
    @GetMapping("/user-cart")
    public ResponseEntity<Cart> getCart(HttpSession session) {

        Userr user = (Userr) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Kullanıcı oturum açmamışsa yetkisiz erişim
        }
        logger.info("GET request received for user-cart with userId {}", user.getId());
        logger.info("Request parameters: userId={}",user.getId());


        Cart cart = cartService.getCart(user.getId());

        logger.info("Retrieved cart of user with userId {}: {}", user.getId(), cart);
        return ResponseEntity.ok(cart);
    }

    // getting user cart items
    @GetMapping("/cart-items")
    public ResponseEntity<List<CartItem>> getCartItems(HttpSession session) {
        Userr user = (Userr) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Kullanıcı oturum açmamışsa yetkisiz erişim
        }

        logger.info("GET request received for cart items of user with userId {}", user.getId());
        logger.info("Request parameters: userId={}", user.getId());

        List<CartItem> cartItems = cartService.getCartItems(user.getId());

        logger.info("Retrieved {} cart items of user with userId {}: {}", cartItems.size(), user.getId(), cartItems);
        return ResponseEntity.ok(cartItems);
    }

    @PostMapping("/remove-item")
    public ResponseEntity<Cart> removeItemFromCart(HttpSession session,
                                                   @RequestParam Long cartItemId,
                                                   @RequestParam(required = false) Integer quantity) {

        Userr user = (Userr) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Kullanıcı oturum açmamışsa yetkisiz erişim
        }

        logger.info("POST request received for removing item with cartItemId {} from cart of user with userId {}", cartItemId, user.getId());
        logger.info("Request parameters: userId={}, cartItemId={}, quantity={}",user.getId(), cartItemId, quantity);

        Cart updatedCart = cartService.removeItemFromCart(user.getId(), cartItemId, quantity);

        logger.info("Item with cartItemId {} removed from cart of user with userId {}: {}", cartItemId,user.getId(), updatedCart);
        return ResponseEntity.ok(updatedCart);
    }

    //buy items on cart
    @PostMapping("/buy-items")
    public ResponseEntity<Cart> buyItemsInCart(HttpSession session) {

        Userr user = (Userr) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Kullanıcı oturum açmamışsa yetkisiz erişim
        }

        logger.info("POST request received for buying items in cart of user with userId {}", user.getId());
        logger.info("Request parameters: userId={}", user.getId());

        Cart updatedCart = cartService.buyItemsInCart(user.getId());

        logger.info("Items bought successfully in cart of user with userId {}: {}", user.getId(), updatedCart);
        return ResponseEntity.ok(updatedCart);
    }


}
