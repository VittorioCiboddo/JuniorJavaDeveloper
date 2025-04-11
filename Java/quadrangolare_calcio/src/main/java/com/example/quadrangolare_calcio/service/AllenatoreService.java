package com.example.quadrangolare_calcio.service;

import com.example.quadrangolare_calcio.model.Allenatore;

public interface AllenatoreService {

    void salvaAllenatore(Allenatore allenatore);
    Allenatore getAllenatoreBySquadraId(Long idSquadra);
    Allenatore dettaglioAllenatore(Long idAllenatore);

}
