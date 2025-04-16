package com.example.quadrangolare_calcio.controller;

import com.example.quadrangolare_calcio.service.AllenatoreService;
import com.example.quadrangolare_calcio.service.NazionalitaService;
import com.example.quadrangolare_calcio.service.SquadraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/areaadmin")
public class AdminAllenatoreController {

    @Autowired
    private AllenatoreService allenatoreService;

    @Autowired
    private SquadraService squadraService;

    @Autowired
    private NazionalitaService nazionalitaService;

    @GetMapping("/adminallenatori")
    public String gestisciAllenatori(@RequestParam) {
        
    }
}
