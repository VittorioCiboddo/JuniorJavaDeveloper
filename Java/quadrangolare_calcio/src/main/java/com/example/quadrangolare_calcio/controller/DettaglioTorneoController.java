package com.example.quadrangolare_calcio.controller;

import com.example.quadrangolare_calcio.model.Torneo;
import com.example.quadrangolare_calcio.service.TorneoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dettagliotorneo")
public class DettaglioTorneoController {

    @Autowired
    private TorneoService torneoService;

    @GetMapping("/{idTorneo}")
    public String getDettaglioTorneo(
            @PathVariable Long idTorneo,
            Model model
    ) {

        Torneo torneo = torneoService.getDettaglioTorneo(idTorneo);

        model.addAttribute("torneo", torneo);
        model.addAttribute("stats", torneoService.getStatsTorneo(idTorneo.intValue()));
        model.addAttribute("classifica", torneoService.getClassificaTorneoDettagliata(idTorneo.intValue()));
        model.addAttribute("miniTabellone", torneoService.getMiniTabellone(idTorneo.intValue()));
        model.addAttribute("classificaMarcatori", torneoService.getClassificaMarcatoriDettagliata(idTorneo.intValue()));
        model.addAttribute("classificaPortieri", torneoService.getClassificaPortieriDettagliata(idTorneo.intValue()));


        return "dettaglio-torneo";
    }



}
