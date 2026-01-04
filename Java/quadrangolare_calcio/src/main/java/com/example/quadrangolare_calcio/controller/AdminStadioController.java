package com.example.quadrangolare_calcio.controller;

import com.example.quadrangolare_calcio.model.*;
import com.example.quadrangolare_calcio.service.SquadraService;
import com.example.quadrangolare_calcio.service.StadioService;
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
public class AdminStadioController {

    @Autowired
    private StadioService stadioService;

    @Autowired
    private SquadraService squadraService;

    @GetMapping("/adminstadio")
    public String gestisciStadio(Model model, HttpSession session) {
        if (session.getAttribute("admin") == null)
            return "redirect:/loginadmin";

        List<Stadio> stadio = stadioService.getAllStadi();
        List<Squadra> squadreDisponibili = squadraService.getSquadreSenzaStadio();

        model.addAttribute("stadi", stadio);
        model.addAttribute("squadreDisponibili", squadreDisponibili);


        return "admin-stadio-form";
    }


    // controllo per aggiungere un nuovo stadio
    @PostMapping("/stadio/aggiungi")
    public String salvaStadio(@ModelAttribute Stadio stadio,
                                  @RequestParam("immagineFile") MultipartFile immagineFile,
                                  HttpSession session) {
        if (session.getAttribute("admin") == null)
            return "redirect:/loginadmin";

        if (!immagineFile.isEmpty()) {
            try {
                String base64 = "data:" + immagineFile.getContentType() + ";base64," +
                        Base64.getEncoder().encodeToString(immagineFile.getBytes());
                stadio.setImmagine(base64);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        stadioService.salvaStadio(stadio);
        return "redirect:/areaadmin/adminstadio";
    }


    // GET e POST per modificare stadi
    @GetMapping("/stadio/modifica")
    public String modificaStadio(@RequestParam("id") int id, Model model, HttpSession session) {
        if (session.getAttribute("admin") == null)
            return "redirect:/loginadmin";

        Stadio stadio = stadioService.dettaglioStadio(id);
        List<Squadra> squadre = (List<Squadra>) squadraService.getAllSquadre();

        model.addAttribute("stadio", stadio);
        model.addAttribute("squadre", squadre);

        return "admin-stadio-modifica";
    }


    @PostMapping("/stadio/modifica")
    public String modificaStadioPost(@ModelAttribute Stadio stadio,
                                         @RequestParam("immagineFile") MultipartFile immagineFile,
                                         HttpSession session) {
        if (session.getAttribute("admin") == null)
            return "redirect:/loginadmin";

        Stadio esistente = stadioService.dettaglioStadio(stadio.getIdStadio());

        // NOME
        if (stadio.getNome() != null && !stadio.getNome().isBlank()) {
            esistente.setNome(stadio.getNome());
        }


        // DESCRIZIONE
        if (stadio.getDescrizione() != null) {
            esistente.setDescrizione(stadio.getDescrizione());
        }

        // SQUADRA
        if (stadio.getSquadra() != null) {
            esistente.setSquadra(stadio.getSquadra());
        }

        // CAPIENZA
        if (stadio.getCapienza() != esistente.getCapienza()) {
            esistente.setCapienza(stadio.getCapienza());
        }

        // ULTRAS
        if (stadio.getUltras() != null) {
            esistente.setUltras(stadio.getUltras());
        }

        // IMMAGINE
        if (immagineFile != null && !immagineFile.isEmpty()) {
            try {
                String formato = immagineFile.getContentType();
                String immagineCodificata = "data:" + formato + ";base64," +
                        Base64.getEncoder().encodeToString(immagineFile.getBytes());
                esistente.setImmagine(immagineCodificata);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        stadioService.salvaStadio(esistente);

        return "redirect:/areaadmin/adminstadio";
    }


    // Controllo per ELIMINA stadio
    @GetMapping("/stadio/elimina")
    public String eliminaStadio(@RequestParam("id") int id, HttpSession session) {
        if (session.getAttribute("admin") == null)
            return "redirect:/loginadmin";

        stadioService.eliminaStadio(id);

        return "redirect:/areaadmin/adminstadio";
    }

}
