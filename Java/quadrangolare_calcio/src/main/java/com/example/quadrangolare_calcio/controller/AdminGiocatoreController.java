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
    public String salvaModificheGiocatore(@ModelAttribute Giocatore giocatore, HttpSession session) {
        if (session.getAttribute("admin") == null)
            return "redirect:/loginadmin";

        giocatoreService.aggiornaGiocatore(giocatore);
        return "redirect:/areaadmin/admingiocatori?tipologia=" + giocatore.getRuolo().getTipologia();
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

