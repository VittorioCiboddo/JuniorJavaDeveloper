package com.example.quadrangolare_calcio.service;

import com.example.quadrangolare_calcio.model.ArchivioGiocatore;
import com.example.quadrangolare_calcio.model.Giocatore;

import java.util.List;
import java.util.Map;

public interface ArchivioGiocatoreService {

    void aggiungiGol(Giocatore giocatore);

    void aggiungiRigoreSegnato(Giocatore giocatore, boolean lotteriaFinali);

    void aggiungiRigoreParato(Giocatore giocatore, boolean lotteriaFinali);

    // Restituisce i giocatori con più gol in un torneo (filtrando dal tabellino)
    List<Map<String, Object>> getClassificaMarcatori(int idTorneo);

    // Calcola i Clean Sheets (partite a porta inviolata) per un portiere
    int getCleanSheets(Giocatore portiere);

    // Calcola la media gol basata sull'archivio storico
    double getMediaGol(Giocatore giocatore, int partiteGiocate);

    // Trova il giocatore con più rigori segnati in un torneo
    Map<String, Object> getSpecialistaRigori(int idTorneo);

    ArchivioGiocatore getOrCreateArchivio(Giocatore giocatore);

    void aggiornaGol(Giocatore giocatore);

}
