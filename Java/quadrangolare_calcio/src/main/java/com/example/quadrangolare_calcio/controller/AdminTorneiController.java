package com.example.quadrangolare_calcio.controller;

import com.example.quadrangolare_calcio.model.Torneo;
import com.example.quadrangolare_calcio.service.TorneoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/areaadmin")
public class AdminTorneiController {

    @Autowired
    private TorneoService torneoService;

    // prendo dal db tutti i tornei salvati
    @GetMapping("/admintornei")
    public String gestisciTornei(Model model, HttpSession session) {

        if (session.getAttribute("admin") == null)
            return "redirect:/loginadmin";

        List<Torneo> tornei = torneoService.getAllTornei();

        model.addAttribute("tornei", tornei);

        return "admin-tornei-form";
    }


    // GET e POST per modificare tornei
    @GetMapping("/torneo/modifica")
    public String modificaTorneo(@RequestParam("id") int id, Model model, HttpSession session) {
        if (session.getAttribute("admin") == null)
            return "redirect:/loginadmin";

        Torneo torneo = torneoService.dettaglioTorneo((long) id);

        model.addAttribute("torneo", torneo);

        return "admin-tornei-modifica";
    }


    @PostMapping("/torneo/modifica")
    public String modificaTorneoPost(@ModelAttribute Torneo torneo,
                                     HttpSession session) {
        if (session.getAttribute("admin") == null)
            return "redirect:/loginadmin";

        Torneo esistente = torneoService.dettaglioTorneo((long) torneo.getIdTorneo());

        // NOME
        if (torneo.getNome() != null && !torneo.getNome().isBlank()) {
            esistente.setNome(torneo.getNome());
        }

        torneoService.salvaTorneo(esistente);

        return "redirect:/areaadmin/admintornei";
    }

    // Controllo per ELIMINA torneo
    @GetMapping("/torneo/elimina")
    public String eliminaTorneo(@RequestParam("id") int id, HttpSession session) {
        if (session.getAttribute("admin") == null)
            return "redirect:/loginadmin";

        torneoService.eliminaTorneo((long) id);

        return "redirect:/areaadmin/admintornei";
    }

}
