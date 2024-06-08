package com.example.demoToy.service;

import com.example.demoToy.dao.UserRepository;
import com.example.demoToy.dao.WalletRepository;
import com.example.demoToy.entity.Userr;
import com.example.demoToy.entity.Wallet;
import com.example.demoToy.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Transactional
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;


    @Mock
    private WalletRepository walletRepository;


    // when we add a user, we except the same user for added user.
    @Test
    public void testAddUser() {

        String name = "John";
        String surname = "Doe";
        String email = "john.doe@example.com";
        String password = "password123";
        String phoneNumber = "123456789";
        LocalDate birthDate = LocalDate.of(1990, 1, 1);

        Userr mockUser = new Userr();
        mockUser.setName(name);
        mockUser.setSurname(surname);
        mockUser.setEmail(email);
        mockUser.setPassword(password);
        mockUser.setPhoneNumber(phoneNumber);
        mockUser.setBirthdate(birthDate);

        when(userRepository.save(any(Userr.class))).thenReturn(mockUser);

        Userr result = userService.addUser( name, surname, email, password, phoneNumber, birthDate);

        assertEquals(mockUser, result);

        verify(userRepository, times(1)).save(any(Userr.class));
    }

    // if we request to get user with id and its return empty, we except NotFoundException.
    @Test
    public void testFindUserByIdNotFound() {

        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.findUserById(userId));


    }

    // if we add wallet to user and user already has wallet then we except IllegalStateException
    @Test
    public void testAddWalletToUserWhenUserAlreadyHasWallet() {

        Long userId = 1L;
        Userr userWithWallet = new Userr();
        userWithWallet.setWallet(new Wallet());

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(userWithWallet));

        assertThrows(IllegalStateException.class, () -> {
            userService.addWalletToUser(userId);
        });

        verify(walletRepository, never()).save(any(Wallet.class));
    }

}