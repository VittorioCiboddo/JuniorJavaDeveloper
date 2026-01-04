package com.example.quadrangolare_calcio.controller;

import com.example.quadrangolare_calcio.model.Stadio;
import com.example.quadrangolare_calcio.service.StadioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/dettagliostadio")
public class DettaglioStadioController {

    @Autowired
    private StadioService stadioService;

    @GetMapping
    public String getDettaglioStadio(@RequestParam("id") int idStadio, Model model) {
        Stadio stadio = stadioService.dettaglioStadio(idStadio);
        if (stadio == null) {
            return "redirect:/squadre"; // o pagina di errore
        }

        model.addAttribute("stadio", stadio);
        return "dettaglio-stadio";
    }
}
