package com.example.quadrangolare_calcio.controller;

import com.example.quadrangolare_calcio.model.*;
import com.example.quadrangolare_calcio.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/areaadmin")
public class AdminSquadraController {

    @Autowired
    private SquadraService squadraService;

    @Autowired
    private ModuloService moduloService;

    @Autowired
    private GiocatoreService giocatoreService;

    @Autowired
    private AllenatoreService allenatoreService;

    @Autowired
    private StadioService stadioService;

    @Autowired
    private NazionalitaService nazionalitaService;

    @GetMapping("/adminsquadra")
    public String gestisciSquadre(Model model, HttpSession session) {
        if (session.getAttribute("admin") == null)
            return "redirect:/loginadmin";

        List<Squadra> squadre = (List<Squadra>) squadraService.getAllSquadre();
        List<Allenatore> allenatori = allenatoreService.getAllAllenatori();

        // Mappa che associa idSquadra → allenatore
        Map<Long, Allenatore> allenatoriPerSquadra = new HashMap<>();
        for (Allenatore a : allenatori) {
            if (a.getSquadra() != null) {
                allenatoriPerSquadra.put(a.getSquadra().getIdSquadra(), a);
            }
        }

        model.addAttribute("squadre", squadre);
        model.addAttribute("allenatoriPerSquadra", allenatoriPerSquadra);

        return "admin-squadre-form";
    }

    @GetMapping("/squadra/aggiungi")
    public String nuovaSquadra(Model model, HttpSession session) {
        if (session.getAttribute("admin") == null)
            return "redirect:/loginadmin";

        model.addAttribute("squadra", new Squadra());
        model.addAttribute("allenatore", new Allenatore());
        model.addAttribute("stadio", new Stadio());
        model.addAttribute("moduli", moduloService.getAllModuli());
        model.addAttribute("nazionalita", squadraService.getAllNazionalita());

        return "admin-squadre-aggiungi";
    }



    @PostMapping("/squadra/aggiungi")
    public String salvaSquadra(@ModelAttribute Squadra squadra,
                               @RequestParam("immagineFile") MultipartFile immagineFile,
                               @RequestParam("nomeAllenatore") String nomeAllenatore,
                               @RequestParam("cognomeAllenatore") String cognomeAllenatore,
                               @RequestParam("nazionalitaAllenatore") Long idNazionalitaAllenatore,
                               @RequestParam("immagineAllenatore") MultipartFile immagineAllenatore,
                               @RequestParam("descrizioneAllenatore") String descrizioneAllenatore,
                               @RequestParam("nomeStadio") String nomeStadio,
                               @RequestParam("capienza") int capienza,
                               @RequestParam("ultras") String ultras,
                               @RequestParam("immagineStadio") MultipartFile immagineStadio,
                               @RequestParam("descrizioneStadio") String descrizioneStadio,
                               HttpSession session) {
        if (session.getAttribute("admin") == null)
            return "redirect:/loginadmin";

        // Salva logo squadra
        if (!immagineFile.isEmpty()) {
            try {
                String base64 = "data:" + immagineFile.getContentType() + ";base64," +
                        Base64.getEncoder().encodeToString(immagineFile.getBytes());
                squadra.setLogo(base64);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Rimuove testo placeholder del capitano
        if (squadra.getCapitano() != null && squadra.getCapitano().equalsIgnoreCase("Il capitano sarà scelto successivamente")) {
            squadra.setCapitano(null);
        }

        // Salva prima la squadra per ottenere ID
        squadraService.salvaSquadra(squadra);

        // Crea e salva Allenatore
        Allenatore allenatore = new Allenatore();
        allenatore.setNome(nomeAllenatore);
        allenatore.setCognome(cognomeAllenatore);
        allenatore.setDescrizione(descrizioneAllenatore);
        allenatore.setSquadra(squadra); // Associazione

        Nazionalita nazionalitaAllenatore = squadraService.getNazionalitaById(idNazionalitaAllenatore);
        allenatore.setNazionalita(nazionalitaAllenatore);

        if (!immagineAllenatore.isEmpty()) {
            try {
                String base64 = "data:" + immagineAllenatore.getContentType() + ";base64," +
                        Base64.getEncoder().encodeToString(immagineAllenatore.getBytes());
                allenatore.setImmagine(base64);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        allenatoreService.salvaAllenatore(allenatore);

        // Crea e salva Stadio
        Stadio stadio = new Stadio();
        stadio.setNome(nomeStadio);
        stadio.setCapienza(capienza);
        stadio.setUltras(ultras);
        stadio.setDescrizione(descrizioneStadio);
        stadio.setSquadra(squadra);

        if (!immagineStadio.isEmpty()) {
            try {
                String base64 = "data:" + immagineStadio.getContentType() + ";base64," +
                        Base64.getEncoder().encodeToString(immagineStadio.getBytes());
                stadio.setImmagine(base64);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        stadioService.salvaStadio(stadio);

        return "redirect:/areaadmin/adminsquadra";
    }


    @GetMapping("/squadra/modifica")
    public String modificaSquadra(@RequestParam("id") int id, Model model, HttpSession session) {
        if (session.getAttribute("admin") == null)
            return "redirect:/loginadmin";

        Squadra squadra = squadraService.getSquadraById((long) id);
        List<Modulo> moduli = (List<Modulo>) moduloService.getAllModuli();
        List<Giocatore> giocatoriSquadra = giocatoreService.getGiocatoriPerSquadra((long) id);
        Allenatore allenatore = allenatoreService.getAllenatoreBySquadraId((long) id);
        Stadio stadio = stadioService.getStadioBySquadraId((long) id);
        List<Nazionalita> nazionalita = nazionalitaService.elencoNazioni();

        model.addAttribute("squadra", squadra);
        model.addAttribute("moduli", moduli);
        model.addAttribute("giocatoriSquadra", giocatoriSquadra);
        model.addAttribute("allenatore", allenatore);
        model.addAttribute("stadio", stadio);
        model.addAttribute("nazionalita", nazionalita);

        return "admin-squadre-modifica";
    }


    @PostMapping("/squadra/modifica")
    public String modificaSquadraPost(@RequestParam("idSquadra") Long idSquadra,
                                      @RequestParam("nome") String nome,
                                      @RequestParam("descrizione") String descrizione,
                                      @RequestParam("capitano") String capitano,
                                      @RequestParam("nazionalita") Long idNazionalita,
                                      @RequestParam("modulo") Long idModulo,
                                      @RequestParam(value = "immagineFile", required = false) MultipartFile immagineFile,

                                      @RequestParam("nomeAllenatore") String nomeAllenatore,
                                      @RequestParam("cognomeAllenatore") String cognomeAllenatore,
                                      @RequestParam("nazionalitaAllenatore") Long idNazAllenatore,
                                      @RequestParam("descrizioneAllenatore") String descrizioneAllenatore,
                                      @RequestParam(value = "immagineAllenatore", required = false) MultipartFile immagineAllenatore,

                                      @RequestParam("nomeStadio") String nomeStadio,
                                      @RequestParam("descrizioneStadio") String descrizioneStadio,
                                      @RequestParam("capienza") Integer capienza,
                                      @RequestParam("ultras") String ultras,
                                      @RequestParam(value = "immagineStadio", required = false) MultipartFile immagineStadio,

                                      HttpSession session) {
        if (session.getAttribute("admin") == null)
            return "redirect:/loginadmin";

        Squadra esistente = squadraService.getSquadraById(idSquadra);
        if (esistente == null)
            return "redirect:/areaadmin/adminsquadra";

        esistente.setNome(nome);
        esistente.setDescrizione(descrizione);
        esistente.setCapitano(capitano);
        esistente.setNazionalita(nazionalitaService.getNazionalitaById(idNazionalita));
        esistente.setModulo(moduloService.getModuloById(idModulo));

        if (immagineFile != null && !immagineFile.isEmpty()) {
            try {
                String base64 = "data:" + immagineFile.getContentType() + ";base64," +
                        Base64.getEncoder().encodeToString(immagineFile.getBytes());
                esistente.setLogo(base64);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        squadraService.salvaSquadra(esistente);

        Allenatore allenatore = allenatoreService.getAllenatoreBySquadraId(idSquadra);
        if (allenatore == null) allenatore = new Allenatore();
        allenatore.setNome(nomeAllenatore);
        allenatore.setCognome(cognomeAllenatore);
        allenatore.setDescrizione(descrizioneAllenatore);
        allenatore.setNazionalita(nazionalitaService.getNazionalitaById(idNazAllenatore));
        allenatore.setSquadra(esistente);

        if (immagineAllenatore != null && !immagineAllenatore.isEmpty()) {
            try {
                String base64 = "data:" + immagineAllenatore.getContentType() + ";base64," +
                        Base64.getEncoder().encodeToString(immagineAllenatore.getBytes());
                allenatore.setImmagine(base64);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        allenatoreService.salvaAllenatore(allenatore);

        Stadio stadio = stadioService.getStadioBySquadraId(idSquadra);
        if (stadio == null) stadio = new Stadio();
        stadio.setNome(nomeStadio);
        stadio.setDescrizione(descrizioneStadio);
        stadio.setCapienza(capienza);
        stadio.setUltras(ultras);
        stadio.setSquadra(esistente);

        if (immagineStadio != null && !immagineStadio.isEmpty()) {
            try {
                String base64 = "data:" + immagineStadio.getContentType() + ";base64," +
                        Base64.getEncoder().encodeToString(immagineStadio.getBytes());
                stadio.setImmagine(base64);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        stadioService.salvaStadio(stadio);

        return "redirect:/areaadmin/adminsquadra";
    }





    @GetMapping("/squadra/elimina")
    public String eliminaSquadra(@RequestParam("id") int id, HttpSession session) {
        if (session.getAttribute("admin") == null)
            return "redirect:/loginadmin";

        squadraService.eliminaSquadra(id);
        return "redirect:/areaadmin/adminsquadra";
    }

}

