package com.example.quadrangolare_calcio.service.impl;

import com.example.quadrangolare_calcio.dao.TipologiaDao;
import com.example.quadrangolare_calcio.model.Tipologia;
import com.example.quadrangolare_calcio.repository.TipologiaRepository;
import com.example.quadrangolare_calcio.service.RuoloService;
import com.example.quadrangolare_calcio.service.TipologiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.quadrangolare_calcio.model.Ruolo;


import java.util.List;

@Service
public class TipologiaServiceImpl implements TipologiaService {

    @Autowired
    private TipologiaDao tipologiaDao;

    @Autowired
    private RuoloService ruoloService;

    @Autowired
    private TipologiaRepository tipologiaRepository;

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

    @Override
    public Tipologia getById(Long id) {
        return tipologiaRepository.findById(id).orElse(null);
    }


}
