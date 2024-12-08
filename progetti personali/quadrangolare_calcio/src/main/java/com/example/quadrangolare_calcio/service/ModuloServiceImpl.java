package com.example.quadrangolare_calcio.service;

import com.example.quadrangolare_calcio.dao.ModuloDao;
import com.example.quadrangolare_calcio.model.Modulo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModuloServiceImpl implements ModuloService {

    @Autowired
    ModuloDao moduloDao;

    @Override
    public List<Modulo> elencoModuli() {
        return (List<Modulo>) moduloDao.findAll();
    }
}
