package com.example.demoToy.dao;

import com.example.demoToy.entity.Toy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ToyRepository extends JpaRepository<Toy, Long> {
}
