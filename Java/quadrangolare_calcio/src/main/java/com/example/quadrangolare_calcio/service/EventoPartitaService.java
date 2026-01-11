package com.example.quadrangolare_calcio.service;

import com.example.quadrangolare_calcio.model.EventoPartita;

import java.util.List;

public interface EventoPartitaService {

    List<EventoPartita> getTuttiEventi();
    EventoPartita getPerId(Long id);

}
