package com.example.quadrangolare_calcio.service;

import com.example.quadrangolare_calcio.dao.TipologiaDao;
import com.example.quadrangolare_calcio.model.Tipologia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TipologiaServiceImpl implements TipologiaService{

    @Autowired
    private TipologiaDao tipologiaDao;

    @Override
    public List<Tipologia> elencoTipologie() {
        return (List<Tipologia>) tipologiaDao.findAll();
    }
}
