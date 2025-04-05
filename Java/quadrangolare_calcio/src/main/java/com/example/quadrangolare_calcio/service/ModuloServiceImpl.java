package com.example.quadrangolare_calcio.service;

import com.example.quadrangolare_calcio.dao.ModuloDao;
import com.example.quadrangolare_calcio.model.Modulo;
import com.example.quadrangolare_calcio.model.Squadra;
import com.example.quadrangolare_calcio.repository.ModuloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModuloServiceImpl implements ModuloService{


    @Autowired
    private ModuloDao moduloDao;

    @Autowired
    private ModuloRepository moduloRepository;

    @Override
    public List<Modulo> elencoModuli() {
        return (List<Modulo>) moduloDao.findAll();
    }

    // Ora questa funzione non dipende più da SquadraService
    public Modulo getModuloPerSquadra(Squadra squadra) {
        return squadra.getModulo(); // Ottieni il modulo direttamente dalla squadra
    }

    @Override
    public Modulo getModuloById(Long idModulo) {
        return moduloRepository.findById(idModulo).orElse(null);
    }

}
