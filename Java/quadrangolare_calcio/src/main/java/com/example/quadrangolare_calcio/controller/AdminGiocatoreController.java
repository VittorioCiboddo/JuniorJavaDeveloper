package com.example.quadrangolare_calcio.controller;

import com.example.quadrangolare_calcio.model.*;
import com.example.quadrangolare_calcio.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/admingiocatori")
    public String gestisciGiocatori(@RequestParam("tipologia") String tipologia, Model model, HttpSession session) {
        if (session.getAttribute("admin") == null)
            return "redirect:/loginadmin";

        List<Giocatore> giocatori = giocatoreService.getByTipologia(tipologia);
        List<Squadra> squadreConSlotDisponibili = squadraService.getSquadreConSpazioPerCategoria(tipologia);

        model.addAttribute("tipologiaFissata", tipologia);
        model.addAttribute("giocatori", giocatori);
        model.addAttribute("squadreDisponibili", squadreConSlotDisponibili);

        return "admin-giocatori";
    }

    @PostMapping("/giocatore/aggiungi")
    public String aggiungiGiocatore(@ModelAttribute Giocatore giocatore, HttpSession session) {
        if (session.getAttribute("admin") == null)
            return "redirect:/loginadmin";

        giocatoreService.salvaGiocatore(giocatore);
        return "redirect:/areaadmin/admingiocatori?tipologia=" + giocatore.getRuolo().getTipologia();
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

        return "admin-modifica-giocatore";
    }

    @PostMapping("/giocatore/modifica")
    public String salvaModificheGiocatore(@ModelAttribute Giocatore giocatore, HttpSession session) {
        if (session.getAttribute("admin") == null)
            return "redirect:/loginadmin";

        giocatoreService.aggiornaGiocatore(giocatore);
        return "redirect:/areaadmin/admingiocatori?tipologia=" + giocatore.getRuolo().getTipologia();
    }

    @GetMapping("/giocatore/elimina")
    public String eliminaGiocatore(@RequestParam("id") int id, HttpSession session) {
        if (session.getAttribute("admin") == null)
            return "redirect:/loginadmin";

        giocatoreService.eliminaGiocatore(id);
        return "redirect:/areaadmin/admingiocatori";
    }
}

