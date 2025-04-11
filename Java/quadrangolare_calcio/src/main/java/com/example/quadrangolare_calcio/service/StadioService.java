package com.example.quadrangolare_calcio.service;

import com.example.quadrangolare_calcio.model.Stadio;

public interface StadioService {

    void salvaStadio(Stadio stadio);
    Stadio getStadioBySquadraId(Long idSquadra);
    Stadio dettaglioStadio(int idStadio);

}
