package com.example.quadrangolare_calcio.controller;

import com.example.quadrangolare_calcio.model.Admin;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

// localhost:8080/areaadmin
@Controller
@RequestMapping("/areaadmin")
public class AreaAdminController {

    @GetMapping
    public String getPage(HttpSession session, Model model) {
        if (session.getAttribute("admin") == null)
            return "redirect:/loginadmin";
        Admin admin = (Admin) session.getAttribute("admin");
        model.addAttribute("admin", admin);
        return "area-admin";
    }

    // localhost:8080/areaadmin/logout
    @GetMapping("/logout")
    public String logoutAdmin(HttpSession session) {
        session.removeAttribute("admin");
        return "redirect:/loginadmin";
    }


}