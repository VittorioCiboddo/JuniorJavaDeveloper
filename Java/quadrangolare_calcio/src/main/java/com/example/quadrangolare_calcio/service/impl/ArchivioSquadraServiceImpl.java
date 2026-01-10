package com.example.quadrangolare_calcio.service.impl;

import com.example.quadrangolare_calcio.model.ArchivioSquadra;
import com.example.quadrangolare_calcio.model.Squadra;
import com.example.quadrangolare_calcio.repository.ArchivioSquadraRepository;
import com.example.quadrangolare_calcio.service.ArchivioSquadraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ArchivioSquadraServiceImpl implements ArchivioSquadraService {

    @Autowired
    private ArchivioSquadraRepository archivioSquadraRepository;

    @Override
    @Transactional
    public void aggiornaStatistichePartita(Squadra squadra, int golFatti, int golSubiti,
                                           boolean vinta, boolean vintaAiRigori, boolean persaAiRigori) {

        // Cerchiamo l'archivio usando la query derivata (ricordati di aggiungerla alla repo!)
        ArchivioSquadra archivio = archivioSquadraRepository.findBySquadraIdSquadra(squadra.getIdSquadra())
                .orElseGet(() -> {
                    ArchivioSquadra nuovo = new ArchivioSquadra();
                    nuovo.setSquadra(squadra);
                    return nuovo;
                });

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
}

