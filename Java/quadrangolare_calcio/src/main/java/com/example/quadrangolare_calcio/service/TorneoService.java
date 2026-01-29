package com.example.quadrangolare_calcio.service;

import com.example.quadrangolare_calcio.dto.ClassificaSquadraDTO;
import com.example.quadrangolare_calcio.dto.MiniMatchDTO;
import com.example.quadrangolare_calcio.dto.TorneoSalvataggioDTO;
import com.example.quadrangolare_calcio.model.Squadra;
import com.example.quadrangolare_calcio.model.Torneo;

import java.util.List;
import java.util.Map;

public interface TorneoService {

    void salvaTorneoIntero(TorneoSalvataggioDTO dto);

    // Nuovo torneo e registrazione della partecipazione delle 4 squadre
    Torneo avviaTorneo(String nomeTorneo, List<Squadra> partecipanti);

    // Chiudo il torneo, decreta il vincitore e aggiorna l'albo d'oro
    void chiudiTorneo(Long idTorneo);

    // Recupera lo storico di un torneo
    Torneo getDettaglioTorneo(Long idTorneo);

    // Restituisce il Ranking delle squadre basato sui tornei vinti nell'archivio
    List<Map<String, Object>> getRankingStorico();

    // Estrae l'Albo d'Oro: lista di tutti i tornei con il relativo vincitore
    List<Map<String, String>> getAlboDOro();

    // Restituisce le statistiche generali di un torneo (gol totali, media gol/partita)
    Map<String, Object> getStatsTorneo(int idTorneo);

    // Restituisce la classifica completa (1°, 2°, 3°, 4°) di un torneo
    Map<Integer, String> getClassificaTorneo(int idTorneo);

    // Restituisce solo le prime 3 squadre (Podio)
    List<String> getPodio(int idTorneo);

    // Restituisce il medagliere storico (Squadra -> Numero di Podi: 1°, 2°, 3°)
    List<Map<String, Object>> getMedagliereStorico();

    // Restituisce la "Squadra Record" (chi ha più vittorie, più gol, ecc.)
    Map<String, Object> getHallOfFame();

    Map<String, MiniMatchDTO> getMiniTabellone(int idTorneo);

    List<ClassificaSquadraDTO> getClassificaTorneoDettagliata(int idTorneo);

}
