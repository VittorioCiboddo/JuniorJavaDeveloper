package com.example.quadrangolare_calcio.service.impl;

import com.example.quadrangolare_calcio.model.TipoPartita;
import com.example.quadrangolare_calcio.repository.TipoPartitaRepository;
import com.example.quadrangolare_calcio.service.TipoPartitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TipoPartitaServiceImpl implements TipoPartitaService {

    @Autowired
    private TipoPartitaRepository tipoPartitaRepository;

    @Override
    public List<TipoPartita> getTuttiTipi() {
        return tipoPartitaRepository.findAll();
    }

    @Override
    public TipoPartita getPerNome(String nome) {

        return tipoPartitaRepository.findByTipo(nome);
    }

    @Override
    public TipoPartita getPerId(Long id) {
        return tipoPartitaRepository.findById(id).orElse(null);
    }
}
