package com.example.quadrangolare_calcio.service.impl;

import com.example.quadrangolare_calcio.model.ArchivioSquadra;
import com.example.quadrangolare_calcio.model.Partita;
import com.example.quadrangolare_calcio.model.Squadra;
import com.example.quadrangolare_calcio.repository.ArchivioSquadraRepository;
import com.example.quadrangolare_calcio.repository.PartitaRepository;
import com.example.quadrangolare_calcio.service.ArchivioSquadraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ArchivioSquadraServiceImpl implements ArchivioSquadraService {

    @Autowired
    private ArchivioSquadraRepository archivioSquadraRepository;

    @Autowired
    private PartitaRepository partitaRepository;

    @Override
    @Transactional
    public void aggiornaStatistichePartita(Squadra squadra, int golFatti, int golSubiti,
                                           boolean vinta, boolean vintaAiRigori, boolean persaAiRigori) {

        // Cerchiamo l'archivio usando la query derivata
        ArchivioSquadra archivio = getOrCreateArchivio(squadra);

        // Sommiamo i dati correnti
        archivio.setGolFattiTotali(archivio.getGolFattiTotali() + golFatti);
        archivio.setGolSubitiTotali(archivio.getGolSubitiTotali() + golSubiti);

        if (vinta) {
            if (vintaAiRigori) {
                archivio.setVittorieRigori(archivio.getVittorieRigori() + 1);
            } else {
                archivio.setVittorieRegolari(archivio.getVittorieRegolari() + 1);
            }
        } else {
            if (persaAiRigori) {
                archivio.setSconfitteRigori(archivio.getSconfitteRigori() + 1);
            } else {
                archivio.setSconfitteRegolari(archivio.getSconfitteRegolari() + 1);
            }
        }

        archivioSquadraRepository.save(archivio);
    }

    @Override
    @Transactional
    public void aggiornaPartecipazioneTorneo(Squadra squadra, boolean haVintoTorneo) {
        ArchivioSquadra archivio = archivioSquadraRepository.findBySquadraIdSquadra(squadra.getIdSquadra())
                .orElseGet(() -> {
                    ArchivioSquadra nuovo = new ArchivioSquadra();
                    nuovo.setSquadra(squadra);
                    return nuovo;
                });

        archivio.setTorneiPartecipati(archivio.getTorneiPartecipati() + 1);
        if (haVintoTorneo) {
            archivio.setTorneiVinti(archivio.getTorneiVinti() + 1);
        }

        archivioSquadraRepository.save(archivio);
    }

    @Override
    public double getPercentualeVittorie(Squadra squadra) {
        ArchivioSquadra archivio = archivioSquadraRepository.findBySquadraIdSquadra(squadra.getIdSquadra())
                .orElse(null);

        if (archivio == null || archivio.getTorneiPartecipati() == 0) return 0.0;

        // Calcoliamo il totale delle vittorie (regolari + rigori) rispetto ai tornei
        int totaliVittorie = archivio.getVittorieRegolari() + archivio.getVittorieRigori();
        return (double) totaliVittorie / archivio.getTorneiPartecipati();
    }

    @Override
    public Map<String, Object> getMigliorDifesaTorneo(int idTorneo) {
        // Recuperiamo tutte le partite del torneo
        List<Partita> partite = partitaRepository.findByTorneoIdTorneo(idTorneo);

        Map<String, Integer> golSubitiPerSquadra = new HashMap<>();

        for (Partita p : partite) {
            // Analizziamo il risultato regolare per contare i gol subiti "veri"
            String[] punteggio = p.getRisultatoRegular().split("-");
            int golHome = Integer.parseInt(punteggio[0]);
            int golAway = Integer.parseInt(punteggio[1]);

            // Sommiamo i gol subiti dalla squadra in casa (quelli fatti dall'away)
            golSubitiPerSquadra.merge(p.getSquadraHome().getNome(), golAway, Integer::sum);
            // Sommiamo i gol subiti dalla squadra in trasferta (quelli fatti dall'home)
            golSubitiPerSquadra.merge(p.getSquadraAway().getNome(), golHome, Integer::sum);
        }

        // Troviamo la squadra con il valore minimo
        return golSubitiPerSquadra.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .map(e -> {
                    Map<String, Object> res = new HashMap<>();
                    res.put("squadra", e.getKey());
                    res.put("golSubiti", e.getValue());
                    return res;
                }).orElse(null);
    }

    @Override
    public double getMediaGolFatti(Squadra squadra) {
        ArchivioSquadra archivio = archivioSquadraRepository.findBySquadraIdSquadra(squadra.getIdSquadra())
                .orElse(null);

        if (archivio == null || archivio.getGolFattiTotali() == 0) return 0.0;

        // Per una media accurata servirebbe il numero totale di partite giocate
        // Se non lo abbiamo salvato nell'archivio, possiamo stimarlo o aggiungerlo
        return (double) archivio.getGolFattiTotali() / (archivio.getVittorieRegolari() + archivio.getSconfitteRegolari());
    }

    @Transactional
    public void registraPiazzamentoTorneo(Squadra squadra, int posizione) {
        ArchivioSquadra archivio = getOrCreateArchivio(squadra);

        switch (posizione) {
            case 1 -> archivio.setTorneiVinti(archivio.getTorneiVinti() + 1);
            case 2 -> archivio.setSecondiPosti(archivio.getSecondiPosti() + 1);
            case 3 -> archivio.setTerziPosti(archivio.getTerziPosti() + 1);
            case 4 -> archivio.setQuartiPosti(archivio.getQuartiPosti() + 1);
        }

        archivioSquadraRepository.save(archivio);
    }

    @Override
    @Transactional
    public ArchivioSquadra getOrCreateArchivio(Squadra squadra) {
        // Cerchiamo l'archivio esistente usando l'ID della squadra
        return archivioSquadraRepository.findBySquadraIdSquadra(squadra.getIdSquadra())
                .orElseGet(() -> {
                    // Se non esiste, creiamo un nuovo oggetto ArchivioSquadra
                    ArchivioSquadra nuovoArchivio = new ArchivioSquadra();
                    nuovoArchivio.setSquadra(squadra);

                    // Inizializziamo tutti i contatori a 0 per sicurezza
                    nuovoArchivio.setTorneiPartecipati(0);
                    nuovoArchivio.setTorneiVinti(0);
                    nuovoArchivio.setSecondiPosti(0);
                    nuovoArchivio.setTerziPosti(0);
                    nuovoArchivio.setQuartiPosti(0);
                    nuovoArchivio.setVittorieRegolari(0);
                    nuovoArchivio.setVittorieRigori(0);
                    nuovoArchivio.setSconfitteRegolari(0);
                    nuovoArchivio.setSconfitteRigori(0);
                    nuovoArchivio.setGolFattiTotali(0);
                    nuovoArchivio.setGolSubitiTotali(0);

                    // Salviamo subito il nuovo record
                    return archivioSquadraRepository.save(nuovoArchivio);
                });
    }

    @Override
    @Transactional
    public void incrementaPartecipazione(Squadra squadra) {
        // USA IL TUO METODO QUI:
        ArchivioSquadra archivio = getOrCreateArchivio(squadra);

        archivio.setTorneiPartecipati(archivio.getTorneiPartecipati() + 1);
        archivioSquadraRepository.save(archivio);
    }


}

