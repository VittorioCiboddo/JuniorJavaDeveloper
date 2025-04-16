package com.example.quadrangolare_calcio.controller;

import com.example.quadrangolare_calcio.model.Modulo;
import com.example.quadrangolare_calcio.model.Ruolo;
import com.example.quadrangolare_calcio.service.GiocatoreService;
import com.example.quadrangolare_calcio.service.RuoloService;
import com.example.quadrangolare_calcio.service.SquadraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AdminGiocatoreRestController {

    @Autowired
    private SquadraService squadraService;

    @Autowired
    private RuoloService ruoloService;

    @Autowired
    private GiocatoreService giocatoreService;

    @GetMapping("/squadra/{id}/modulo")
    @ResponseBody
    public ResponseEntity<?> getModuloBySquadra(@PathVariable("id") int id) {
        try {
            Modulo modulo = squadraService.getModuloBySquadra(id);
            if (modulo != null) {
                return ResponseEntity.ok(modulo);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Modulo non trovato per questa squadra.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore interno: " + e.getMessage());
        }
    }


    @GetMapping("/ruoliDisponibili")
    public List<Ruolo> getRuoliDisponibili(@RequestParam String categoria, @RequestParam int moduloId) {
        List<Ruolo> ruoli = ruoloService.getRuoliDisponibiliPerCategoriaEModulo(categoria, moduloId);
        for (Ruolo r : ruoli) {
            if (giocatoreService.isRuoloGiaAssegnato(r.getIdRuolo())) {
                r.setGiocatoreRegistrato(true); // serve setGiocatoreRegistrato nel model
            }
        }
        return ruoli;
    }

}

