package com.example.quadrangolare_calcio.controller;

import com.example.quadrangolare_calcio.model.Allenatore;
import com.example.quadrangolare_calcio.model.Modulo;
import com.example.quadrangolare_calcio.model.Nazionalita;
import com.example.quadrangolare_calcio.model.Squadra;
import com.example.quadrangolare_calcio.service.AllenatoreService;
import com.example.quadrangolare_calcio.service.ModuloService;
import com.example.quadrangolare_calcio.service.NazionalitaService;
import com.example.quadrangolare_calcio.service.SquadraService;
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
public class AdminAllenatoreController {

    @Autowired
    private AllenatoreService allenatoreService;

    @Autowired
    private SquadraService squadraService;

    @Autowired
    private NazionalitaService nazionalitaService;

    @Autowired
    private ModuloService moduloService;

    @GetMapping("/adminallenatori")
    public String gestisciAllenatori(Model model, HttpSession session) {
        if (session.getAttribute("admin") == null)
            return "redirect:/loginadmin";

        List<Allenatore> allenatori = allenatoreService.getAllAllenatori();
        List<Squadra> squadreDisponibili = squadraService.getSquadreSenzaAllenatore();

        model.addAttribute("allenatori", allenatori);
        model.addAttribute("squadreDisponibili", squadreDisponibili);
        model.addAttribute("nazionalita", nazionalitaService.elencoNazioni());
        model.addAttribute("moduli", moduloService.getAllModuli());

        return "admin-allenatore-form";
    }

    // controllo per aggiungere un nuovo allenatore
    @PostMapping("/allenatore/aggiungi")
    public String salvaAllenatore(@ModelAttribute Allenatore allenatore,
                                  @RequestParam("immagineFile") MultipartFile immagineFile,
                                  HttpSession session) {
        if (session.getAttribute("admin") == null)
            return "redirect:/loginadmin";

        if (!immagineFile.isEmpty()) {
            try {
                String base64 = "data:" + immagineFile.getContentType() + ";base64," +
                        Base64.getEncoder().encodeToString(immagineFile.getBytes());
                allenatore.setImmagine(base64);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        allenatoreService.salvaAllenatore(allenatore);
        return "redirect:/areaadmin/adminallenatori";
    }

    // GET e POST per modificare allenatori
    @GetMapping("/allenatore/modifica")
    public String modificaAllenatore(@RequestParam("id") int id, Model model, HttpSession session) {
        if (session.getAttribute("admin") == null)
            return "redirect:/loginadmin";

        Allenatore allenatore = allenatoreService.dettaglioAllenatore((long) id);
        List<Squadra> squadre = (List<Squadra>) squadraService.getAllSquadre();
        List<Nazionalita> nazionalita = nazionalitaService.elencoNazioni();
        List<Modulo> moduli = (List<Modulo>) moduloService.getAllModuli();

        model.addAttribute("allenatore", allenatore);
        model.addAttribute("squadre", squadre);
        model.addAttribute("moduli", moduli);
        model.addAttribute("nazionalita", nazionalita);

        return "admin-allenatore-modifica";
    }


    @PostMapping("/allenatore/modifica")
    public String modificaAllenatorePost(@ModelAttribute Allenatore allenatore,
                                         @RequestParam("immagineFile") MultipartFile immagineFile,
                                         HttpSession session) {
        if (session.getAttribute("admin") == null)
            return "redirect:/loginadmin";

        Allenatore esistente = allenatoreService.dettaglioAllenatore(allenatore.getIdAllenatore());

        // NOME
        if (allenatore.getNome() != null && !allenatore.getNome().isBlank()) {
            esistente.setNome(allenatore.getNome());
        }

        // COGNOME
        if (allenatore.getCognome() != null && !allenatore.getCognome().isBlank()) {
            esistente.setCognome(allenatore.getCognome());
        }

        // DESCRIZIONE
        if (allenatore.getDescrizione() != null) {
            esistente.setDescrizione(allenatore.getDescrizione());
        }

        // SQUADRA
        if (allenatore.getSquadra() != null) {
            esistente.setSquadra(allenatore.getSquadra());
        }

        // NAZIONALITÀ
        if (allenatore.getNazionalita() != null) {
            esistente.setNazionalita(allenatore.getNazionalita());
        }

        // MODULO (aggiornato tramite la squadra)
        if (allenatore.getSquadra() != null && allenatore.getSquadra().getModulo() != null) {
            esistente.getSquadra().setModulo(allenatore.getSquadra().getModulo());
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

        allenatoreService.salvaAllenatore(esistente);

        return "redirect:/areaadmin/adminallenatori";
    }


    // Controllo per ELIMINA allenatore
    @GetMapping("/allenatore/elimina")
    public String eliminaAllenatore(@RequestParam("id") int id, HttpSession session) {
        if (session.getAttribute("admin") == null)
            return "redirect:/loginadmin";

        allenatoreService.eliminaAllenatore(id);

        return "redirect:/areaadmin/adminallenatori";
    }





}
