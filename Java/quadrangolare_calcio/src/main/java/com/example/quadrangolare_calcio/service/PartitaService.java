package com.example.quadrangolare_calcio.service;

import com.example.quadrangolare_calcio.model.Partita;
import com.example.quadrangolare_calcio.model.TabellinoPartita;

import java.util.List;

public interface PartitaService {

    // Salva una partita e scatena l'aggiornamento degli archivi
    Partita salvaPartitaCompleta(Partita partita, List<TabellinoPartita> eventi);

    // Recupera tutte le partite di un torneo
    List<Partita> getPartitePerTorneo(int idTorneo);

    // Trova una singola partita con il suo tabellino
    Partita getDettaglioPartita(Long idPartita);

}
