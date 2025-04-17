package com.example.quadrangolare_calcio.service;

import com.example.quadrangolare_calcio.model.Stadio;

import java.util.List;

public interface StadioService {

    void salvaStadio(Stadio stadio);
    Stadio getStadioBySquadraId(Long idSquadra);
    Stadio dettaglioStadio(int idStadio);
    List<Stadio> getAllStadi();
    void eliminaStadio (int id);

}
