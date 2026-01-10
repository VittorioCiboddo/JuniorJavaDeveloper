package com.example.quadrangolare_calcio.service;

import com.example.quadrangolare_calcio.model.Giocatore;

public interface ArchivioGiocatoreService {

    void aggiungiGol(Giocatore giocatore);

    void aggiungiRigoreSegnato(Giocatore giocatore, boolean lotteriaFinali);

    void aggiungiRigoreParato(Giocatore giocatore, boolean lotteriaFinali);

}
