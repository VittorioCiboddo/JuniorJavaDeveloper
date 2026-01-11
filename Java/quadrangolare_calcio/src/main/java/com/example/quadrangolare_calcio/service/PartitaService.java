package com.example.quadrangolare_calcio.service;

import com.example.quadrangolare_calcio.model.Partita;
import com.example.quadrangolare_calcio.model.TabellinoPartita;

import java.util.List;
import java.util.Map;

public interface PartitaService {

    // Salva una partita e scatena l'aggiornamento degli archivi
    Partita salvaPartitaCompleta(Partita partita, List<TabellinoPartita> eventi);

    // Recupera tutte le partite di un torneo
    List<Partita> getPartitePerTorneo(int idTorneo);

    // Trova una singola partita con il suo tabellino
    Partita getDettaglioPartita(Long idPartita);

    // Trova le partite dove una squadra ha ribaltato lo svantaggio dei tempi regolamentari
    List<Partita> getRimonteEffettuate(int idTorneo);

    // Estrae la sequenza dei rigori (chi ha segnato/sbagliato) dal tabellino
    Map<String, List<String>> getSequenzaRigori(int idPartita);

    // Calcola il "momento d'oro": la fascia di minuti con più gol nel torneo
    String getFasciaOrariaPiuProlifica(int idTorneo);

    void registraEventoSimulato(Long idPartita, int secondi, long idEvento, long idGiocatore);

    void salvaRisultatoFinale(int idPartita, String resRegular, String resFinale, boolean aiRigori);

}
