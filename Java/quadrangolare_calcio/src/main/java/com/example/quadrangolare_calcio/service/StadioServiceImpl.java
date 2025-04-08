package com.example.quadrangolare_calcio.service;

import com.example.quadrangolare_calcio.dao.StadioDao;
import com.example.quadrangolare_calcio.model.Stadio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StadioServiceImpl implements StadioService {

    @Autowired
    private StadioDao stadioDao;

    @Override
    public void salvaStadio(Stadio stadio) {
        stadioDao.save(stadio);

    }
}
