package com.example.quadrangolare_calcio.controller;

import com.example.quadrangolare_calcio.model.*;
import com.example.quadrangolare_calcio.service.ModuloService;
import com.example.quadrangolare_calcio.service.NazionalitaService;
import com.example.quadrangolare_calcio.service.SquadraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
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

    private Allenatore allenatore;

    private Stadio stadio;

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
                              @RequestParam("modulo") Modulo modulo,
                              @RequestParam("capitano") String capitano,
                              @RequestParam("descrizione") String descrizione,
                              @RequestParam(value = "nomeAllenatore", required = false) String nomeAllenatore,
                              @RequestParam("cognomeAllenatore") String cognomeAllenatore,
                              @RequestParam("nazionalitaAllenatore") Nazionalita nazionalitaAllenatore,
                              @RequestParam("immagineAllenatore") MultipartFile immagineAllenatore,
                              @RequestParam("nomeStadio") String nomeStadio,
                              @RequestParam("immagineStadio") MultipartFile immagineStadio,
                              Model model) {


        Squadra squadra = squadraService.registraSquadra(nome, logo, nazionalita, modulo, capitano, descrizione);

        Allenatore allenatore = new Allenatore();
        allenatore.setNome(nomeAllenatore);
        allenatore.setCognome(cognomeAllenatore);
        allenatore.setNazionalita(nazionalitaAllenatore);
        allenatore.setSquadra(squadra);
        allenatore.setImmagine("data:" + immagineAllenatore.getContentType() + ";base64," +
                Base64.getEncoder().encodeToString(immagineAllenatore.getBytes()));
        allenatoreService.salvaAllenatore(allenatore);

        Stadio stadio = new Stadio();
        stadio.setNome(nomeStadio);
        stadio.setSquadra(squadra);
        stadio.setImmagine("data:" + immagineStadio.getContentType() + ";base64," +
                Base64.getEncoder().encodeToString(immagineStadio.getBytes()));
        stadioService.salvaStadio(stadio);



        model.addAttribute("message", "Squadra registrata con successo!");
        return "redirect:/registra-squadre";
    }

}