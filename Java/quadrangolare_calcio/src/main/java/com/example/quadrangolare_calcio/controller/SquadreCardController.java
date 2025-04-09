package com.example.quadrangolare_calcio.controller;

import com.example.quadrangolare_calcio.model.Squadra;
import com.example.quadrangolare_calcio.service.SquadraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/squadre-disponibili")
public class SquadreCardController {

     @Autowired
     SquadraService squadraService;

     @GetMapping
    public String getPage (Model model) {
         List<Squadra> squadra = squadraService.elencoSquadre();
         model.addAttribute("squadra", squadra);
         return "squadre";
     }
}
