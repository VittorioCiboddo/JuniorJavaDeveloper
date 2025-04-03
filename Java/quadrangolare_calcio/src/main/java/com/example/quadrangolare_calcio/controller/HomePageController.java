package com.example.quadrangolare_calcio.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

// localhost:8080
@Controller
@RequestMapping("/")
public class HomePageController {

    @GetMapping
    public String getPage() {
        return "home-page";
    }
}
