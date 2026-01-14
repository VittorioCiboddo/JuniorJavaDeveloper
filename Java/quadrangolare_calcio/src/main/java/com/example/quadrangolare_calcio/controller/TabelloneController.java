package com.example.quadrangolare_calcio.controller;

import com.example.quadrangolare_calcio.model.Squadra;
import com.example.quadrangolare_calcio.service.SquadraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/tabellone")
public class TabelloneController {

    @Autowired
    SquadraService squadraService;

    @GetMapping()
    public String getPage(Model model) {
        List<Squadra> squadreComplete = squadraService.getSquadreComplete();

        List<Map<String, Object>> squadreSemplici = squadreComplete.stream().map(s -> {
            Map<String, Object> map = new HashMap<>();
            map.put("idSquadra", s.getIdSquadra());
            map.put("nome", s.getNome());
            map.put("logo", s.getLogo());

            List<Map<String, String>> giocatoriSemplici = s.getGiocatori().stream().map(g -> {
                Map<String, String> gMap = new HashMap<>();
                gMap.put("nome", g.getNome());
                gMap.put("cognome", g.getCognome());
                gMap.put("categoria", g.getRuolo().getTipologia().getCategoria());
                return gMap;
            }).collect(Collectors.toList());

            map.put("giocatori", giocatoriSemplici);
            return map;

        }).collect(Collectors.toList());

        model.addAttribute("squadre", squadreSemplici); // Passiamo questa al JS
        model.addAttribute("isPronta", squadreComplete.size() >= 4);

        return "tabellone";
    }
}
