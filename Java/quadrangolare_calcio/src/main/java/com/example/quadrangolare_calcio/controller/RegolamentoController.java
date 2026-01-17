package com.example.quadrangolare_calcio.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/regolamento")
public class RegolamentoController {

    @GetMapping
    public String getPage() {

        return "regolamento";
    }

}
