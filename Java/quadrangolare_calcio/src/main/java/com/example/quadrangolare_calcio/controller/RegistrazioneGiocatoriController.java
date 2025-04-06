package com.example.quadrangolare_calcio.controller;

import com.example.quadrangolare_calcio.dao.SquadraDao;
import com.example.quadrangolare_calcio.model.*;
import com.example.quadrangolare_calcio.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


import java.time.LocalDate;

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

//        if (!model.containsAttribute("idSquadra")) {
//            model.addAttribute("idSquadra", null);
//        }
//        if (!model.containsAttribute("idModulo")) {
//            model.addAttribute("idModulo", null);
//        }
//
//        if (!model.containsAttribute("conferma")) {
//            model.addAttribute("conferma", null);
//        }
//        if (!model.containsAttribute("selectedSquadraId")) {
//            model.addAttribute("selectedSquadraId", null);
//        }
//        if (!model.containsAttribute("selectedModuloId")) {
//            model.addAttribute("selectedModuloId", null);
//        }


        return "registrazione-giocatori";

    }

    @PostMapping
    public String registraGiocatore(@RequestParam("squadra") Long squadraId,
                                    @RequestParam("modulo") Long moduloId,
                                    @RequestParam("tipologia") Long tipologiaId,
                                    @RequestParam("ruolo") Long ruoloId,
                                    @RequestParam("nome") String nome,
                                    @RequestParam("cognome") String cognome,
                                    @RequestParam("immagine") MultipartFile immagine,
                                    @RequestParam("numeroMaglia") int numeroMaglia,
                                    @RequestParam("dataNascita") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataNascita,
                                    @RequestParam("nazionalita") Long nazionalitaId,
                                    @RequestParam("descrizione") String descrizione,
                                    RedirectAttributes redirectAttributes) {

        // Recupera gli oggetti dai rispettivi ID
        Squadra squadra = squadraService.getSquadraById(squadraId);
        Modulo modulo = moduloService.getModuloById(moduloId);
        Tipologia tipologia = tipologiaService.getById(id);
        Ruolo ruolo = ruoloService.getById(ruoloId);
        Nazionalita nazionalita = nazionalitaService.getNazionalitaById(id);

        // Crea il giocatore
        Giocatore giocatore = new Giocatore();
        giocatore.setNome(nome);
        giocatore.setCognome(cognome);
        giocatore.setNumeroMaglia(numeroMaglia);
        giocatore.setDataNascita(dataNascita);
        giocatore.setDescrizione(descrizione);
        giocatore.setRuolo(ruolo);
        giocatore.setSquadra(squadra);
        giocatore.setNazionalita(nazionalita);

        // Conversione immagine in Base64
        try {
            byte[] imageBytes = immagine.getBytes();
            String base64 = Base64.getEncoder().encodeToString(imageBytes);
            giocatore.setImmagine(base64);
        } catch (IOException e) {
            e.printStackTrace();
        }

        giocatoreService.registraGiocatore(giocatore);

        redirectAttributes.addFlashAttribute("conferma", nome + " " + cognome + " è stato registrato con successo!");
        redirectAttributes.addAttribute("selectedSquadraId", squadraId);
        redirectAttributes.addAttribute("selectedModuloId", moduloId);

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
            if (giocatoreService.isRuoloGiaAssegnato(ruolo.getIdRuolo())) {
                ruolo.setGiocatoreRegistrato(true); // Imposta come già assegnato
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("ruoli", ruoliDisponibili);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/getCategorieDisponibili/{idSquadra}/{idModulo}")
    public ResponseEntity<Map<String, Object>> getCategorieDisponibili(@PathVariable Long idSquadra, @PathVariable Long idModulo) {
        // Recupera tutti i ruoli del modulo
        List<Ruolo> ruoliModulo = ruoloService.getRuoliPerModulo(idModulo);

        // Calcola max categorie per quel modulo
        Map<String, Long> maxCategorie = ruoliModulo.stream()
                .map(r -> r.getTipologia().getCategoria())
                .collect(Collectors.groupingBy(c -> c, Collectors.counting()));

        // Recupera giocatori già registrati per la squadra
        List<Giocatore> giocatoriSquadra = giocatoreService.getGiocatoriPerSquadra(idSquadra);

        // Calcola categorie già assegnate
        Map<String, Long> giaAssegnati = giocatoriSquadra.stream()
                .map(g -> g.getRuolo().getTipologia().getCategoria())
                .collect(Collectors.groupingBy(c -> c, Collectors.counting()));

        // Costruzione risposta
        List<Map<String, Object>> categorie = new ArrayList<>();
        for (String categoria : maxCategorie.keySet()) {
            Map<String, Object> entry = new HashMap<>();
            long max = maxCategorie.get(categoria);
            long count = giaAssegnati.getOrDefault(categoria, 0L);

            entry.put("categoria", categoria);
            entry.put("completata", count >= max);

            categorie.add(entry);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("categorieDisponibili", categorie);
        return ResponseEntity.ok(response);
    }




}
