package com.example.demoToy.service;

import com.example.demoToy.dao.ToyRepository;
import com.example.demoToy.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import com.example.demoToy.entity.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Transactional
@ExtendWith(MockitoExtension.class)
class ToyServiceTest {


    @InjectMocks
    private ToyService toyService;

    @Mock
    private ToyRepository toyRepository;

    @Test
    public void testAddToyWithNullValues() {

        String toyName = null;
        Double price = null;
        int stock = 0;
        String imageUrl = null;
        String description = null;

        Toy result = toyService.addToy(toyName, price, stock, imageUrl, description);

        assertEquals(null, result);

    }
    @Test
    public void testGetToyByIdNotFound() {

        when(toyRepository.findById(2L)).thenReturn(Optional.empty());

        // Metodu çağır ve sonucu al
        Toy result = toyService.getToyById(2L);

        // Sonucun null olup olmadığını kontrol et
        assertEquals(null, result);
    }
    @Test()
    public void testIncreaseStockNotFound() {

        // burada empty() çağırdığımız için NotFoundException bekliyoruz..
        when(toyRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> toyService.increaseStock(1L, 3));
    }
}