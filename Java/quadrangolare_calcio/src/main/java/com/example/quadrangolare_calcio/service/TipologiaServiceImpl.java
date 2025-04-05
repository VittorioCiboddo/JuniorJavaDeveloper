package com.example.quadrangolare_calcio.service;

import com.example.quadrangolare_calcio.dao.TipologiaDao;
import com.example.quadrangolare_calcio.model.Tipologia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.quadrangolare_calcio.model.Ruolo;


import java.util.List;

@Service
public class TipologiaServiceImpl implements TipologiaService{

    @Autowired
    private TipologiaDao tipologiaDao;

    @Autowired
    private RuoloService ruoloService;

    @Override
    public List<Tipologia> elencoTipologie() {
        return (List<Tipologia>) tipologiaDao.findAll();
    }

    @Override
    public List<Tipologia> getTipologiePerModulo(Long idModulo) {
        List<Ruolo> ruoli = ruoloService.getRuoliPerModulo(idModulo);

        return ruoli.stream()
                .map(Ruolo::getTipologia)
                .distinct()
                .toList();
    }

}
