package com.example.demoToy.controller;

import com.example.demoToy.entity.CartItem;
import com.example.demoToy.entity.Toy;
import com.example.demoToy.service.ToyService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/toys")
@RequiredArgsConstructor
public class ToyController {

    @Autowired
    private ToyService toyService;
    private static final Logger logger = LoggerFactory.getLogger(ToyController.class);

    @GetMapping("/all")
    public ResponseEntity<List<Toy>> getAllToys() {

        logger.info("GET request received for getting all toys.");
        List<Toy> toys = toyService.getAllToys();
        logger.info("Retrieved {} toys.", toys.size());
        return ResponseEntity.ok().body(toys);
    }

    @PostMapping("/add")
    public ResponseEntity<Toy> addToy(@RequestParam String toyName,
                                      @RequestParam Double price,
                                      @RequestParam int stock,
                                      @RequestParam String imageUrl,
                                      @RequestParam String description) {
        logger.info("POST request received for adding toy with name: {}, price: {}, stock: {}, imageUrl: {}, description: {}", toyName, price, stock, imageUrl, description);

        Toy savedToy = toyService.addToy(toyName, price, stock, imageUrl, description);

        logger.info("Toy added successfully with id: {}", savedToy.getId());
        return ResponseEntity.ok(savedToy);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Toy> getToyById(@PathVariable Long id) {
        logger.info("GET request received for getting toy by id: {}", id);
        Toy toy = toyService.getToyById(id);
        if (toy != null) {
            logger.info("Toy found with id: {}", id);
            return ResponseEntity.ok(toy);
        } else {
            logger.warn("Toy not found with id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/increase-stock/{amount}")
    public ResponseEntity<Toy> increaseStock(@PathVariable Long id, @PathVariable Integer amount) {
        logger.info("POST request received for increasing stock of toy with id {} by {}", id, amount);
        Toy toy = toyService.increaseStock(id, amount);
        logger.info("Stock increased successfully for toy with id {}. New stock: {}", id, toy.getStock());
        return ResponseEntity.ok(toy);
    }


    @DeleteMapping("/delete/{toyId}")
    public ResponseEntity<String> deleteToyById(@PathVariable Long toyId) {
        try {
            toyService.deleteToyById(toyId);
            return ResponseEntity.ok("Toy with ID " + toyId + " has been successfully deleted.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred while deleting toy with ID " + toyId + ": " + e.getMessage());
        }
    }





}
