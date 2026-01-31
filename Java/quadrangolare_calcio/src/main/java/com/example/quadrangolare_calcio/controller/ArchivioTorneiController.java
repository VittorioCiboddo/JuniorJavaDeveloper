package com.example.quadrangolare_calcio.controller;

import com.example.quadrangolare_calcio.repository.TorneoRepository;
import com.example.quadrangolare_calcio.service.TorneoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/archivio/tornei")
public class ArchivioTorneiController {

    @Autowired
    private TorneoRepository torneoRepository;

    @Autowired
    private TorneoService torneoService;

    @GetMapping
    public String getPage(Model model) {
        boolean esistonoTornei = !torneoRepository.findAll().isEmpty();
        model.addAttribute("esistonoTornei", esistonoTornei);

        if (esistonoTornei) {
            model.addAttribute("alboDOro", torneoService.getAlboDOro());           // List<Map<String,String>>
            model.addAttribute("rankingSquadre", torneoService.getMedagliereStorico()); // List<Map<String,Object>>
            model.addAttribute("hallOfFame", torneoService.getHallOfFame());       // Map<String,Object>
            model.addAttribute("classificaMarcatori", torneoService.getClassificaMarcatoriAllTime());
            model.addAttribute("classificaPortieri", torneoService.getClassificaPortieriAllTime());
        }

        return "archivio-tornei";
    }

}
