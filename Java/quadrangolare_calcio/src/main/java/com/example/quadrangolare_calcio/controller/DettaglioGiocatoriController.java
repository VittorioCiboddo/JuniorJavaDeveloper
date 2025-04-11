package com.example.quadrangolare_calcio.controller;

import com.example.quadrangolare_calcio.model.Giocatore;
import com.example.quadrangolare_calcio.service.GiocatoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/dettagliogiocatori")
public class DettaglioGiocatoriController {

    @Autowired
    private GiocatoreService giocatoreService;

    @GetMapping
    public String getDettaglioGiocatore(@RequestParam("id") int idGiocatore, Model model) {
        Giocatore giocatore = giocatoreService.dettaglioGiocatore(idGiocatore);
        if (giocatore == null) {
            return "redirect:/squadre"; // o pagina di errore
        }

        model.addAttribute("giocatore", giocatore);
        return "dettaglio-giocatori";
    }

}
