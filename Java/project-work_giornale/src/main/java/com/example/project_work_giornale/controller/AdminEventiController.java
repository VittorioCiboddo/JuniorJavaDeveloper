package com.example.project_work_giornale.controller;

import com.example.project_work_giornale.model.Categoria;
import com.example.project_work_giornale.model.Evento;
import com.example.project_work_giornale.service.CategoriaService;
import com.example.project_work_giornale.service.EventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

// localhost:8080/admineventi
@Controller
@RequestMapping("/admineventi")
public class AdminEventiController {

    @Autowired
    private EventoService eventoService;

    @Autowired
    private CategoriaService categoriaService;

    private Evento evento;

    private Map<String, String> registroErrori;


    @GetMapping
    public String getPage(Model model, @RequestParam(name = "id", required = false) Integer idEvento) {
        List<Evento> eventi = eventoService.elencoEventi();
        List<Categoria> categorie = categoriaService.elencoCategorie();
        Evento eventoDettaglio = idEvento == null ? new Evento() : eventoService.dettaglioEvento(idEvento);
        evento = eventoDettaglio != null ? eventoDettaglio : new Evento(); // Assegna un nuovo Evento se è null
        model.addAttribute("eventi", eventi);
        model.addAttribute("categorie", categorie);
        model.addAttribute("evento", evento);
        model.addAttribute("registroErrori", registroErrori);
        return "admin-eventi";
    }


    @PostMapping
    public String formManager(@RequestParam("nome") String nome,
                              @RequestParam("luogo") String luogo,
                              @RequestParam("dataInizio") LocalDateTime dataInizio,
                              @RequestParam("dataFine") LocalDateTime dataFine,
                              @RequestParam("categoria") int idCategoria,
                              @RequestParam("descrizione") String descrizione,
                              @RequestParam("immagine") MultipartFile immagine) {
        eventoService.aggiungiEvento(evento, nome, luogo, dataInizio, dataFine, idCategoria, descrizione, immagine);
        return "redirect:/admineventi";
    }


    @GetMapping("/elimina")
    public String eliminazioneEvento(@RequestParam("id") int idEvento) {
        eventoService.eliminaEvento(idEvento);
        return "redirect:/admineventi";
    }
}
