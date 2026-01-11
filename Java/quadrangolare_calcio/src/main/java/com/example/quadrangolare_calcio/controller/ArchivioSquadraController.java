package com.example.quadrangolare_calcio.controller;

import com.example.quadrangolare_calcio.model.ArchivioSquadra;
import com.example.quadrangolare_calcio.model.Squadra;
import com.example.quadrangolare_calcio.service.ArchivioSquadraService;
import com.example.quadrangolare_calcio.service.SquadraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/stats/squadre")
public class ArchivioSquadraController {

    @Autowired
    private SquadraService squadraService;

    @Autowired
    private ArchivioSquadraService archivioSquadraService;

    // Utilizzo il metodo elencoSquadre() che restituisce la lista ordinata per nome
    @GetMapping
    public ResponseEntity<List<Squadra>> getAllSquadre() {
        return ResponseEntity.ok(squadraService.elencoSquadre());
    }

    // Utilizzo il metodo dettaglioSquadra()
    @GetMapping("/{id}")
    public ResponseEntity<Squadra> getSquadra(@PathVariable Long id) {
        Squadra squadra = squadraService.dettaglioSquadra(id);
        return squadra != null ? ResponseEntity.ok(squadra) : ResponseEntity.notFound().build();
    }

    // Endpoint per le statistiche storiche
    @GetMapping("/{id}/archivio")
    public ResponseEntity<ArchivioSquadra> getArchivioSquadra(@PathVariable Long id) {
        Squadra squadra = squadraService.dettaglioSquadra(id);
        if (squadra == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(archivioSquadraService.getOrCreateArchivio(squadra));
    }

    // Recupero squadre con 11 giocatori
    @GetMapping("/complete")
    public ResponseEntity<List<Squadra>> getSquadreComplete() {
        return ResponseEntity.ok(squadraService.getSquadreComplete());
    }

}
