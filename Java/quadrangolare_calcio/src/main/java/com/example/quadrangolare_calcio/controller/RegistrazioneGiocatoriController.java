package com.example.quadrangolare_calcio.controller;

import com.example.quadrangolare_calcio.dao.SquadraDao;
import com.example.quadrangolare_calcio.model.*;
import com.example.quadrangolare_calcio.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/registra-giocatori")
public class RegistrazioneGiocatoriController {

    @Autowired
    private GiocatoreService giocatoreService;

    @Autowired
    private SquadraService squadraService;

    @Autowired
    private ModuloService moduloService;

    @Autowired
    private NazionalitaService nazionalitaService;

    @Autowired
    private TipologiaService tipologiaService;

    @Autowired
    private RuoloService ruoloService;

    private Giocatore giocatore;

    private Modulo modulo;

    @GetMapping
    private String getPage(Model model) {
        Giocatore giocatore = new Giocatore();

        List<Squadra> squadre = squadraService.elencoSquadre();
        List<Nazionalita> nazionalita = nazionalitaService.elencoNazioni();
        List<Tipologia> tipologie = tipologiaService.elencoTipologie();
        List<Ruolo> ruoli = ruoloService.elencoRuoli();

        model.addAttribute("giocatore", giocatore);
        model.addAttribute("squadre", squadre);
        model.addAttribute("nazionalita", nazionalita);
        model.addAttribute("tipologie", tipologie);
        model.addAttribute("ruoli", ruoli);

        return "registrazione-giocatori";

    }

    @PostMapping
    public String formManager (@RequestParam("nome") String nome,
                               @RequestParam("cognome") String cognome,
                               @RequestParam("immagine") MultipartFile immagine,
                               @RequestParam("numeroMaglia") int numeroMaglia,
                               @RequestParam("dataNascita") LocalDate dataNascita,
                               @RequestParam("descrizione") String descrizione,
                               @RequestParam("ruolo")Ruolo ruolo,
                               @RequestParam("squadra") Squadra squadra,
                               @RequestParam("nazionalita")Nazionalita nazionalita,
                               Model model) {
        giocatoreService.registraGiocatore(nome, cognome, immagine, numeroMaglia, dataNascita, descrizione, ruolo, squadra, nazionalita);
        return "redirect:/registra-giocatori";
    }

    @GetMapping("/getModuloPerSquadra/{idSquadra}")
    public ResponseEntity<Map<String, Object>> getModuloPerSquadra(@PathVariable Long idSquadra) {
        Squadra squadra = squadraService.getSquadraById(idSquadra);
        if (squadra == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("modulo", squadra.getModulo()); // Recupera il modulo direttamente dalla squadra

        return ResponseEntity.ok(response);
    }

    @GetMapping("/getTipologiePerModulo/{idModulo}")
    public ResponseEntity<Map<String, Object>> getTipologiePerModulo(@PathVariable Long idModulo) {
        // Supponiamo che tu abbia un servizio che gestisce le tipologie
        List<Tipologia> tipologie = tipologiaService.getTipologiePerModulo(idModulo);
        Map<String, Object> response = new HashMap<>();
        response.put("tipologie", tipologie); // Aggiungi le tipologie associate al modulo
        return ResponseEntity.ok(response);
    }



    @GetMapping("/getRuoliDisponibili/{idModulo}")
    public ResponseEntity<Map<String, Object>> getRuoliDisponibili(@PathVariable Long idModulo) {
        Modulo modulo = moduloService.getModuloById(idModulo);
        if (modulo == null) {
            return ResponseEntity.notFound().build();
        }

        // Ottieni i ruoli per il modulo
        List<Ruolo> ruoliDisponibili = ruoloService.getRuoliPerModulo(idModulo);

        // Aggiungi l'informazione sul fatto che il ruolo sia già assegnato
        for (Ruolo ruolo : ruoliDisponibili) {
            if (giocatoreService.isRuoloGiaAssegnato(ruolo.getId())) {
                ruolo.setGiocatoreRegistrato(true); // Imposta come già assegnato
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("ruoli", ruoliDisponibili);

        return ResponseEntity.ok(response);
    }



}
