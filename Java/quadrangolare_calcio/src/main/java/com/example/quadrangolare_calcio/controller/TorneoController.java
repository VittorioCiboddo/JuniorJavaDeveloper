package com.example.quadrangolare_calcio.controller;

import com.example.quadrangolare_calcio.model.Squadra;
import com.example.quadrangolare_calcio.model.enums.Partita;
import com.example.quadrangolare_calcio.service.GiocatoreService;
import com.example.quadrangolare_calcio.service.SquadraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/torneo")
public class TorneoController {

    @Autowired
    private SquadraService squadraService;

    @Autowired
    private GiocatoreService giocatoreService;

    @GetMapping("/match")
    public String avviaPartita(@RequestParam("fase") Partita fase,
                               @RequestParam("idHome") long idHome,
                               @RequestParam("idAway") long idAway,
                               Model model) {

        // 1. Recuperiamo le squadre
        Squadra home = squadraService.getSquadraById(idHome);
        Squadra away = squadraService.getSquadraById(idAway);

        // 2. Prepariamo le liste dei nomi per il JavaScript
        List<String> playersHome = giocatoreService.getGiocatoriPerSquadra(idHome)
                .stream().map(g -> g.getCognome()).toList();

        List<String> playersAway = giocatoreService.getGiocatoriPerSquadra(idAway)
                .stream().map(g -> g.getCognome()).toList();

        // 3. Passiamo tutto alla pagina HTML
        model.addAttribute("teamHome", home.getNome());
        model.addAttribute("playersHome", playersHome);
        model.addAttribute("teamAway", away.getNome());
        model.addAttribute("playersAway", playersAway);
        model.addAttribute("partita", fase.getDescrizione());

        return "partita"; // La pagina HTML con lo script JS
    }

}
