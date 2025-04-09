package com.example.quadrangolare_calcio.controller;

import com.example.quadrangolare_calcio.model.Squadra;
import com.example.quadrangolare_calcio.service.GiocatoreService;
import com.example.quadrangolare_calcio.service.NazionalitaService;
import com.example.quadrangolare_calcio.service.SquadraService;
import com.example.quadrangolare_calcio.service.StadioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/dettagliosquadre")
public class DettaglioSquadreController {

    @Autowired
    private SquadraService squadraService;

    @GetMapping
    public String getPage (@RequestParam("id") int idSquadra, Model model) {
        Squadra squadra = squadraService.dettaglioSquadra(idSquadra);
        if (squadra == null) {
            System.out.println("Squadra non trovata con ID: " + idSquadra);
        } else {
            System.out.println("Squadra trovata: " + squadra.getNome());
        }
        model.addAttribute("squadra", squadra);
        return "dettaglio-squadre";
    }
}
