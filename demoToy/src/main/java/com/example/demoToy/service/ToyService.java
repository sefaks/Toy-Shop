package com.example.demoToy.service;

import com.example.demoToy.dao.ToyRepository;
import com.example.demoToy.entity.Toy;
import com.example.demoToy.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ToyService {


    private final ToyRepository toyRepository;

    public Toy addToy(String toyName,Double price,int stock,String imageUrl,String description){

        Toy newToy = new Toy();

        newToy.setName(toyName);
        newToy.setPrice(price != null ? price : 0.0);
        newToy.setStock(stock);
        newToy.setImageUrl(imageUrl);
        newToy.setDescription(description);
        return toyRepository.save(newToy);
    }

    public Toy getToyById(Long id) {
        return toyRepository.findById(id).orElse(null);
    }



    public List<Toy> getAllToys() {

        return toyRepository.findAll();
    }

    public Toy increaseStock(Long toyId, int stock) {
        Toy toy = getToyById(toyId);
        if (toy == null) {
            throw new NotFoundException("Cant found toy with this id" + toyId);
        } else {
            toy.setStock(toy.getStock() + stock);
        }
        return toyRepository.save(toy);
    }

    public void deleteToyById(Long toyId) {

        toyRepository.deleteById(toyId);
    }



}
