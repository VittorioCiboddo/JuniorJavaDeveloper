package com.example.quadrangolare_calcio.service;

import com.example.quadrangolare_calcio.model.Squadra;

public interface ArchivioSquadraService {

    void aggiornaStatistichePartita(Squadra squadra, int golFatti, int golSubiti,
                                    boolean vinta, boolean vintaAiRigori, boolean persaAiRigori);

    void aggiornaPartecipazioneTorneo(Squadra squadra, boolean haVintoTorneo);

}
