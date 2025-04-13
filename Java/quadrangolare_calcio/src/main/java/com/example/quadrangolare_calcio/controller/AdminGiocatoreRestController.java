package com.example.quadrangolare_calcio.controller;

import com.example.quadrangolare_calcio.model.Modulo;
import com.example.quadrangolare_calcio.model.Ruolo;
import com.example.quadrangolare_calcio.service.RuoloService;
import com.example.quadrangolare_calcio.service.SquadraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AdminGiocatoreRestController {

    @Autowired
    private SquadraService squadraService;

    @Autowired
    private RuoloService ruoloService;

    @GetMapping("/squadra/{id}/modulo")
    public Modulo getModuloBySquadra(@PathVariable("id") int id) {
        return squadraService.getModuloBySquadra(id);
    }

    @GetMapping("/ruoliDisponibili")
    public List<Ruolo> getRuoliDisponibili(@RequestParam String categoria, @RequestParam int moduloId) {
        return ruoloService.getRuoliDisponibiliPerCategoriaEModulo(categoria, moduloId);
    }
}

