package com.example.quadrangolare_calcio.controller;

import com.example.quadrangolare_calcio.model.*;
import com.example.quadrangolare_calcio.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Controller
@RequestMapping("/areaadmin")
public class AdminGiocatoreController {

    @Autowired
    private GiocatoreService giocatoreService;

    @Autowired
    private SquadraService squadraService;

    @Autowired
    private ModuloService moduloService;

    @Autowired
    private RuoloService ruoloService;

    @Autowired
    private NazionalitaService nazionalitaService;

    @GetMapping("/admingiocatori")
    public String gestisciGiocatori(@RequestParam("tipologia") String tipologia, Model model, HttpSession session) {

        System.out.println(">>> [DEBUG] tipologia ricevuta: " + tipologia);

        if (session.getAttribute("admin") == null)
            return "redirect:/loginadmin";

        List<Giocatore> giocatori = giocatoreService.getByTipologia(tipologia);
        List<Squadra> squadreConSlotDisponibili = squadraService.getSquadreConSpazioPerCategoria(tipologia);

        model.addAttribute("tipologiaFissata", tipologia);
        model.addAttribute("giocatori", giocatori);
        model.addAttribute("squadreDisponibili", squadreConSlotDisponibili);

        return "admin-giocatori-form";
    }

    @GetMapping("/giocatori")
    public String selezioneTipologiaGiocatori(HttpSession session) {
        if (session.getAttribute("admin") == null)
            return "redirect:/loginadmin";
        return "admin-giocatori-selezione";
    }


    @GetMapping("/giocatore/nuovo")
    public String nuovoGiocatore(@RequestParam("tipologia") String tipologia, Model model, HttpSession session) {
        if (session.getAttribute("admin") == null)
            return "redirect:/loginadmin";

        List<Squadra> squadreDisponibili = squadraService.getSquadreConSpazioPerCategoria(tipologia);
        model.addAttribute("tipologiaFissata", tipologia);
        model.addAttribute("squadreDisponibili", squadreDisponibili);
        model.addAttribute("nazionalita", nazionalitaService.elencoNazioni());


        return "admin-giocatori-aggiungi";
    }


    @PostMapping("/giocatore/aggiungi")
    public String aggiungiGiocatore(@ModelAttribute Giocatore giocatore,
                                    @RequestParam("ruolo.tipologia.categoria") String tipologia,
                                    @RequestParam("immagineFile") MultipartFile immagineFile,
                                    HttpSession session) {

        if (session.getAttribute("admin") == null)
            return "redirect:/loginadmin";

        if (immagineFile != null && !immagineFile.isEmpty()) {
            try {
                String formato = immagineFile.getContentType();
                String immagineCodificata = "data:" + formato + ";base64," +
                        Base64.getEncoder().encodeToString(immagineFile.getBytes());
                giocatore.setImmagine(immagineCodificata);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        giocatoreService.salvaGiocatore(giocatore);
        return "redirect:/areaadmin/admingiocatori?tipologia=" + tipologia;
    }



    @GetMapping("/giocatore/modifica")
    public String modificaGiocatore(@RequestParam("id") int id, Model model, HttpSession session) {
        if (session.getAttribute("admin") == null)
            return "redirect:/loginadmin";

        Giocatore giocatore = giocatoreService.dettaglioGiocatore(id);
        model.addAttribute("giocatore", giocatore);
        model.addAttribute("squadre", squadraService.getAllSquadre());
        model.addAttribute("moduli", moduloService.getAllModuli());
        model.addAttribute("ruoli", ruoloService.getRuoliPerCategoria(giocatore.getRuolo().getTipologia()));

        return "admin-modifica-giocatori";
    }

    @PostMapping("/giocatore/modifica")
    public String salvaModificheGiocatore(@ModelAttribute Giocatore giocatore,
                                          @RequestParam("immagineFile") MultipartFile immagineFile,
                                          HttpSession session) {
        if (session.getAttribute("admin") == null)
            return "redirect:/loginadmin";

        Giocatore esistente = giocatoreService.dettaglioGiocatore(giocatore.getIdGiocatore());

        // NOME
        if (giocatore.getNome() != null && !giocatore.getNome().isBlank()) {
            esistente.setNome(giocatore.getNome());
        }

        // COGNOME
        if (giocatore.getCognome() != null && !giocatore.getCognome().isBlank()) {
            esistente.setCognome(giocatore.getCognome());
        }

        // DATA DI NASCITA
        if (giocatore.getDataNascita() != null) {
            esistente.setDataNascita(giocatore.getDataNascita());
        }

        // NUMERO MAGLIA
        if (giocatore.getNumeroMaglia() != esistente.getNumeroMaglia()) {
            esistente.setNumeroMaglia(giocatore.getNumeroMaglia());
        }

        // DESCRIZIONE (anche vuota va bene se volontaria)
        if (giocatore.getDescrizione() != null) {
            esistente.setDescrizione(giocatore.getDescrizione());
        }

        // SQUADRA
        if (giocatore.getSquadra() != null) {
            esistente.setSquadra(giocatore.getSquadra());
        }

        // RUOLO
        if (giocatore.getRuolo() != null) {
            esistente.setRuolo(giocatore.getRuolo());
        }

        // NAZIONALITÀ
        if (giocatore.getNazionalita() != null) {
            esistente.setNazionalita(giocatore.getNazionalita());
        }

        // IMMAGINE
        if (immagineFile != null && !immagineFile.isEmpty()) {
            try {
                String formato = immagineFile.getContentType();
                String immagineCodificata = "data:" + formato + ";base64," +
                        Base64.getEncoder().encodeToString(immagineFile.getBytes());
                esistente.setImmagine(immagineCodificata);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        giocatoreService.aggiornaGiocatore(esistente);


        String categoria;
        if (esistente.getRuolo() != null && esistente.getRuolo().getTipologia() != null
                && esistente.getRuolo().getTipologia().getCategoria() != null) {
            categoria = esistente.getRuolo().getTipologia().getCategoria();
            return "redirect:/areaadmin/admingiocatori?tipologia=" + categoria;
        } else {
            System.out.println("⚠️ Errore: tipologia o ruolo null durante redirect, ritorno alla selezione");
            return "redirect:/areaadmin/giocatori";
        }


    }


    @GetMapping("/giocatore/elimina")
    public String eliminaGiocatore(@RequestParam("id") int id,
                                   @RequestParam("tipologia") String tipologia,
                                   HttpSession session) {
        if (session.getAttribute("admin") == null)
            return "redirect:/loginadmin";

        giocatoreService.eliminaGiocatore(id);
        return "redirect:/areaadmin/admingiocatori?tipologia=" + tipologia;
    }

}

