package com.example.quadrangolare_calcio.service;

import jakarta.servlet.http.HttpSession;

public interface AdminService {
    boolean loginAdmin(String username, String password, HttpSession session);
}
