package com.example.quadrangolare_calcio.service.impl;

import com.example.quadrangolare_calcio.model.EventoPartita;
import com.example.quadrangolare_calcio.repository.EventoPartitaRepository;
import com.example.quadrangolare_calcio.service.EventoPartitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventoPartitaServiceImpl implements EventoPartitaService {

    @Autowired
    private EventoPartitaRepository eventoPartitaRepository;

    @Override
    public List<EventoPartita> getTuttiEventi() {
        return eventoPartitaRepository.findAll();
    }

    @Override
    public EventoPartita getPerId(Long id) {
        return eventoPartitaRepository.findById(id).orElse(null);
    }
}
