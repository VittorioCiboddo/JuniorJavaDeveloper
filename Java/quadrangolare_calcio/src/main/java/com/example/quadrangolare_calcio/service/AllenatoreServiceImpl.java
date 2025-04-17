package com.example.quadrangolare_calcio.service;

import com.example.quadrangolare_calcio.dao.AllenatoreDao;
import com.example.quadrangolare_calcio.model.Allenatore;
import com.example.quadrangolare_calcio.model.Stadio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AllenatoreServiceImpl implements AllenatoreService {

    @Autowired
    private AllenatoreDao allenatoreDao;

    @Override
    public void salvaAllenatore(Allenatore allenatore) {
        allenatoreDao.save(allenatore);

    }

    @Override
    public Allenatore getAllenatoreBySquadraId(Long idSquadra) {
        return allenatoreDao.findBySquadraId(idSquadra);
    }

    @Override
    public Allenatore dettaglioAllenatore(Long idAllenatore) {
        Optional<Allenatore> allenatoreOptional = allenatoreDao.findById(idAllenatore);
        if(allenatoreOptional.isPresent())
            return allenatoreOptional.get();
        return null;
    }

    @Override
    public List<Allenatore> getAllAllenatori() {
        return (List<Allenatore>) allenatoreDao.findAll();
    }

}
