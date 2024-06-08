package com.example.demoToy.controller;

import com.example.demoToy.entity.Order;
import com.example.demoToy.entity.Userr;
import com.example.demoToy.entity.Wallet;
import com.example.demoToy.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @RequestParam(required = true) String name,
            @RequestParam(required = true) String surname,
            @RequestParam(required = true) String email,
            @RequestParam(required = true) String password,
            @RequestParam(required = true) String phone,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate birthDate) {

        logger.info("Yeni kullanıcı ekleniyor: isim: {}, soyisim: {}, email: {}, telefon: {}, doğum tarihi: {}",
                name, surname, email, phone, birthDate);

        Userr user = userService.addUser(name, surname, email, password, phone, birthDate);

        if (user != null) {
            logger.info("Kullanıcı başarıyla eklendi.");
            return ResponseEntity.status(HttpStatus.CREATED).body("Kullanıcı başarıyla kayıt edildi.");
        } else {
            logger.error("Kullanıcı ekleme başarısız oldu.");
            return ResponseEntity.badRequest().body("Geçersiz istek.");
        }
    }



    @GetMapping("/all")
    public ResponseEntity<List<Userr>> getAllUsers() {
        logger.info("Getting all users.");
        List<Userr> users = userService.getAllUsers();
        logger.info("Retrieved {} users.", users.size());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Userr> getUserrById(@PathVariable Long id) {
        logger.info("Getting user by id: {}", id);
        Userr user = userService.findUserById(id);
        if (user != null) {
            logger.info("User found with id: {}", id);
            return new ResponseEntity<>(user, OK);
        } else {
            logger.warn("User not found with id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getUserOrders(HttpSession session) {

        Userr user = (Userr) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Kullanıcı oturum açmamışsa yetkisiz erişim
        }

        List<Order> orders = userService.getUserOrders(user.getId());
        if (orders.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(orders);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteUser(@PathVariable(name = "id") Long id) {

        logger.info("Deleting user with id: {}", id);
        userService.deleteUser(id);

        logger.info("User deleted successfully.");
        return new ResponseEntity<>("User deleted successfully.", HttpStatus.OK);
    }

    @PostMapping("/add-wallet")
    public ResponseEntity<Userr> addWalletToUser(HttpSession session) {

        Userr user = (Userr) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Kullanıcı oturum açmamışsa yetkisiz erişim
        }

        logger.info("Adding wallet to user with userId: {}", user.getId());
        Userr updatedUser = userService.addWalletToUser(user.getId());
        logger.info("Wallet added successfully to user with userId: {}", user.getId());
        return ResponseEntity.ok(updatedUser);
    }


    @PostMapping("/money-to-wallet")
    public ResponseEntity<Userr> addMoneyToWallet(HttpSession session,
                                                  @RequestParam Double amount) {

        Userr user = (Userr) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Kullanıcı oturum açmamışsa yetkisiz erişim
        }

        logger.info("Adding money {} to wallet of user with userId: {}", amount,user.getId());
        Userr updatedUser = userService.addMoneyToWallet(user.getId(), amount);

        logger.info("Money {} added to wallet of user with userId: {}", amount, user.getId());
        return ResponseEntity.ok(updatedUser);
    }


    @GetMapping("/by-email")
    public ResponseEntity<Userr> getUserByEmail(@RequestParam String email) {
        Userr user = userService.getUserByEmail(email);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/wallet")
    public ResponseEntity<Wallet> getUserWallet(HttpSession session) {

        Userr user = (Userr) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Kullanıcı oturum açmamışsa yetkisiz erişim
        }

        Wallet wallet = userService.getUserWallet(user.getId());
        return ResponseEntity.ok(wallet);
    }



    }

