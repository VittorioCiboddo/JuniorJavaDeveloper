package com.example.quadrangolare_calcio.service.impl;

import com.example.quadrangolare_calcio.model.Squadra;
import com.example.quadrangolare_calcio.model.Torneo;
import com.example.quadrangolare_calcio.repository.TorneoRepository;
import com.example.quadrangolare_calcio.service.ArchivioSquadraService;
import com.example.quadrangolare_calcio.service.TorneoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TorneoServiceImpl implements TorneoService {

    @Autowired
    private TorneoRepository torneoRepository;

    @Autowired
    private ArchivioSquadraService archivioSquadraService;

    @Override
    @Transactional
    public Torneo avviaTorneo(String nomeTorneo, List<Squadra> partecipanti) {
        // 1. Creiamo e salviamo l'entità Torneo
        Torneo nuovoTorneo = new Torneo();
        nuovoTorneo.setNome(nomeTorneo);
        Torneo torneoSalvato = torneoRepository.save(nuovoTorneo);

        // 2. Registriamo la partecipazione per ognuna delle 4 squadre
        for (Squadra s : partecipanti) {
            // false perché il torneo è appena iniziato, non l'hanno ancora vinto
            archivioSquadraService.aggiornaPartecipazioneTorneo(s, false);
        }

        return torneoSalvato;
    }

    @Override
    @Transactional
    public void chiudiTorneo(Long idTorneo, Squadra vincitrice) {

        // Aggiorniamo l'archivio della squadra vincitrice incrementando i trofei
        archivioSquadraService.aggiornaPartecipazioneTorneo(vincitrice, true);
    }

    @Override
    public Torneo getDettaglioTorneo(Long idTorneo) {
        return torneoRepository.findById(idTorneo).orElse(null);
    }
}
