package com.example.project_work_giornale.controller;

import com.example.project_work_giornale.model.Evento;
import com.example.project_work_giornale.service.EventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

// localhost:8080/dettaglioevento
@Controller
@RequestMapping("/dettaglioevento")
public class DettaglioEventiController {

    @Autowired
    private EventoService eventoService;

    @GetMapping
    public String getPage(@RequestParam("id") int idEvento, Model model) {
        Evento evento = eventoService.dettaglioEvento(idEvento);
        if (evento == null) {
            System.out.println("Evento non trovato con ID: " + idEvento);
        } else {
            System.out.println("Evento trovato: " + evento.getNome());
        }
        model.addAttribute("evento", evento);
        return "dettaglio-evento";
    }

}
