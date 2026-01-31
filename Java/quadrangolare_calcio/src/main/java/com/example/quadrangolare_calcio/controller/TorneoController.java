package com.example.quadrangolare_calcio.controller;

import com.example.quadrangolare_calcio.dto.TorneoSalvataggioDTO;
import com.example.quadrangolare_calcio.model.Torneo;
import com.example.quadrangolare_calcio.service.TorneoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/stats/tornei")
public class TorneoController {

    @Autowired
    private TorneoService torneoService;

    // --- GESTIONE TORNEO ---

    @PostMapping("/salva-completo")
    public ResponseEntity<String> salvaTorneoCompleto(@RequestBody TorneoSalvataggioDTO dto) {
        try {
            torneoService.salvaTorneoIntero(dto);
            return ResponseEntity.ok("Torneo salvato con successo!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Errore durante il salvataggio: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Torneo> getDettaglioTorneo(@PathVariable Long id) {
        Torneo torneo = torneoService.getDettaglioTorneo(id);
        return torneo != null ? ResponseEntity.ok(torneo) : ResponseEntity.notFound().build();
    }

    // Restituisce la classifica finale (1°, 2°, 3°, 4°) di uno specifico torneo
    @GetMapping("/{id}/classifica")
    public ResponseEntity<Map<Integer, String>> getClassificaTorneo(@PathVariable int id) {
        return ResponseEntity.ok(torneoService.getClassificaTorneo(id));
    }

    // Restituisce il podio (primi 3) con le emoji delle medaglie
    @GetMapping("/{id}/podio")
    public ResponseEntity<List<String>> getPodio(@PathVariable int id) {
        return ResponseEntity.ok(torneoService.getPodio(id));
    }

    // Restituisce statistiche generali (gol totali, media) di un torneo
    @GetMapping("/{id}/stats")
    public ResponseEntity<Map<String, Object>> getStatsTorneo(@PathVariable int id) {
        return ResponseEntity.ok(torneoService.getStatsTorneo(id));
    }

    // --- STATISTICHE STORICHE (ALL-TIME) ---

    // L'Albo d'Oro: lista di tutti i vincitori di ogni torneo
    @GetMapping("/albo-doro")
    public ResponseEntity<List<Map<String, String>>> getAlboDOro() {
        return ResponseEntity.ok(torneoService.getAlboDOro());
    }

    // Il Medagliere Storico: ordina le squadre per Ori, Argenti, Bronzi e Quarti posti
    @GetMapping("/medagliere")
    public ResponseEntity<List<Map<String, Object>>> getMedagliereStorico() {
        return ResponseEntity.ok(torneoService.getMedagliereStorico());
    }

    // Ranking basato solo sui trofei vinti
    @GetMapping("/ranking")
    public ResponseEntity<List<Map<String, Object>>> getRanking() {
        return ResponseEntity.ok(torneoService.getRankingStorico());
    }

    // Hall of Fame: record storici (miglior attacco, squadra più vincente)
    @GetMapping("/hall-of-fame")
    public ResponseEntity<Map<String, Object>> getHallOfFame() {
        return ResponseEntity.ok(torneoService.getHallOfFame());
    }

    @GetMapping("/classifica-marcatori")
    public ResponseEntity<List<Map<String, Object>>> getClassificaMarcatoriAllTime() {
        return ResponseEntity.ok(torneoService.getClassificaMarcatoriAllTime());
    }

    @GetMapping("/classifica-portieri")
    public ResponseEntity<List<Map<String, Object>>> getClassificaPortieriAllTime() {
        return ResponseEntity.ok(torneoService.getClassificaPortieriAllTime());
    }


}
