package com.example.quadrangolare_calcio.service.impl;

import com.example.quadrangolare_calcio.dao.AdminDao;
import com.example.quadrangolare_calcio.model.Admin;
import com.example.quadrangolare_calcio.service.AdminService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {

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
