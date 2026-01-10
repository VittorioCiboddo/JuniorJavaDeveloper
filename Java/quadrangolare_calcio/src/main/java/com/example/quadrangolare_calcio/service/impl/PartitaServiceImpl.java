package com.example.quadrangolare_calcio.service.impl;

import com.example.quadrangolare_calcio.model.Partita;
import com.example.quadrangolare_calcio.model.TabellinoPartita;
import com.example.quadrangolare_calcio.repository.PartitaRepository;
import com.example.quadrangolare_calcio.repository.TabellinoPartitaRepository;
import com.example.quadrangolare_calcio.service.ArchivioGiocatoreService;
import com.example.quadrangolare_calcio.service.ArchivioSquadraService;
import com.example.quadrangolare_calcio.service.PartitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PartitaServiceImpl implements PartitaService {

    @Autowired
    private PartitaRepository partitaRepository;

    @Autowired
    private TabellinoPartitaRepository tabellinoRepository;

    @Autowired
    private ArchivioSquadraService archivioSquadraService;

    @Autowired
    private ArchivioGiocatoreService archivioGiocatoreService;

    @Override
    @Transactional
    public Partita salvaPartitaCompleta(Partita partita, List<TabellinoPartita> eventi) {
        // 1. Salviamo la partita
        Partita partitaSalvata = partitaRepository.save(partita);

        // 2. Salviamo il tabellino e aggiorniamo gli archivi giocatori
        for (TabellinoPartita evento : eventi) {
            evento.setPartita(partitaSalvata);
            tabellinoRepository.save(evento);

            int idEvento = evento.getEventoPartita().getIdEventoPartita();

            // Identifichiamo se l'evento fa parte della lotteria finale dei rigori
            // Potresti usare il minuto (es. minuto 91, no extra time) o un flag nel Tabellino
            boolean isLotteria = evento.getMinuto() > 90;

            if (idEvento == 4) { // Goal
                archivioGiocatoreService.aggiungiGol(evento.getGiocatore());
            } else if (idEvento == 5) { // Rigore Goal
                archivioGiocatoreService.aggiungiRigoreSegnato(evento.getGiocatore(), isLotteria);
            } else if (idEvento == 6) { // Rigore Parato
                archivioGiocatoreService.aggiungiRigoreParato(evento.getGiocatore(), isLotteria);
            }
        }

        // 3. Analisi del risultato per l'Archivio Squadra
        // Usiamo il risultato finale per stabilire chi ha vinto,
        // ma il risultato regular per le statistiche dei gol "su azione"
        String[] finale = partitaSalvata.getRisultatoFinale().split("-");
        int golFinaliHome = Integer.parseInt(finale[0]);
        int golFinaliAway = Integer.parseInt(finale[1]);

        String[] regular = partitaSalvata.getRisultatoRegular().split("-");
        int golRegularHome = Integer.parseInt(regular[0]);
        int golRegularAway = Integer.parseInt(regular[1]);

        boolean vintaHome = golFinaliHome > golFinaliAway;
        boolean finitaAiRigori = partitaSalvata.isRigori();

        // Aggiornamento Squadra Casa
        archivioSquadraService.aggiornaStatistichePartita(
                partitaSalvata.getSquadraHome(),
                golRegularHome, // gol fatti (regolari)
                golRegularAway, // gol subiti (regolari)
                vintaHome,
                finitaAiRigori && vintaHome, // vinta ai rigori
                finitaAiRigori && !vintaHome  // persa ai rigori
        );

        // Aggiornamento Squadra Trasferta
        archivioSquadraService.aggiornaStatistichePartita(
                partitaSalvata.getSquadraAway(),
                golRegularAway,
                golRegularHome,
                !vintaHome,
                finitaAiRigori && !vintaHome,
                finitaAiRigori && vintaHome
        );

        return partitaSalvata;
    }

    @Override
    public List<Partita> getPartitePerTorneo(int idTorneo) {
        return partitaRepository.findByTorneoIdTorneo(idTorneo);
    }

    @Override
    public Partita getDettaglioPartita(Long idPartita) {
        return partitaRepository.findById(idPartita).orElse(null);
    }
}
