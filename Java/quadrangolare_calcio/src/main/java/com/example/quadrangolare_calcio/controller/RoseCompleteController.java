package com.example.quadrangolare_calcio.controller;

import com.example.quadrangolare_calcio.model.*;
import com.example.quadrangolare_calcio.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/formazioni")
public class RoseCompleteController {

    @Autowired
    private GiocatoreService giocatoreService;

    @Autowired
    private AllenatoreService allenatoreService;

    @Autowired
    private StadioService stadioService;

    @Autowired
    private SquadraService squadraService;

    Modulo modulo;

    @GetMapping("/{id}")
    public String mostraFormazione(@PathVariable("id") Long idSquadra, Model model) {
        Squadra squadra = squadraService.getSquadraById(idSquadra);
        Allenatore allenatore = allenatoreService.getAllenatoreBySquadraId(idSquadra);
        Stadio stadio = stadioService.getStadioBySquadraId(idSquadra);
        List<Giocatore> tuttiGiocatori = giocatoreService.getGiocatoriPerSquadra(idSquadra);

        Map<String, Integer> ordineCategoria = Map.of(
                "Portiere", 1,
                "Difensore", 2,
                "Centrocampista", 3,
                "Attaccante", 4
        );

        List<Giocatore> giocatoriOrdinati = tuttiGiocatori.stream()
                .sorted(Comparator
                        .comparing((Giocatore g) -> ordineCategoria.getOrDefault(
                                g.getRuolo().getTipologia().getCategoria(), 5))
                        .thenComparing(Giocatore::getCognome))
                .peek(g -> {
                    if (g.getImmagine() != null && !g.getImmagine().startsWith("data:image")) {
                        g.setImmagine("data:image/png;base64," + g.getImmagine());
                    }
                })
                .collect(Collectors.toList());

        String modulo = squadra.getModulo().getSchemaGioco();

        model.addAttribute("squadra", squadra);
        model.addAttribute("allenatore", allenatore);
        model.addAttribute("stadio", stadio);
        model.addAttribute("giocatori", giocatoriOrdinati);
        model.addAttribute("modulo", modulo);

        return "rose-complete";
    }

}

