package com.example.quadrangolare_calcio.service.impl;

import com.example.quadrangolare_calcio.model.ArchivioGiocatore;
import com.example.quadrangolare_calcio.model.Giocatore;
import com.example.quadrangolare_calcio.model.TabellinoPartita;
import com.example.quadrangolare_calcio.repository.ArchivioGiocatoreRepository;
import com.example.quadrangolare_calcio.repository.TabellinoPartitaRepository;
import com.example.quadrangolare_calcio.service.ArchivioGiocatoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ArchivioGiocatoreServiceImpl implements ArchivioGiocatoreService {

    @Autowired
    private ArchivioGiocatoreRepository archivioGiocatoreRepository;

    @Autowired
    private TabellinoPartitaRepository tabellinoPartitaRepository;


    @Override
    @Transactional
    public ArchivioGiocatore getOrCreateArchivio(Giocatore giocatore) {
        return archivioGiocatoreRepository
                .findByGiocatoreIdGiocatore(giocatore.getIdGiocatore())
                .orElseGet(() -> {
                    ArchivioGiocatore nuovo = new ArchivioGiocatore();
                    nuovo.setGiocatore(giocatore);

                    nuovo.setGolTotali(0);
                    nuovo.setRigoriSegnati(0);
                    nuovo.setRigoriRegolariSegnati(0);
                    nuovo.setRigoriParati(0);
                    nuovo.setRigoriRegolariParati(0);

                    return archivioGiocatoreRepository.save(nuovo);
                });
    }


    // In ArchivioGiocatoreServiceImpl.java
    @Override
    public void aggiornaGol(Giocatore giocatore) {
        // Se non esiste la riga per questo giocatore, la creiamo!
        ArchivioGiocatore arc = getOrCreateArchivio(giocatore);

        arc.setGolTotali(arc.getGolTotali() + 1);
        archivioGiocatoreRepository.save(arc);
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

    @Override
    public List<Map<String, Object>> getClassificaMarcatori(int idTorneo) {
        // Recuperiamo tutti i tabellini del torneo specifico per l'evento "Goal" (ID 4 o 5)
        List<TabellinoPartita> golDelTorneo = tabellinoPartitaRepository.findByPartitaTorneoIdTorneo(idTorneo);

        // Raggruppiamo per giocatore e contiamo i gol
        return golDelTorneo.stream()
                .filter(t -> t.getEventoPartita().getIdEventoPartita() == 4 || t.getEventoPartita().getIdEventoPartita() == 5)
                .collect(Collectors.groupingBy(t -> t.getGiocatore().getNome() + " " + t.getGiocatore().getCognome(), Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .map(e -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("giocatore", e.getKey());
                    m.put("gol", e.getValue());
                    return m;
                })
                .collect(Collectors.toList());
    }

    @Override
    public int getCleanSheets(Giocatore portiere) {
        // Recuperiamo tutte le partite della squadra del portiere
        // Controlliamo in quante di queste il risultato subìto (home o away) è stato 0
        // Questa è una logica che richiede l'accesso a PartitaRepository
        return 0; // Implementazione dipendente dalla squadra del giocatore
    }

    @Override
    public double getMediaGol(Giocatore giocatore, int partiteGiocate) {
        ArchivioGiocatore archivio = getOrCreateArchivio(giocatore);
        if (partiteGiocate == 0) return 0.0;
        return (double) archivio.getGolTotali() / partiteGiocate;
    }

    @Override
    public Map<String, Object> getSpecialistaRigori(int idTorneo) {
        List<TabellinoPartita> rigori = tabellinoPartitaRepository.findByPartitaTorneoIdTorneo(idTorneo);

        return rigori.stream()
                .filter(t -> t.getEventoPartita().getIdEventoPartita() == 5) // Rigore Segnato
                .collect(Collectors.groupingBy(t -> t.getGiocatore().getNome() + " " + t.getGiocatore().getCognome(), Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(e -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("giocatore", e.getKey());
                    m.put("rigoriSegnati", e.getValue());
                    return m;
                }).orElse(null);
    }


}
