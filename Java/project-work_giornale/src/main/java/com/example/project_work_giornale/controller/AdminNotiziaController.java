package com.example.project_work_giornale.controller;

import com.example.project_work_giornale.model.Categoria;
import com.example.project_work_giornale.model.Evento;
import com.example.project_work_giornale.model.Notizia;
import com.example.project_work_giornale.service.CategoriaService;
import com.example.project_work_giornale.service.NotiziaService;
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

// localhost:8080/adminnotizie
@Controller
@RequestMapping("/adminnotizie")
public class AdminNotiziaController {

    @Autowired
    private NotiziaService notiziaService;

    @Autowired
    private CategoriaService categoriaService;

    private Notizia notizia;

    private Map<String, String> registroErrori;


    @GetMapping
    public String getPage(Model model, @RequestParam(name = "id", required = false) Integer idNotizia) {
        List<Notizia> notizie = notiziaService.elencoNotizia();
        List<Categoria> categorie = categoriaService.elencoCategorie();
        Notizia notiziaDettaglio = idNotizia == null ? new Notizia() : notiziaService.dettaglioNotizia(idNotizia);
        notizia = notiziaDettaglio != null ? notiziaDettaglio : new Notizia(); // Assegna un nuovo Notizia se è null
        model.addAttribute("notizie", notizie);
        model.addAttribute("categorie", categorie);
        model.addAttribute("notizia", notizia);
        model.addAttribute("registroErrori", registroErrori);
        return "admin-notizie";
    }


    @PostMapping
    public String formManager(@RequestParam("titolo") String titolo,
                              @RequestParam("descrizione") String descrizione,
                              @RequestParam("autore") String autore,
                              @RequestParam("dataPubblicazione") LocalDate dataPubblicazione,
                              @RequestParam("immagine") MultipartFile immagine,
                              @RequestParam("linkVideo") String linkVideo,
                              @RequestParam("categoria") int idCategoria) {
        notiziaService.aggiungiNotizia(notizia, titolo, descrizione, autore, dataPubblicazione, immagine, linkVideo, idCategoria);
        return "redirect:/adminnotizie";
    }


    @GetMapping("/elimina")
    public String eliminazioneNotizia(@RequestParam("id") int idNotizia) {
        notiziaService.eliminaNotizia(idNotizia);
        return "redirect:/adminnotizie";
    }
}
