package com.example.quadrangolare_calcio.service;

import com.example.quadrangolare_calcio.model.Allenatore;

import java.util.List;

public interface AllenatoreService {

    void salvaAllenatore(Allenatore allenatore);
    Allenatore getAllenatoreBySquadraId(Long idSquadra);
    Allenatore dettaglioAllenatore(Long idAllenatore);
    List<Allenatore> getAllAllenatori();


    void eliminaAllenatore(int id);
}
