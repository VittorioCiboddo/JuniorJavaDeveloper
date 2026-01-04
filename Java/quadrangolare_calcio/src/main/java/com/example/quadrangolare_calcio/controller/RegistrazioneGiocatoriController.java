package com.example.quadrangolare_calcio.controller;

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
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/registra-giocatori")
public class RegistrazioneGiocatoriController {

    @Autowired private GiocatoreService giocatoreService;
    @Autowired private SquadraService squadraService;
    @Autowired private ModuloService moduloService;
    @Autowired private NazionalitaService nazionalitaService;
    @Autowired private TipologiaService tipologiaService;
    @Autowired private RuoloService ruoloService;

    @GetMapping
    public String getPage(Model model,
                          @RequestParam(value = "selectedSquadraId", required = false) Long selectedSquadraId,
                          @RequestParam(value = "selectedModuloId", required = false) Long selectedModuloId,
                          @ModelAttribute("conferma") String conferma) {

        model.addAttribute("giocatore", new Giocatore());
        model.addAttribute("squadre", squadraService.elencoSquadre());
        model.addAttribute("nazionalita", nazionalitaService.elencoNazioni());
        model.addAttribute("tipologie", tipologiaService.elencoTipologie());
        model.addAttribute("ruoli", ruoloService.elencoRuoli());
        model.addAttribute("moduli", moduloService.elencoModuli());

        model.addAttribute("selectedSquadraId", selectedSquadraId);
        model.addAttribute("selectedModuloId", selectedModuloId);
        model.addAttribute("conferma", conferma);

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

        Squadra squadra = squadraService.getSquadraById(squadraId);
        Modulo modulo = moduloService.getModuloById(moduloId);
        Tipologia tipologia = tipologiaService.getById(tipologiaId);
        Ruolo ruolo = ruoloService.getById(ruoloId);
        Nazionalita nazionalita = nazionalitaService.getNazionalitaById(nazionalitaId);

        Giocatore giocatore = new Giocatore();
        giocatore.setNome(nome);
        giocatore.setCognome(cognome);
        giocatore.setNumeroMaglia(numeroMaglia);
        giocatore.setDataNascita(dataNascita);
        giocatore.setDescrizione(descrizione);
        giocatore.setRuolo(ruolo);
        giocatore.setSquadra(squadra);
        giocatore.setNazionalita(nazionalita);

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
        if (squadra == null) return ResponseEntity.notFound().build();
        Map<String, Object> response = new HashMap<>();
        response.put("modulo", squadra.getModulo());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getTipologiePerModulo/{idModulo}")
    public ResponseEntity<Map<String, Object>> getTipologiePerModulo(@PathVariable Long idModulo) {
        List<Tipologia> tipologie = tipologiaService.getTipologiePerModulo(idModulo);
        Map<String, Object> response = new HashMap<>();
        response.put("tipologie", tipologie);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getRuoliDisponibili/{idModulo}")
    public ResponseEntity<Map<String, Object>> getRuoliDisponibili(@PathVariable Long idModulo) {
        Modulo modulo = moduloService.getModuloById(idModulo);
        if (modulo == null) return ResponseEntity.notFound().build();
        List<Ruolo> ruoliDisponibili = ruoloService.getRuoliPerModulo(idModulo);
        for (Ruolo ruolo : ruoliDisponibili) {
            if (giocatoreService.isRuoloGiaAssegnato(ruolo.getIdRuolo())) {
                ruolo.setGiocatoreRegistrato(true);
            }
        }
        Map<String, Object> response = new HashMap<>();
        response.put("ruoli", ruoliDisponibili);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getCategorieDisponibili/{idSquadra}/{idModulo}")
    public ResponseEntity<Map<String, Object>> getCategorieDisponibili(@PathVariable Long idSquadra, @PathVariable Long idModulo) {
        List<Ruolo> ruoliModulo = ruoloService.getRuoliPerModulo(idModulo);
        Map<String, Long> maxCategorie = ruoliModulo.stream()
                .map(r -> r.getTipologia().getCategoria())
                .collect(Collectors.groupingBy(c -> c, Collectors.counting()));

        List<Giocatore> giocatoriSquadra = giocatoreService.getGiocatoriPerSquadra(idSquadra);
        Map<String, Long> giaAssegnati = giocatoriSquadra.stream()
                .map(g -> g.getRuolo().getTipologia().getCategoria())
                .collect(Collectors.groupingBy(c -> c, Collectors.counting()));

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