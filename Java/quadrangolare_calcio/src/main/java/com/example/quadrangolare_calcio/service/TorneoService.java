package com.example.quadrangolare_calcio.service;

import com.example.quadrangolare_calcio.model.Squadra;
import com.example.quadrangolare_calcio.model.Torneo;

import java.util.List;

public interface TorneoService {

    // Crea un nuovo torneo e registra la partecipazione delle 4 squadre
    Torneo avviaTorneo(String nomeTorneo, List<Squadra> partecipanti);

    // Chiude il torneo, decreta il vincitore e aggiorna l'albo d'oro
    void chiudiTorneo(Long idTorneo, Squadra vincitrice);

    // Recupera lo storico di un torneo
    Torneo getDettaglioTorneo(Long idTorneo);

}
