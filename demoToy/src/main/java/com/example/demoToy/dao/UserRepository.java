package com.example.demoToy.dao;

import com.example.demoToy.entity.Userr;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Userr,Long> {


    Userr findUserByEmail(String email);

    Userr findByEmail(String email);
}
