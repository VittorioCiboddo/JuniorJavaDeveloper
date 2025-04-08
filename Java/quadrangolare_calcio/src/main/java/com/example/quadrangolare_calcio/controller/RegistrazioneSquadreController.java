package com.example.quadrangolare_calcio.controller;

import com.example.quadrangolare_calcio.model.*;
import com.example.quadrangolare_calcio.service.*;
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

    @Autowired
    private AllenatoreService allenatoreService;

    @Autowired
    private StadioService stadioService;

    private Allenatore allenatore;

    private Stadio stadio;

    private Squadra squadra;

    @GetMapping
    public String getPage(Model model) {
        Squadra squadra = new Squadra();
        Allenatore allenatore = new Allenatore();
        Stadio stadio = new Stadio();
        List<Modulo> moduli = moduloService.elencoModuli();
        List<Nazionalita> nazionalita = nazionalitaService.elencoNazioni();
        model.addAttribute("squadra", squadra);
        model.addAttribute("allenatore", allenatore);
        model.addAttribute("stadio", stadio);
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
                              @RequestParam("nazionalitaAllenatore") Long idNazionalitaAllenatore,
                              @RequestParam("immagineAllenatore") MultipartFile immagineAllenatore,
                              @RequestParam("nomeStadio") String nomeStadio,
                              @RequestParam("immagineStadio") MultipartFile immagineStadio,
                              Model model) {


        Squadra squadra = squadraService.registraSquadra(nome, logo, nazionalita, modulo, capitano, descrizione);

        Allenatore allenatore = new Allenatore();
        allenatore.setNome(nomeAllenatore);
        allenatore.setCognome(cognomeAllenatore);
        Nazionalita nazionalitaAllenatore = nazionalitaService.getNazionalitaById(idNazionalitaAllenatore);
        allenatore.setSquadra(squadra);
        if (immagineAllenatore != null && !immagineAllenatore.isEmpty()) {
            try {
                String formato = immagineAllenatore.getContentType();
                String immagineCodificata = "data:" + formato + ";base64," +
                        Base64.getEncoder().encodeToString(immagineAllenatore.getBytes());
                allenatore.setImmagine(immagineCodificata);
            } catch (Exception e) {
                System.out.println("Error encoding image: " + e.getMessage());
            }
        }
        allenatoreService.salvaAllenatore(allenatore);

        Stadio stadio = new Stadio();
        stadio.setNome(nomeStadio);
        stadio.setSquadra(squadra);
        if (immagineStadio != null && !immagineStadio.isEmpty()) {
            try {
                String formato = immagineStadio.getContentType();
                String immagineCodificata = "data:" + formato + ";base64," +
                        Base64.getEncoder().encodeToString(immagineStadio.getBytes());
                stadio.setImmagine(immagineCodificata);
            } catch (Exception e) {
                System.out.println("Error encoding image: " + e.getMessage());
            }
        }
        stadioService.salvaStadio(stadio);

        model.addAttribute("message", "Squadra registrata con successo!");
        return "redirect:/registra-squadre";
    }

}