package com.example.quadrangolare_calcio.controller;

import com.example.quadrangolare_calcio.model.Giocatore;
import com.example.quadrangolare_calcio.model.Modulo;
import com.example.quadrangolare_calcio.model.Nazionalita;
import com.example.quadrangolare_calcio.model.Squadra;
import com.example.quadrangolare_calcio.service.GiocatoreService;
import com.example.quadrangolare_calcio.service.ModuloService;
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
public class AdminSquadraController {
    
    @Autowired
    private SquadraService squadraService;

    @Autowired
    private ModuloService moduloService;

    @Autowired
    private GiocatoreService giocatoreService;

    @GetMapping("/adminsquadra")
    public String gestisciSquadre(Model model, HttpSession session) {
        if (session.getAttribute("admin") == null)
            return "redirect:/loginadmin";

        Squadra squadra = (Squadra) squadraService.getAllSquadre();
        model.addAttribute("squadra", squadra);
        return "admin-squadre-form";
    }

    @GetMapping("/squadra/aggiungi")
    public String nuovaSquadra(Model model, HttpSession session) {
        if (session.getAttribute("admin") == null)
            return "redirect:/loginadmin";

        model.addAttribute("squadra", new Squadra());
        model.addAttribute("moduli", moduloService.getAllModuli());
        return "admin-squadre-aggiungi";
    }

    @PostMapping("/squadra/aggiungi")
    public String salvaSquadra(@ModelAttribute Squadra squadra,
                               @RequestParam("immagineFile") MultipartFile immagineFile,
                               HttpSession session) {
        if (session.getAttribute("admin") == null)
            return "redirect:/loginadmin";

        // Gestione immagine
        if (!immagineFile.isEmpty()) {
            try {
                String base64 = "data:" + immagineFile.getContentType() + ";base64," +
                        Base64.getEncoder().encodeToString(immagineFile.getBytes());
                squadra.setLogo(base64);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Gestione campo capitano con messaggio segnaposto
        if (squadra.getCapitano() != null && squadra.getCapitano().equalsIgnoreCase("Il capitano sarà scelto successivamente")) {
            squadra.setCapitano(null);
        }

        squadraService.salvaSquadra(squadra);
        return "redirect:/areaadmin/adminsquadre";
    }


    @GetMapping("/squadra/modifica")
    public String modificaSquadra(@RequestParam("id") int id, Model model, HttpSession session) {
        if (session.getAttribute("admin") == null)
            return "redirect:/loginadmin";

        Squadra squadra = squadraService.getSquadraById((long) id);
        List<Modulo> moduli = (List<Modulo>) moduloService.getAllModuli();
        List<Giocatore> giocatoriSquadra = giocatoreService.getGiocatoriPerSquadra((long) id);

        model.addAttribute("squadra", squadra);
        model.addAttribute("moduli", moduli);
        model.addAttribute("giocatoriSquadra", giocatoriSquadra);

        return "admin-squadre-modifica";
    }

    @PostMapping("/squadra/modifica")
    public String modificaSquadraPost(@ModelAttribute Squadra squadra,
                                      @RequestParam("immagineFile") MultipartFile immagineFile,
                                      HttpSession session) {
        if (session.getAttribute("admin") == null)
            return "redirect:/loginadmin";

        Squadra esistente = squadraService.getSquadraById(squadra.getIdSquadra());

        if (squadra.getNome() != null && !squadra.getNome().isBlank()) {
            esistente.setNome(squadra.getNome());
        }

        if (squadra.getModulo() != null) {
            esistente.setModulo(squadra.getModulo());
        }

        if (squadra.getCapitano() != null) {
            esistente.setCapitano(squadra.getCapitano());
        }

        if (immagineFile != null && !immagineFile.isEmpty()) {
            try {
                String formato = immagineFile.getContentType();
                String immagineCodificata = "data:" + formato + ";base64," +
                        Base64.getEncoder().encodeToString(immagineFile.getBytes());
                esistente.setLogo(immagineCodificata);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        squadraService.salvaSquadra(esistente);
        return "redirect:/areaadmin/adminsquadre";
    }

    @GetMapping("/squadra/elimina")
    public String eliminaSquadra(@RequestParam("id") int id, HttpSession session) {
        if (session.getAttribute("admin") == null)
            return "redirect:/loginadmin";

        squadraService.eliminaSquadra(id);
        return "redirect:/areaadmin/adminsquadra";
    }

}

