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

    @Autowired
    private ArchivioGiocatoreRepository archivioGiocatoreRepository;

    @Override
    @Transactional
    public void salvaTorneoIntero(TorneoSalvataggioDTO dto) {
        // 1. SALVATAGGIO TORNEO
        Torneo torneo = new Torneo();
        torneo.setNome(dto.getNomeTorneo());
        Torneo torneoSalvato = torneoRepository.save(torneo);

        // 2. AGGIORNAMENTO PARTECIPAZIONE SQUADRE
        Set<Integer> idsPartecipanti = new HashSet<>(Arrays.asList(
                dto.getIdPrimo(), dto.getIdSecondo(), dto.getIdTerzo(), dto.getIdQuarto()
        ));

        for (Integer idSquadra : idsPartecipanti) {
            squadraRepository.findById((long) idSquadra).ifPresent(squadra -> {
                archivioSquadraService.incrementaPartecipazione(squadra);
            });
        }

        // 3. CICLO SALVATAGGIO PARTITE
        for (PartitaDTO pDto : dto.getPartite()) {
            Partita partita = new Partita();
            partita.setTorneo(torneoSalvato);
            partita.setRisultatoRegular(pDto.getRisultatoRegular());
            partita.setRisultatoFinale(pDto.getRisultatoFinale());
            partita.setRigori(pDto.isRigori());

            TipoPartita tipo = tipoPartitaRepository.findByTipo(pDto.getTipoPartita());
            partita.setTipoPartita(tipo);

            Squadra home = squadraRepository.findById((long) pDto.getIdSquadraHome()).orElse(null);
            Squadra away = squadraRepository.findById((long) pDto.getIdSquadraAway()).orElse(null);
            partita.setSquadraHome(home);
            partita.setSquadraAway(away);

            Partita partitaSalvata = partitaRepository.save(partita);

            // 4. CICLO TABELLINO E ARCHIVIO GIOCATORI
            if (pDto.getEventi() != null) {
                for (EventoDTO eDto : pDto.getEventi()) {

                    // --- 1. NON CREARE NUOVI EVENTI. CERCA QUELLO ESISTENTE NEL DB ---
                    EventoPartita ep = eventoPartitaRepository.findByTipoEventoAndEsitoEvento(
                            eDto.getTipoEvento(),
                            eDto.getEsitoEvento()
                    );

                    // Se per caso non lo trova (errore di battitura nel JS), lancia un errore o logga
                    if (ep == null) {
                        System.out.println("ERRORE: Evento non trovato nel DB per " + eDto.getTipoEvento() + " - " + eDto.getEsitoEvento());
                        continue;
                    }

                    // --- 2. CREA IL TABELLINO PUNTANDO ALL'EVENTO STATICO ---
                    TabellinoPartita tp = new TabellinoPartita();
                    tp.setPartita(partitaSalvata);
                    tp.setMinuto(eDto.getMinuto());
                    tp.setEventoPartita(ep);

                    Giocatore g = giocatoreRepository.findById((long) eDto.getIdGiocatore()).orElse(null);
                    tp.setGiocatore(g);
                    tabellinoPartitaRepository.save(tp);

                    // --- AGGIORNAMENTO STATISTICHE ARCHIVIO ---
                    if (g != null) {
                        ArchivioGiocatore ag = archivioGiocatoreService.getOrCreateArchivio(g);

                        String tipoEv = eDto.getTipoEvento();
                        String esitoEv = eDto.getEsitoEvento();

                        // GOL NORMALE
                        if ("Tiro".equalsIgnoreCase(tipoEv) && "Goal".equalsIgnoreCase(esitoEv)) {
                            ag.setGolTotali(ag.getGolTotali() + 1);
                        }
                        // RIGORE REGOLARE PARATO (dal portiere)
                        else if ("Rigore_regolare".equalsIgnoreCase(tipoEv) && "Parato".equalsIgnoreCase(esitoEv)) {
                            ag.setRigoriRegolariParati(ag.getRigoriRegolariParati() + 1);
                        }
                        // RIGORE LOTTERIA PARATO (dal portiere)
                        else if ("Rigore_lotteria".equalsIgnoreCase(tipoEv) && "Parato".equalsIgnoreCase(esitoEv)) {
                            ag.setRigoriLotteriaParati(ag.getRigoriLotteriaParati() + 1);
                        }

                        archivioGiocatoreRepository.save(ag);
                    }
                }
            }

            // 5. AGGIORNAMENTO STATISTICHE MATCH SQUADRE
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
        List<Torneo> tornei = torneoRepository.findAll();
        List<Map<String, String>> albo = new ArrayList<>();

        for (Torneo t : tornei) {
            t.getPartite().stream()
                    .filter(p -> p.getTipoPartita().getTipo().equalsIgnoreCase("Finale"))
                    .findFirst()
                    .ifPresent(finale -> {
                        Map<String, String> voce = new HashMap<>();
                        voce.put("torneo", t.getNome());

                        // Determina vincitore dal risultato finale
                        String[] punteggio = finale.getRisultatoFinale().split("-");
                        boolean homeVince = Integer.parseInt(punteggio[0]) > Integer.parseInt(punteggio[1]);
                        Squadra vincitoreSquadra = homeVince ? finale.getSquadraHome() : finale.getSquadraAway();

                        voce.put("vincitore", vincitoreSquadra.getNome());

                        // Logo dual-mode: path o Base64
                        String logo = vincitoreSquadra.getLogo();
                        if (logo != null && !logo.isEmpty()) {
                            if (!logo.startsWith("data:")) {
                                // è un path, aggiungiamo slash iniziale
                                logo = "/" + logo;
                            }
                            voce.put("vincitoreLogo", logo);
                        } else {
                            // fallback logo generico
                            voce.put("vincitoreLogo", "/images/default_logo.png");
                        }

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
                .sorted(
                        Comparator.comparingInt(ArchivioSquadra::getTorneiVinti).reversed()
                                .thenComparing(Comparator.comparingInt(ArchivioSquadra::getSecondiPosti).reversed())
                                .thenComparing(Comparator.comparingInt(ArchivioSquadra::getTerziPosti).reversed())
                                .thenComparing(Comparator.comparingInt(ArchivioSquadra::getQuartiPosti)) // ASC
                                .thenComparing(a -> a.getSquadra().getNome())
                )
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
        Map<String, Object> hall = new LinkedHashMap<>();

        // Squadra con più vittorie totali (Regolari + Rigori)
        // 1. Troviamo il numero massimo di vittorie totali tra tutte le squadre
        int maxVittorie = archivioSquadraRepository.findAll().stream()
                .mapToInt(a -> a.getVittorieRegolari() + a.getVittorieRigori())
                .max()
                .orElse(0);

        // 2. Se il massimo è > 0, prendiamo tutte le squadre che hanno quel valore
        if (maxVittorie > 0) {
            String squadreVincenti = archivioSquadraRepository.findAll().stream()
                    .filter(a -> (a.getVittorieRegolari() + a.getVittorieRigori()) == maxVittorie)
                    .map(a -> a.getSquadra().getNome())
                    .collect(Collectors.joining(", ")); // Le unisce con una virgola

            hall.put("Più Partite Vinte", squadreVincenti + " (" + maxVittorie + " vittorie)");
        }

        // 1. MIGLIOR ATTACCO STORICO (Squadra con più gol totali fatti, comprende ex-Aequo)
        int maxGolFatti = archivioSquadraRepository.findAll().stream()
                .mapToInt(ArchivioSquadra::getGolFattiTotali)
                .max()
                .orElse(0);

        if (maxGolFatti > 0) {
            String nomiAttacco = archivioSquadraRepository.findAll().stream()
                    .filter(as -> as.getGolFattiTotali() == maxGolFatti)
                    .map(as -> as.getSquadra().getNome())
                    .collect(Collectors.joining(", "));
            hall.put("Miglior Attacco", nomiAttacco + " (" + maxGolFatti + " gol)");
        }

        // 2. MURO DIFENSIVO (Ex-Aequo - Squadra con meno gol subiti)
        int minGolSubiti = archivioSquadraRepository.findAll().stream()
                .mapToInt(ArchivioSquadra::getGolSubitiTotali)
                .min()
                .orElse(-1);

        if (minGolSubiti != -1) {
            String nomiDifesa = archivioSquadraRepository.findAll().stream()
                    .filter(as -> as.getGolSubitiTotali() == minGolSubiti)
                    .map(as -> as.getSquadra().getNome())
                    .collect(Collectors.joining(", "));
            hall.put("Muro Difensivo", nomiDifesa + " (" + minGolSubiti + " subiti)");
        }

        // 3. PICHICHI STORICO (Giocatore con più gol totali)
        int maxGol = archivioGiocatoreRepository.findAll().stream()
                .mapToInt(ArchivioGiocatore::getGolTotali)
                .max()
                .orElse(0);

        if (maxGol > 0) {
            String nomiPichichi = archivioGiocatoreRepository.findAll().stream()
                    .filter(ag -> ag.getGolTotali() == maxGol)
                    .map(ag -> ag.getGiocatore().getNome() + " " + ag.getGiocatore().getCognome() + " -" + ag.getGiocatore().getSquadra().getNome())
                    .collect(Collectors.joining(", "));

            hall.put("Pichichi Storico", nomiPichichi + " (" + maxGol + " gol)");
        }

        // 4. SARACINESCA (Solo tra i giocatori che sono Portieri)
        archivioGiocatoreRepository.findAll().stream()
                .filter(ag -> ag.getGiocatore().getRuolo() != null &&
                        "Portiere".equalsIgnoreCase(ag.getGiocatore().getRuolo().getTipologia().getCategoria()))
                .max(Comparator.comparingInt(ag -> ag.getRigoriRegolariParati() + ag.getRigoriLotteriaParati()))
                .ifPresent(ag -> {
                    int totali = ag.getRigoriRegolariParati() + ag.getRigoriLotteriaParati();
                    hall.put("Para rigori", ag.getGiocatore().getCognome()
                            + " -" + ag.getGiocatore().getSquadra().getNome()
                            + " (" + totali + " rigori parati)");

                });

        // 5. PARTITA SPETTACOLO (Più gol segnati in un solo match)
        // Nota: assumendo che 'risultato_regular' sia in formato "3-2"
        partitaRepository.findAll().stream()
                .max(Comparator.comparingInt(p -> {
                    // Calcoliamo la somma dei gol basandoci sul tempo regolare (es. "3-2")
                    String[] parti = p.getRisultatoRegular().split("-");
                    return Integer.parseInt(parti[0].trim()) + Integer.parseInt(parti[1].trim());
                }))
                .ifPresent(p -> {
                    String descrizione = p.getSquadraHome().getNome() + " vs " + p.getSquadraAway().getNome()
                            + " (" + p.getRisultatoRegular();
                    if (p.isRigori()) {
                        descrizione += ", " + p.getRisultatoFinale() + " d.c.r.";
                    }
                    descrizione += ") - "
                            + p.getTorneo().getNome()      // Nome torneo
                            + " - " + p.getTipoPartita().getTipo();   // Fase del torneo (es. Semifinale 1, Finale)
                    hall.put("Partita Spettacolo", descrizione);
                });

        return hall;
    }
}
