package com.example.quadrangolare_calcio.controller;

import com.example.quadrangolare_calcio.model.ArchivioGiocatore;
import com.example.quadrangolare_calcio.model.Giocatore;
import com.example.quadrangolare_calcio.service.ArchivioGiocatoreService;
import com.example.quadrangolare_calcio.service.GiocatoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/stats/giocatori")
public class ArchivioGiocatoreController {

    @Autowired
    private GiocatoreService giocatoreService;

    @Autowired
    private ArchivioGiocatoreService archivioGiocatoreService;

    // Recupera tutti i giocatori ordinati per Categoria e Cognome
    @GetMapping
    public ResponseEntity<List<Giocatore>> getAllGiocatori() {
        return ResponseEntity.ok(giocatoreService.elencoGiocatori());
    }

    // Dettaglio del singolo giocatore usando l'ID int
    @GetMapping("/{id}")
    public ResponseEntity<Giocatore> getGiocatore(@PathVariable int id) {
        Giocatore g = giocatoreService.dettaglioGiocatore(id);
        return g != null ? ResponseEntity.ok(g) : ResponseEntity.notFound().build();
    }

    // Recupera la rosa completa di una squadra
    @GetMapping("/squadra/{idSquadra}")
    public ResponseEntity<List<Giocatore>> getBySquadra(@PathVariable Long idSquadra) {
        return ResponseEntity.ok(giocatoreService.getGiocatoriPerSquadra(idSquadra));
    }

    // --- STATISTICHE INDIVIDUALI ---

    // Recupera l'archivio (gol, rigori) di un giocatore
    @GetMapping("/{id}/archivio")
    public ResponseEntity<ArchivioGiocatore> getArchivioGiocatore(@PathVariable int id) {
        Giocatore g = giocatoreService.dettaglioGiocatore(id);
        if (g == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(archivioGiocatoreService.getOrCreateArchivio(g));
    }

    // La tua classifica marcatori per torneo
    @GetMapping("/classifica-marcatori/{idTorneo}")
    public ResponseEntity<List<Map<String, Object>>> getClassificaMarcatori(@PathVariable int idTorneo) {
        return ResponseEntity.ok(archivioGiocatoreService.getClassificaMarcatori(idTorneo));
    }

    // Specialista rigori del torneo
    @GetMapping("/specialista-rigori/{idTorneo}")
    public ResponseEntity<Map<String, Object>> getSpecialistaRigori(@PathVariable int idTorneo) {
        return ResponseEntity.ok(archivioGiocatoreService.getSpecialistaRigori(idTorneo));
    }

}
