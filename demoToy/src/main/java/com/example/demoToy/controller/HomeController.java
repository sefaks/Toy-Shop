package com.example.demoToy.controller;

import com.example.demoToy.entity.Userr;
import com.example.demoToy.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HomeController {


    private final UserService userService;

    // kullanıcının mail ve passwordunu kontrol et, eğer doğru ise session'a kaydet ve login olsun.
    @PostMapping("/log")
    public String login(@RequestParam("email") String email, @RequestParam("password") String password, HttpSession session) {

        if (userService.authenticate(email, password)) {
            Userr user = userService.getUserByEmail(email);
            session.setAttribute("user", user);
            return "redirect:/toys/all";
        } else {
            return "redirect:/login?error=true";
        }
    }
}
