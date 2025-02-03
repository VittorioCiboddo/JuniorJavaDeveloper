package com.example.project_work_giornale.service;

import com.example.project_work_giornale.dao.AdminDao;
import com.example.project_work_giornale.model.Admin;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService{

    @Autowired
    private AdminDao adminDao;

    @Override
    public boolean loginAdmin(String username, String password, HttpSession session) {
        Admin admin = adminDao.findByUsernameAndPassword(username, password);
        if(admin != null) {
            session.setAttribute("admin", admin);
            return true;
        }
        return false;
    }
}
