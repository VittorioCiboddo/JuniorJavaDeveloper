package com.example.quadrangolare_calcio.controller;

import com.example.quadrangolare_calcio.model.Allenatore;
import com.example.quadrangolare_calcio.service.AllenatoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/dettaglioallenatore")
public class DettaglioAllenatoreController {

    @Autowired
    private AllenatoreService allenatoreService;

    @GetMapping
    public String getDettaglioAllenatore(@RequestParam("id") Long idAllenatore, Model model) {
        Allenatore allenatore = allenatoreService.dettaglioAllenatore(idAllenatore);
        if (allenatore == null) {
            return "redirect:/squadre"; // o pagina di errore
        }

        model.addAttribute("allenatore", allenatore);
        return "dettaglio-allenatore";
    }
}
