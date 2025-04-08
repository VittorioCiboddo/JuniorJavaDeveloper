package com.example.quadrangolare_calcio.service;

import com.example.quadrangolare_calcio.dao.AllenatoreDao;
import com.example.quadrangolare_calcio.model.Allenatore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AllenatoreServiceImpl implements AllenatoreService {

    @Autowired
    private AllenatoreDao allenatoreDao;


    @Override
    public void salvaAllenatore(Allenatore allenatore) {
        allenatoreDao.save(allenatore);

    }
}
