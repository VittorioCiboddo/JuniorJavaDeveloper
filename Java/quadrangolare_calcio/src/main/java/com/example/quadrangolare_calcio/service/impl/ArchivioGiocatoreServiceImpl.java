package com.example.quadrangolare_calcio.service.impl;

import com.example.quadrangolare_calcio.model.ArchivioGiocatore;
import com.example.quadrangolare_calcio.model.Giocatore;
import com.example.quadrangolare_calcio.repository.ArchivioGiocatoreRepository;
import com.example.quadrangolare_calcio.service.ArchivioGiocatoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ArchivioGiocatoreServiceImpl implements ArchivioGiocatoreService {

    @Autowired
    private ArchivioGiocatoreRepository archivioGiocatoreRepository;

    // Metodo privato di utilità per recuperare o creare l'archivio
    private ArchivioGiocatore getOrCreateArchivio(Giocatore giocatore) {
        return archivioGiocatoreRepository.findByGiocatoreIdGiocatore(giocatore.getIdGiocatore())
                .orElseGet(() -> {
                    ArchivioGiocatore nuovo = new ArchivioGiocatore();
                    nuovo.setGiocatore(giocatore);
                    return nuovo;
                });
    }

    @Override
    @Transactional
    public void aggiungiGol(Giocatore giocatore) {
        ArchivioGiocatore archivio = getOrCreateArchivio(giocatore);
        archivio.setGolTotali(archivio.getGolTotali() + 1);
        archivioGiocatoreRepository.save(archivio);
    }

    @Override
    @Transactional
    public void aggiungiRigoreSegnato(Giocatore giocatore, boolean lotteriaFinali) {
        ArchivioGiocatore archivio = getOrCreateArchivio(giocatore);

        // Un rigore segnato incrementa sempre il totale rigori
        archivio.setRigoriSegnati(archivio.getRigoriSegnati() + 1);

        // Se è avvenuto durante i 90 min, incrementa anche il contatore specifico
        if (!lotteriaFinali) {
            archivio.setRigoriRegolariSegnati(archivio.getRigoriRegolariSegnati() + 1);
            // Opzionale: i rigori nei 90 min spesso contano anche come gol totali
            archivio.setGolTotali(archivio.getGolTotali() + 1);
        }

        archivioGiocatoreRepository.save(archivio);
    }

    @Override
    @Transactional
    public void aggiungiRigoreParato(Giocatore giocatore, boolean lotteriaFinali) {
        ArchivioGiocatore archivio = getOrCreateArchivio(giocatore);

        archivio.setRigoriParati(archivio.getRigoriParati() + 1);

        if (!lotteriaFinali) {
            archivio.setRigoriRegolariParati(archivio.getRigoriRegolariParati() + 1);
        }

        archivioGiocatoreRepository.save(archivio);
    }
}
