package com.example.project_work_giornale.service;

import jakarta.servlet.http.HttpSession;

public interface AdminService {
    boolean loginAdmin(String username, String password, HttpSession session);
}
