package com.example.quadrangolare_calcio.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

// localhost:8080
@Controller
@RequestMapping("/")
public class IntroController {

    @GetMapping
    public String getPage() {
        return "intro";
    }
}
