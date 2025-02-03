package com.example.project_work_giornale.controller;

import com.example.project_work_giornale.model.Evento;
import com.example.project_work_giornale.service.EventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

// localhost:8080/eventi
@Controller
@RequestMapping("/eventi")
public class EventiController {

    @Autowired
    private EventoService eventoService;

    @GetMapping
    public String getPage(Model model) {
        List<Evento> evento = eventoService.elencoEventi();
        model.addAttribute("evento", evento);
        return "eventi";
    }
}
