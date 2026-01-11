package com.example.quadrangolare_calcio.controller;

import com.example.quadrangolare_calcio.model.Partita;
import com.example.quadrangolare_calcio.service.PartitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stats/partite")
public class PartitaController {

    @Autowired
    private PartitaService partitaService;

    /**
     * Endpoint chiamato dallo script JS ogni volta che viene generato un evento (Goal o Rigore).
     * Mappa i parametri inviati dalla logica di simulazione.
     */
    @PostMapping("/{idPartita}/evento")
    public ResponseEntity<Void> registraEvento(
            @PathVariable int idPartita,
            @RequestParam int secondi,
            @RequestParam int idEvento,
            @RequestParam int idGiocatore) {

        partitaService.registraEventoSimulato((long) idPartita, secondi, idEvento, idGiocatore);
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint chiamato dallo script JS alla fine del match o della lotteria dei rigori.
     * Salva il risultato finale e aggiorna le statistiche delle squadre.
     */
    @PostMapping("/{idPartita}/fine")
    public ResponseEntity<Void> salvaFinePartita(
            @PathVariable int idPartita,
            @RequestParam String resRegular,
            @RequestParam String resFinale,
            @RequestParam boolean aiRigori) {

        partitaService.salvaRisultatoFinale(idPartita, resRegular, resFinale, aiRigori);
        return ResponseEntity.ok().build();
    }

    /**
     * Recupera tutte le partite di un determinato torneo.
     */
    @GetMapping("/torneo/{idTorneo}")
    public ResponseEntity<List<Partita>> getPartiteTorneo(@PathVariable int idTorneo) {
        return ResponseEntity.ok(partitaService.getPartitePerTorneo(idTorneo));
    }

    /**
     * Recupera il dettaglio di una singola partita.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Partita> getPartita(@PathVariable Long id) {
        Partita p = partitaService.getDettaglioPartita(id);
        return p != null ? ResponseEntity.ok(p) : ResponseEntity.notFound().build();
    }

}
