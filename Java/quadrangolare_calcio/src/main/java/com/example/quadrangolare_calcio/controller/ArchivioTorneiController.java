package com.example.quadrangolare_calcio.controller;

import com.example.quadrangolare_calcio.repository.TorneoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/archivio/tornei")
public class ArchivioTorneiController {

    @Autowired
    private TorneoRepository torneoRepository;

    @GetMapping
    public String getPage(Model model) {

        boolean esistonoTornei = torneoRepository.existsBy();
        model.addAttribute("esistonoTornei", esistonoTornei);

        return "archivio-tornei";
    }
}
