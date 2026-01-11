package com.example.quadrangolare_calcio.service.impl;

import com.example.quadrangolare_calcio.model.Giocatore;
import com.example.quadrangolare_calcio.model.Partita;
import com.example.quadrangolare_calcio.model.TabellinoPartita;
import com.example.quadrangolare_calcio.repository.EventoPartitaRepository;
import com.example.quadrangolare_calcio.repository.GiocatoreRepository;
import com.example.quadrangolare_calcio.repository.PartitaRepository;
import com.example.quadrangolare_calcio.repository.TabellinoPartitaRepository;
import com.example.quadrangolare_calcio.service.ArchivioGiocatoreService;
import com.example.quadrangolare_calcio.service.ArchivioSquadraService;
import com.example.quadrangolare_calcio.service.PartitaService;
import com.example.quadrangolare_calcio.service.TorneoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    private GiocatoreRepository giocatoreRepository;

    @Autowired
    private EventoPartitaRepository eventoPartitaRepository;

    @Autowired
    private TorneoService torneoService;

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

    @Override
    public List<Partita> getRimonteEffettuate(int idTorneo) {
        List<Partita> partite = partitaRepository.findByTorneoIdTorneo(idTorneo);

        return partite.stream().filter(p -> {
            // Risultato regular (es. "0-1")
            String[] reg = p.getRisultatoRegular().split("-");
            int regHome = Integer.parseInt(reg[0]);
            int regAway = Integer.parseInt(reg[1]);

            // Risultato finale (es. "2-1")
            String[] fin = p.getRisultatoFinale().split("-");
            int finHome = Integer.parseInt(fin[0]);
            int finAway = Integer.parseInt(fin[1]);

            // Verifica se chi era in svantaggio ha poi vinto
            boolean rimontaHome = (regHome < regAway && finHome > finAway);
            boolean rimontaAway = (regAway < regHome && finAway > finHome);

            return rimontaHome || rimontaAway;
        }).collect(Collectors.toList());
    }

    @Override
    public Map<String, List<String>> getSequenzaRigori(int idPartita) {
        // Filtriamo il tabellino per la partita e per eventi tipo rigore (ID 5 o 6)
        // avvenuti oltre il minuto 120 (convenzione lotteria)
        List<TabellinoPartita> eventiRigori = tabellinoRepository.findByPartitaIdPartita(idPartita)
                .stream()
                .filter(t -> t.getMinuto() > 120)
                .sorted(Comparator.comparingInt(TabellinoPartita::getMinuto))
                .collect(Collectors.toList());

        Map<String, List<String>> sequenza = new HashMap<>();
        for (TabellinoPartita t : eventiRigori) {
            String esito = t.getEventoPartita().getEsitoEvento(); // "Segnato" o "Parato/Sbagliato"
            String nomeGiocatore = t.getGiocatore().getCognome();

            sequenza.computeIfAbsent(t.getPartita().getSquadraHome().getNome(), k -> new ArrayList<>())
                    .add(nomeGiocatore + ": " + esito);
        }
        return sequenza;
    }

    @Override
    public String getFasciaOrariaPiuProlifica(int idTorneo) {
        List<TabellinoPartita> gol = tabellinoRepository.findByPartitaTorneoIdTorneo(idTorneo)
                .stream()
                .filter(t -> t.getEventoPartita().getIdEventoPartita() == 4) // Solo Goal su azione
                .collect(Collectors.toList());

        // Raggruppiamo i gol in fasce (0-15, 16-30, ecc.)
        Map<String, Long> fasce = gol.stream().collect(Collectors.groupingBy(t -> {
            int m = t.getMinuto();
            if (m <= 15) return "0-15";
            if (m <= 30) return "16-30";
            if (m <= 45) return "31-45";
            if (m <= 60) return "46-60";
            if (m <= 75) return "61-75";
            return "76-90";
        }, Collectors.counting()));

        return fasce.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Nessun dato");
    }

    @Override
    @Transactional
    public void registraEventoSimulato(Long idPartita, int secondi, long idEvento, long idGiocatore) {
        // 1. Convertiamo i secondi dello script in minuti
        int minuto = secondi / 60;

        // 2. Creiamo il record del tabellino
        TabellinoPartita tabellino = new TabellinoPartita();

        // Usiamo getReferenceById per caricare i proxy senza query SELECT inutili
        tabellino.setPartita(partitaRepository.getReferenceById(idPartita));
        tabellino.setGiocatore(giocatoreRepository.getReferenceById(idGiocatore));
        tabellino.setEventoPartita(eventoPartitaRepository.getReferenceById(idEvento));
        tabellino.setMinuto(minuto);

        tabellinoRepository.save(tabellino);

        // 3. Aggiorniamo l'archivio del giocatore in tempo reale
        // Recuperiamo l'oggetto reale per passarlo ai service di statistica
        Giocatore g = giocatoreRepository.getReferenceById(idGiocatore);

        if (idEvento == 4) { // Assumendo 4 = Goal su azione
            archivioGiocatoreService.aggiungiGol(g);
        } else if (idEvento == 5) { // Assumendo 5 = Rigore Segnato
            // Se il tempo supera i 300 secondi (fine match JS), è lotteria
            boolean isLotteria = minuto > 90;
            archivioGiocatoreService.aggiungiRigoreSegnato(g, isLotteria);
        }
    }

    @Override
    @Transactional
    public void salvaRisultatoFinale(int idPartita, String resRegular, String resFinale, boolean aiRigori) {
        // 1. Recupero partita
        Partita partita = partitaRepository.findById((long) idPartita)
                .orElseThrow(() -> new RuntimeException("Partita non trovata"));

        // 2. Aggiorno i campi della partita
        partita.setRisultatoRegular(resRegular);
        partita.setRisultatoFinale(resFinale);
        partita.setRigori(aiRigori);
        partitaRepository.save(partita);

        // 3. Calcolo parametri per Squadra HOME
        String[] scoreHome = resFinale.split("-");
        int golHome = Integer.parseInt(scoreHome[0]);
        int golAway = Integer.parseInt(scoreHome[1]);

        boolean homeVinta = golHome > golAway;
        boolean homeVintaRigori = aiRigori && homeVinta;
        boolean homePersaRigori = aiRigori && !homeVinta;

        // Chiamata per la squadra HOME
        archivioSquadraService.aggiornaStatistichePartita(
                partita.getSquadraHome(), golHome, golAway, homeVinta, homeVintaRigori, homePersaRigori
        );

        // 4. Calcolo parametri per Squadra AWAY
        boolean awayVinta = golAway > golHome;
        boolean awayVintaRigori = aiRigori && awayVinta;
        boolean awayPersaRigori = aiRigori && !awayVinta;

        // Chiamata per la squadra AWAY
        archivioSquadraService.aggiornaStatistichePartita(
                partita.getSquadraAway(), golAway, golHome, awayVinta, awayVintaRigori, awayPersaRigori
        );

        // 5. Gestione chiusura torneo (se Finale)
        if (partita.getTipoPartita().getTipo().equalsIgnoreCase("Finale")) {
            torneoService.chiudiTorneo((long) partita.getTorneo().getIdTorneo());
        }
    }
}
