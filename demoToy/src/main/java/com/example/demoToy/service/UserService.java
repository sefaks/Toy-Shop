package com.example.demoToy.service;

import com.example.demoToy.dao.CartRepository;
import com.example.demoToy.dao.ToyRepository;
import com.example.demoToy.dao.UserRepository;
import com.example.demoToy.dao.WalletRepository;
import com.example.demoToy.entity.*;
import com.example.demoToy.exception.AlreadyExistException;
import com.example.demoToy.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {


    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private  final CartRepository cartRepository;

    @Autowired
    private  final ToyRepository toyRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @Autowired
    private WalletRepository walletRepository;

    public Userr addUser( String name, String surname, String email, String password, String phoneNumber, LocalDate birthDate) {

        Userr existingUser = userRepository.findUserByEmail(email);
        if (existingUser != null) {
            throw new AlreadyExistException("This email already associated another user. Try another email: " + email);
        }

        Userr user = new Userr();
        user.setName(name);
        user.setSurname(surname);
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setBirthdate(birthDate);
        String encodedPassword = passwordEncoder.encode(password);
        user.setPassword(encodedPassword);

        Userr savedUser = userRepository.save(user);
        return savedUser;
    }

    public List<Userr> getAllUsers(){

        List<Userr> users =  userRepository.findAll();
        return users;
    }

    public Userr findUserById(Long userId){

        return userRepository.findById(userId).
            orElseThrow(()-> new NotFoundException("User not found with id: " + userId));
    }

    public void deleteUser(Long userId){

        Userr user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User not found with id: " + userId));

        userRepository.delete(user);
    }



    public Userr addWalletToUser(Long userId) {
        Userr user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User not found with id: " + userId));

        if (user.getWallet() != null) {
            throw new IllegalStateException("User already has a wallet.");
        }
        Wallet wallet = new Wallet();
        wallet.setWalletOwner(user);
        wallet.setBudget(0.0);

        Wallet savedWallet = walletRepository.save(wallet);

        return user;
    }

    public Userr addMoneyToWallet(Long userId, Double amount ){


        Userr user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User not found with id: " + userId));

        if(user.getWallet() == null){
            throw new NotFoundException("No associated wallet for this user: " + userId);
        }
        Wallet wallet = user.getWallet();
        wallet.setBudget(wallet.getBudget()+amount);
        walletRepository.save(wallet);

        return user;
    }

    public List<Order> getUserOrders(Long userId){

        Userr user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User not found with id: " + userId));

        List<Order> orders = user.getOrders();
        return orders;
    }

    public Userr getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Wallet getUserWallet(Long userId) {
        Userr user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User not found with id: " + userId));

        return user.getWallet();

    }
    public boolean authenticate(String email, String password) {
        Userr user = userRepository.findByEmail(email);
        if (user != null) {
            return passwordEncoder.matches(password, user.getPassword());
        }
        return false;
    }



}
