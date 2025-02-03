package com.example.project_work_giornale.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

// localhost:8080/tutorial
@Controller
@RequestMapping("/tutorial")
public class TutorialController {

    @GetMapping
    public String getPage() {
        return "tutorial";
    }
}
