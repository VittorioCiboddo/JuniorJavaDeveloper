package com.example.quadrangolare_calcio.controller;

import com.example.quadrangolare_calcio.model.Allenatore;
import com.example.quadrangolare_calcio.model.Squadra;
import com.example.quadrangolare_calcio.model.Stadio;
import com.example.quadrangolare_calcio.service.*;
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

    @Autowired
    private AllenatoreService allenatoreService;

    @Autowired
    private StadioService stadioService;

    @GetMapping
    public String getPage (@RequestParam("id") Long idSquadra, Model model) {
        Squadra squadra = squadraService.dettaglioSquadra(idSquadra);
        Allenatore allenatore = allenatoreService.getAllenatoreBySquadraId(idSquadra);
        Stadio stadio = stadioService.getStadioBySquadraId(idSquadra);

        if (squadra == null) {
            System.out.println("Squadra non trovata con ID: " + idSquadra);
        } else {
            System.out.println("Squadra trovata: " + squadra.getNome());
        }
        model.addAttribute("squadra", squadra);
        model.addAttribute("allenatore", allenatore);
        model.addAttribute("stadio", stadio);
        return "dettaglio-squadre";
    }
}
