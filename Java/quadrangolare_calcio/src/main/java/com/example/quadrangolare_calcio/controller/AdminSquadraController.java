package com.example.quadrangolare_calcio.controller;

import com.example.quadrangolare_calcio.service.AllenatoreService;
import com.example.quadrangolare_calcio.service.NazionalitaService;
import com.example.quadrangolare_calcio.service.SquadraService;
import com.example.quadrangolare_calcio.service.StadioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/areaadmin")
public class AdminSquadraController {

    @Autowired
    private SquadraService squadraService;

    @Autowired
    private AllenatoreService allenatoreService;

    @Autowired
    private StadioService stadioService;

    @Autowired
    private NazionalitaService nazionalitaService;

    @GetMapping("/adminsquadre")
    public String gestisciSquadre(Model model, HttpSession session) {
        
    }
}
