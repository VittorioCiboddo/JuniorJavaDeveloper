package com.example.quadrangolare_calcio.service;

import com.example.quadrangolare_calcio.dao.RuoloDao;
import com.example.quadrangolare_calcio.model.Ruolo;
import com.example.quadrangolare_calcio.repository.RuoloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RuoloServiceImpl implements RuoloService{

    @Autowired
    private RuoloRepository ruoloRepository;

    @Autowired
    private RuoloDao ruoloDao;

    @Override
    public List<Ruolo> elencoRuoli() {
        return (List<Ruolo>) ruoloDao.findAll();
    }

    @Override
    public List<Ruolo> getRuoliPerModulo(Long idModulo) {
        return ruoloRepository.findByModulo_IdModulo(idModulo); // Trova ruoli per modulo
    }

    @Override
    public Ruolo getById(Long id) {
        return ruoloDao.findById(Math.toIntExact(id)).orElse(null);
    }



}
