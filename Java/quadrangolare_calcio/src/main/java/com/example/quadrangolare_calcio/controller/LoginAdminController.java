package com.example.quadrangolare_calcio.controller;

import com.example.quadrangolare_calcio.service.AdminService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

// localhost:8080/loginadmin
@Controller
@RequestMapping("/loginadmin")
public class LoginAdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping
    public String getPage(@RequestParam(name = "error", required = false) String error, Model model, HttpSession session) {
        if (session.getAttribute("utente") != null)
            return "redirect:/areaadmin";
        model.addAttribute("error", error != null);
        return "login-admin";
    }


    @PostMapping
    public String formManager(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpSession session) {
        if(!adminService.loginAdmin(username, password, session))
            return "redirect:/loginadmin?error";
        return "redirect:/areaadmin";
    }
}
