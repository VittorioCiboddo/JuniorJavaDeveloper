package com.example.project_work_giornale.controller;

import com.example.project_work_giornale.model.Evento;
import com.example.project_work_giornale.model.Notizia;
import com.example.project_work_giornale.service.NotiziaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

// localhost:8080/dettaglionotizia
@Controller
@RequestMapping("/dettaglionotizia")
public class DettaglioNotizieController {

    @Autowired
    private NotiziaService notiziaService;

    @GetMapping
    public String getPage(@RequestParam("id") int idNotizia, Model model) {
        Notizia notizia = notiziaService.dettaglioNotizia(idNotizia);
        if (notizia == null) {
            System.out.println("Notizia non trovata con ID: " + idNotizia);
        } else {
            System.out.println("Notizia trovata: " + notizia.getTitolo());
        }
        model.addAttribute("notizia", notizia);
        return "dettaglio-notizia";
    }
}
