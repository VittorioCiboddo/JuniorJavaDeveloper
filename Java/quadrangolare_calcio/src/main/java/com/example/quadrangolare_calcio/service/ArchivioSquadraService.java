package com.example.quadrangolare_calcio.service;

import com.example.quadrangolare_calcio.model.ArchivioSquadra;
import com.example.quadrangolare_calcio.model.Squadra;

import java.util.Map;

public interface ArchivioSquadraService {

    void aggiornaStatistichePartita(Squadra squadra, int golFatti, int golSubiti,
                                    boolean vinta, boolean vintaAiRigori, boolean persaAiRigori);

    void aggiornaPartecipazioneTorneo(Squadra squadra, boolean haVintoTorneo);

    // Calcola la percentuale di successo (vittorie su tornei giocati)
    double getPercentualeVittorie(Squadra squadra);

    // Identifica la miglior difesa in un torneo specifico (meno gol subiti)
    Map<String, Object> getMigliorDifesaTorneo(int idTorneo);

    // Calcola la media gol fatti a partita nel tempo
    double getMediaGolFatti(Squadra squadra);

    void registraPiazzamentoTorneo(Squadra squadra, int posizione);

    ArchivioSquadra getOrCreateArchivio(Squadra squadra);

    ArchivioSquadra getOrCreateArchivioByNome(String nomeSquadra);

    void incrementaPartecipazione(Squadra squadra);

}
