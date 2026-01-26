package com.example.quadrangolare_calcio.service.impl;

import com.example.quadrangolare_calcio.dto.EventoDTO;
import com.example.quadrangolare_calcio.dto.PartitaDTO;
import com.example.quadrangolare_calcio.dto.TorneoSalvataggioDTO;
import com.example.quadrangolare_calcio.model.*;
import com.example.quadrangolare_calcio.repository.*;
import com.example.quadrangolare_calcio.service.ArchivioGiocatoreService;
import com.example.quadrangolare_calcio.service.ArchivioSquadraService;
import com.example.quadrangolare_calcio.service.TorneoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TorneoServiceImpl implements TorneoService {

    @Autowired
    private PartitaRepository partitaRepository;

    @Autowired
    private TipoPartitaRepository tipoPartitaRepository;

    @Autowired
    private GiocatoreRepository giocatoreRepository;

    @Autowired
    private EventoPartitaRepository eventoPartitaRepository;

    @Autowired
    private TabellinoPartitaRepository tabellinoPartitaRepository;

    @Autowired
    private TorneoRepository torneoRepository;

    @Autowired
    private ArchivioSquadraService archivioSquadraService;

    @Autowired
    private ArchivioGiocatoreService archivioGiocatoreService;

    @Autowired
    private ArchivioSquadraRepository archivioSquadraRepository;

    @Autowired
    private SquadraRepository squadraRepository;

    @Override
    @Transactional
    public void salvaTorneoIntero(TorneoSalvataggioDTO dto) {
        // 1. SALVATAGGIO TORNEO
        Torneo torneo = new Torneo();
        torneo.setNome(dto.getNomeTorneo());
        Torneo torneoSalvato = torneoRepository.save(torneo);

        // 2. AGGIORNAMENTO PARTECIPAZIONE SQUADRE (Punto 2)
        // Usiamo un Set per essere sicuri di incrementare solo una volta per squadra
        Set<Integer> idsPartecipanti = new HashSet<>(Arrays.asList(
                dto.getIdPrimo(), dto.getIdSecondo(), dto.getIdTerzo(), dto.getIdQuarto()
        ));

        for (Integer idSquadra : idsPartecipanti) {
            squadraRepository.findById((long) idSquadra).ifPresent(squadra -> {
                archivioSquadraService.incrementaPartecipazione(squadra); // Crea questo metodo nel Service o usa il Repository
            });
        }

        // 3. CICLO SALVATAGGIO PARTITE
        for (PartitaDTO pDto : dto.getPartite()) {
            Partita partita = new Partita();
            partita.setTorneo(torneoSalvato);
            partita.setRisultatoRegular(pDto.getRisultatoRegular());
            partita.setRisultatoFinale(pDto.getRisultatoFinale());
            partita.setRigori(pDto.isRigori());

            // FIX PUNTO 3: Recupero TipoPartita dall'oggetto reale nel DB
            TipoPartita tipo = tipoPartitaRepository.findByTipo(pDto.getTipoPartita());
            if (tipo == null) {
                System.out.println("ERRORE: Tipo partita '" + pDto.getTipoPartita() + "' non trovato nel DB!");
            }
            partita.setTipoPartita(tipo);

            // Recupero Squadre
            Squadra home = squadraRepository.findById((long) pDto.getIdSquadraHome()).orElse(null);
            Squadra away = squadraRepository.findById((long) pDto.getIdSquadraAway()).orElse(null);
            partita.setSquadraHome(home);
            partita.setSquadraAway(away);

            Partita partitaSalvata = partitaRepository.save(partita);

            // 4. CICLO TABELLINO E ARCHIVIO GIOCATORI (Punti 1 e 4)
            if (pDto.getEventi() != null) {
                for (EventoDTO eDto : pDto.getEventi()) {
                    TabellinoPartita tabellino = new TabellinoPartita();
                    tabellino.setPartita(partitaSalvata);
                    tabellino.setMinuto(eDto.getMinuto());

                    // Colleghiamo il giocatore reale
                    Giocatore g = giocatoreRepository.findById((long) eDto.getIdGiocatore()).orElse(null);
                    tabellino.setGiocatore(g);

                    EventoPartita ep;

                    // Se è un gol normale (non rigore)
                    if (eDto.getTipoEvento().equalsIgnoreCase("Tiro") && eDto.getEsitoEvento().equalsIgnoreCase("Goal")) {
                        ep = eventoPartitaRepository.findByTipoEventoAndEsitoEvento("Tiro", "Goal");
                    }

                    // Se è un gol su rigore
                    else if (eDto.getTipoEvento().equalsIgnoreCase("Rigore") && eDto.getEsitoEvento().equalsIgnoreCase("Goal")) {
                        ep = eventoPartitaRepository.findByTipoEventoAndEsitoEvento("Rigore", "Goal");
                    }

                    // Altri casi: parato, fuori, palo/trasversa, calcio d'angolo, ecc.
                    else {
                        ep = eventoPartitaRepository.findByTipoEventoAndEsitoEvento(
                                eDto.getTipoEvento(),
                                eDto.getEsitoEvento()
                        );
                    }

                    if (ep == null) {
                        throw new RuntimeException("EventoPartita non trovato: " + eDto.getTipoEvento() + " / " + eDto.getEsitoEvento());
                    }

                    tabellino.setEventoPartita(ep);


                    tabellinoPartitaRepository.save(tabellino);

                    // AGGIORNA ARCHIVIO GIOCATORE (Punto 1)
                    if ("Goal".equalsIgnoreCase(eDto.getEsitoEvento())) {
                        archivioGiocatoreService.aggiornaGol(g);

                        // Se è un rigore, aggiorna anche il contatore rigori
                        if ("Rigore".equalsIgnoreCase(eDto.getTipoEvento())) {
                            archivioGiocatoreService.aggiungiRigoreSegnato(g, false);
                        }
                    }


                }
            }

            // 5. AGGIORNAMENTO STATISTICHE MATCH (Vittorie/Sconfitte/Gol fatti)
            aggiornaStatisticheSquadre(pDto, home, away);
        }

        // 6. REGISTRAZIONE PODIO
        registraPodio(dto);
    }

    private void registraPodio(TorneoSalvataggioDTO dto) {
        squadraRepository.findById((long) dto.getIdPrimo()).ifPresent(s -> archivioSquadraService.registraPiazzamentoTorneo(s, 1));
        squadraRepository.findById((long) dto.getIdSecondo()).ifPresent(s -> archivioSquadraService.registraPiazzamentoTorneo(s, 2));
        squadraRepository.findById((long) dto.getIdTerzo()).ifPresent(s -> archivioSquadraService.registraPiazzamentoTorneo(s, 3));
        squadraRepository.findById((long) dto.getIdQuarto()).ifPresent(s -> archivioSquadraService.registraPiazzamentoTorneo(s, 4));
    }

    private void aggiornaStatisticheSquadre(PartitaDTO pDto, Squadra home, Squadra away) {
        // Dividiamo il punteggio (es. "5-3")
        String[] gol = pDto.getRisultatoFinale().split("-");
        int gH = Integer.parseInt(gol[0]);
        int gA = Integer.parseInt(gol[1]);

        boolean vintaHome = gH > gA;
        boolean vintaAway = gA > gH;
        boolean isRigori = pDto.isRigori();

        // Aggiornamento Home
        archivioSquadraService.aggiornaStatistichePartita(
                home,
                gH,
                gA,
                vintaHome,
                isRigori,
                (!vintaHome && isRigori) // persa ai rigori?
        );

        // Aggiornamento Away
        archivioSquadraService.aggiornaStatistichePartita(
                away,
                gA,
                gH,
                vintaAway,
                isRigori,
                (!vintaAway && isRigori) // persa ai rigori?
        );
    }

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
    public void chiudiTorneo(Long idTorneo) {
        // Recuperiamo la classifica finale del torneo appena concluso
        Map<Integer, String> classifica = getClassificaTorneo(idTorneo.intValue());

        // Per ogni posizione (1, 2, 3, 4), aggiorniamo l'archivio della squadra corrispondente
        classifica.forEach((posizione, nomeSquadra) -> {
            // Recuperiamo l'oggetto Squadra dal DB (tramite il nome o ID)
            Squadra squadra = squadraRepository.findByNome(nomeSquadra);
            // Registriamo il piazzamento storico
            archivioSquadraService.registraPiazzamentoTorneo(squadra, posizione);
        });
    }

    @Override
    public Torneo getDettaglioTorneo(Long idTorneo) {
        return torneoRepository.findById(idTorneo).orElse(null);
    }

    @Override
    public List<Map<String, Object>> getRankingStorico() {
        // Recuperiamo tutti gli archivi squadra ordinati per tornei vinti
        return archivioSquadraRepository.findAll().stream()
                .sorted(Comparator.comparingInt(ArchivioSquadra::getTorneiVinti).reversed())
                .map(archivio -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("squadra", archivio.getSquadra().getNome());
                    map.put("trofei", archivio.getTorneiVinti());
                    map.put("partecipazioni", archivio.getTorneiPartecipati());
                    return map;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, String>> getAlboDOro() {
        // Recuperiamo tutti i tornei
        List<Torneo> tornei = torneoRepository.findAll();
        List<Map<String, String>> albo = new ArrayList<>();

        for (Torneo t : tornei) {
            // Cerchiamo la partita di tipo "Finale" per quel torneo
            t.getPartite().stream()
                    .filter(p -> p.getTipoPartita().getTipo().equalsIgnoreCase("Finale"))
                    .findFirst()
                    .ifPresent(finale -> {
                        Map<String, String> voce = new HashMap<>();
                        voce.put("torneo", t.getNome());

                        // Logica per determinare il vincitore dal risultato finale
                        String[] punteggio = finale.getRisultatoFinale().split("-");
                        String vincitore = Integer.parseInt(punteggio[0]) > Integer.parseInt(punteggio[1])
                                ? finale.getSquadraHome().getNome()
                                : finale.getSquadraAway().getNome();

                        voce.put("vincitore", vincitore);
                        albo.add(voce);
                    });
        }
        return albo;
    }

    @Override
    public Map<String, Object> getStatsTorneo(int idTorneo) {
        Torneo torneo = torneoRepository.findById((long) idTorneo).orElse(null);
        if (torneo == null) return null;

        int golTotali = 0;
        for (Partita p : torneo.getPartite()) {
            String[] reg = p.getRisultatoRegular().split("-");
            golTotali += (Integer.parseInt(reg[0]) + Integer.parseInt(reg[1]));
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("nomeTorneo", torneo.getNome());
        stats.put("partiteGiocate", torneo.getPartite().size());
        stats.put("golTotali", golTotali);
        stats.put("mediaGol", (double) golTotali / torneo.getPartite().size());

        return stats;
    }

    @Override
    public Map<Integer, String> getClassificaTorneo(int idTorneo) {
        Torneo torneo = torneoRepository.findById((long) idTorneo).orElse(null);
        if (torneo == null) return new HashMap<>();

        Map<Integer, String> classifica = new TreeMap<>(); // TreeMap mantiene l'ordine 1, 2, 3, 4

        for (Partita p : torneo.getPartite()) {
            String tipo = p.getTipoPartita().getTipo().toLowerCase();

            // Gestione 1° e 2° Posto (Dalla Finale)
            if (tipo.contains("finale") && !tipo.contains("3")) {
                String[] result = p.getRisultatoFinale().split("-");
                boolean homeVince = Integer.parseInt(result[0]) > Integer.parseInt(result[1]);

                classifica.put(1, homeVince ? p.getSquadraHome().getNome() : p.getSquadraAway().getNome());
                classifica.put(2, homeVince ? p.getSquadraAway().getNome() : p.getSquadraHome().getNome());
            }

            // Gestione 3° e 4° Posto (Dalla Finalina)
            if (tipo.contains("3") || tipo.contains("terzo")) {
                String[] result = p.getRisultatoFinale().split("-");
                boolean homeVince = Integer.parseInt(result[0]) > Integer.parseInt(result[1]);

                classifica.put(3, homeVince ? p.getSquadraHome().getNome() : p.getSquadraAway().getNome());
                classifica.put(4, homeVince ? p.getSquadraAway().getNome() : p.getSquadraHome().getNome());
            }
        }
        return classifica;
    }

    @Override
    public List<String> getPodio(int idTorneo) {
        Map<Integer, String> classifica = getClassificaTorneo(idTorneo);
        List<String> podio = new ArrayList<>();

        if (classifica.containsKey(1)) podio.add("🥇 " + classifica.get(1));
        if (classifica.containsKey(2)) podio.add("🥈 " + classifica.get(2));
        if (classifica.containsKey(3)) podio.add("🥉 " + classifica.get(3));

        return podio;
    }

    @Override
    public List<Map<String, Object>> getMedagliereStorico() {
        return archivioSquadraRepository.findAll().stream()
                .sorted(Comparator.comparingInt(ArchivioSquadra::getTorneiVinti).reversed() // 1. Vittorie DESC
                        .thenComparing(a -> a.getSquadra().getNome()))                      // 2. Nome ASC (Alfabetico)
                .map(a -> {
                    Map<String, Object> riga = new LinkedHashMap<>();
                    riga.put("squadra", a.getSquadra().getNome());
                    riga.put("partecipazioni", a.getTorneiPartecipati());
                    riga.put("ori", a.getTorneiVinti());
                    riga.put("argenti", a.getSecondiPosti());
                    riga.put("bronzi", a.getTerziPosti());
                    riga.put("quarti", a.getQuartiPosti());
                    return riga;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getHallOfFame() {
        Map<String, Object> hall = new HashMap<>();

        // Squadra con più gol fatti nella storia
        archivioSquadraRepository.findAll().stream()
                .max(Comparator.comparingInt(ArchivioSquadra::getGolFattiTotali))
                .ifPresent(a -> hall.put("migliorAttaccoStorico", a.getSquadra().getNome() + " (" + a.getGolFattiTotali() + " gol)"));

        // Squadra con più vittorie totali (Regolari + Rigori)
        archivioSquadraRepository.findAll().stream()
                .max(Comparator.comparingInt(a -> a.getVittorieRegolari() + a.getVittorieRigori()))
                .ifPresent(a -> hall.put("piuVincente", a.getSquadra().getNome()));

        return hall;
    }
}
