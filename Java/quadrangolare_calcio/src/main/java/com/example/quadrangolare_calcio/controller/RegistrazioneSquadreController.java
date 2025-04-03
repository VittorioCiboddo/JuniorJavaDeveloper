package com.example.quadrangolare_calcio.controller;

import com.example.quadrangolare_calcio.model.Modulo;
import com.example.quadrangolare_calcio.model.Nazionalita;
import com.example.quadrangolare_calcio.model.Squadra;
import com.example.quadrangolare_calcio.service.ModuloService;
import com.example.quadrangolare_calcio.service.NazionalitaService;
import com.example.quadrangolare_calcio.service.SquadraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/registra-squadre")
public class RegistrazioneSquadreController {

    @Autowired
    private SquadraService squadraService;

    @Autowired
    private ModuloService moduloService;

    @Autowired
    private NazionalitaService nazionalitaService;

    private Squadra squadra;

    @GetMapping
    public String getPage(Model model) {
        Squadra squadra = new Squadra();
        List<Modulo> moduli = moduloService.elencoModuli();
        List<Nazionalita> nazionalita = nazionalitaService.elencoNazioni();
        model.addAttribute("squadra", squadra);
        model.addAttribute("modulo", moduli);
        model.addAttribute("nazionalita", nazionalita);
        return "registrazione-squadre";
    }

    @PostMapping
    public String formManager(@RequestParam("nome") String nome,
                              @RequestParam("logo") MultipartFile logo,
                              @RequestParam("nazionalita") Nazionalita nazionalita,
                              @RequestParam("allenatore") String allenatore,
                              @RequestParam("modulo") Modulo modulo,
                              @RequestParam("capitano") String capitano,
                              @RequestParam("descrizione") String descrizione,
                              Model model) {

        squadraService.registraSquadra(nome, logo, nazionalita, allenatore, modulo, capitano, descrizione);
        model.addAttribute("message", "Squadra registrata con successo!");
        return "redirect:/registra-squadre";
    }

}