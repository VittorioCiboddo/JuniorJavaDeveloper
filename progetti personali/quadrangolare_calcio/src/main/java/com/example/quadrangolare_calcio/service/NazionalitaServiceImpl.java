package com.example.quadrangolare_calcio.service;

import com.example.quadrangolare_calcio.dao.NazionalitaDao;
import com.example.quadrangolare_calcio.model.Nazionalita;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NazionalitaServiceImpl implements NazionalitaService{

    @Autowired
    NazionalitaDao nazionalitaDao;

    @Override
    public List<Nazionalita> elencoNazioni() {
        return (List<Nazionalita>) nazionalitaDao.findAll();
    }
}
