package com.example.quadrangolare_calcio.service.impl;

import com.example.quadrangolare_calcio.dto.*;
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
                    if (g == null) {
                        System.err.println("ERRORE: tentativo di salvare evento per giocatore inesistente, minuto=" + eDto.getMinuto());
                        continue; // salta questo evento
                    }
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
                        // RIGORE REGOLARE SEGNATO
                        else if ("Rigore_regolare".equalsIgnoreCase(tipoEv) && "Goal".equalsIgnoreCase(esitoEv)) {
                            ag.setGolTotali(ag.getGolTotali() + 1);          // conta anche nel totale gol
                            ag.setRigoriRegolariSegnati(ag.getRigoriRegolariSegnati() + 1); // incremento rigori segnati
                        }
                        // RIGORE REGOLARE PARATO (dal portiere)
                        else if ("Rigore_regolare".equalsIgnoreCase(tipoEv) && "Parato".equalsIgnoreCase(esitoEv)) {
                            ag.setRigoriRegolariParati(ag.getRigoriRegolariParati() + 1);
                        }
                        // RIGORE REGOLARE SEGNATO
                        else if ("Rigore_lotteria".equalsIgnoreCase(tipoEv) && "Goal".equalsIgnoreCase(esitoEv)) {
                            ag.setRigoriLotteriaSegnati(ag.getRigoriLotteriaSegnati() + 1); // incremento rigori segnati lotteria
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
                        voce.put("idTorneo", String.valueOf(t.getIdTorneo()));
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
        int partiteAiRigori = 0;

        List<Partita> partitePiuGol = new ArrayList<>();
        List<Partita> partiteMenoGol = new ArrayList<>();
        List<Partita> partiteDeciseAiRigori = new ArrayList<>();

        int maxGol = -1;
        int minGol = Integer.MAX_VALUE;


        // 👉 USIAMO GLI ID, NON LE ENTITY
        Map<Long, Integer> golFatti = new HashMap<>();
        Map<Long, Integer> golSubiti = new HashMap<>();
        Map<Long, Squadra> squadreMap = new HashMap<>();

        // 👉 capocannoniere: giocatoreId -> gol
        Map<Long, Integer> golGiocatori = new HashMap<>();
        Map<Long, Giocatore> giocatoriMap = new HashMap<>();

        // ======================
        // PARTITE
        // ======================
        for (Partita p : torneo.getPartite()) {

            String[] reg = p.getRisultatoRegular().split("-");
            int golHome = Integer.parseInt(reg[0]);
            int golAway = Integer.parseInt(reg[1]);

            int golMatch = golHome + golAway;

            golTotali += golMatch;

            // --- gestione ex-aequo per più gol ---
            if (golMatch > maxGol) {
                maxGol = golMatch;
                partitePiuGol.clear();
                partitePiuGol.add(p);
            } else if (golMatch == maxGol) {
                partitePiuGol.add(p);
            }

            // --- gestione ex-aequo per meno gol ---
            if (golMatch < minGol) {
                minGol = golMatch;
                partiteMenoGol.clear();
                partiteMenoGol.add(p);
            } else if (golMatch == minGol) {
                partiteMenoGol.add(p);
            }


            if (p.getRisultatoFinale() != null &&
                    !p.getRisultatoFinale().equals(p.getRisultatoRegular())) {

                partiteAiRigori++;
                partiteDeciseAiRigori.add(p);
            }


            golFatti.merge((long) p.getSquadraHome().getIdSquadra(), golHome, Integer::sum);
            golFatti.merge((long) p.getSquadraAway().getIdSquadra(), golAway, Integer::sum);

            golSubiti.merge((long) p.getSquadraHome().getIdSquadra(), golAway, Integer::sum);
            golSubiti.merge((long) p.getSquadraAway().getIdSquadra(), golHome, Integer::sum);

            squadreMap.putIfAbsent(
                    (long) p.getSquadraHome().getIdSquadra(),
                    p.getSquadraHome()
            );
            squadreMap.putIfAbsent(
                    (long) p.getSquadraAway().getIdSquadra(),
                    p.getSquadraAway()
            );

        }

        // ======================
        // TROVO IL VINCITORE DEL TORNEO
        // ======================
        Squadra vincitore = torneo.getPartite().stream()
                // filtriamo solo la finale
                .filter(p -> "finale".equalsIgnoreCase(p.getTipoPartita().getTipo()))
                .findFirst()
                .map(p -> {
                    // prendi risultato finale se presente, altrimenti quello regolare
                    String[] risultato = (p.getRisultatoFinale() != null
                            ? p.getRisultatoFinale()
                            : p.getRisultatoRegular())
                            .split("-");
                    int golHome = Integer.parseInt(risultato[0]);
                    int golAway = Integer.parseInt(risultato[1]);

                    // squadra con più gol
                    if (golHome > golAway) return p.getSquadraHome();
                    else if (golAway > golHome) return p.getSquadraAway();
                    else return null; // pareggio impossibile in finale, ma gestiamo il caso
                })
                .orElse(null);


        // ======================
        // TABELLINI → GOL GIOCATORI
        // ======================
        List<TabellinoPartita> tabellini =
                tabellinoPartitaRepository.findByPartitaTorneoIdTorneo(idTorneo);

        for (TabellinoPartita t : tabellini) {

            EventoPartita e = t.getEventoPartita();

            // SOLO goal validi
            if ("goal".equalsIgnoreCase(e.getEsitoEvento())) {

                Giocatore g = t.getGiocatore();
                long idGiocatore = g.getIdGiocatore();

                giocatoriMap.put(idGiocatore, g);
                golGiocatori.merge(idGiocatore, 1, Integer::sum);
            }
        }

        // ======================
        // CALCOLI FINALI
        // ======================

        // Miglior attacco (ex-aequo)
        int maxGolFatti = golFatti.values().stream()
                .max(Integer::compareTo)
                .orElse(0);

        List<Squadra> miglioriAttacchi = golFatti.entrySet().stream()
                .filter(e -> e.getValue() == maxGolFatti)
                .map(e -> squadreMap.get(e.getKey()))
                .toList();


        // Miglior difesa (ex-aequo)
        int minGolSubiti = golSubiti.values().stream()
                .min(Integer::compareTo)
                .orElse(0);

        List<Squadra> miglioriDifese = golSubiti.entrySet().stream()
                .filter(e -> e.getValue() == minGolSubiti)
                .map(e -> squadreMap.get(e.getKey()))
                .toList();


        // Capocannoniere (ex-aequo)
        int maxGolGiocatore = golGiocatori.values().stream()
                .max(Integer::compareTo)
                .orElse(0);

        List<Giocatore> capocannonieri = golGiocatori.entrySet().stream()
                .filter(e -> e.getValue() == maxGolGiocatore)
                .map(e -> giocatoriMap.get(e.getKey()))
                .toList();


        // ======================
        // MAP DI RITORNO
        // ======================
        Map<String, Object> stats = new HashMap<>();

        stats.put("nomeTorneo", torneo.getNome());
        stats.put("partiteGiocate", torneo.getPartite().size());
        stats.put("vincitore", vincitore != null ? vincitore.getNome() : "-");
        stats.put("golTotali", golTotali);
        stats.put("mediaGol", torneo.getPartite().isEmpty()
                ? 0
                : (double) golTotali / torneo.getPartite().size());

        stats.put("migliorAttacco",
                miglioriAttacchi.isEmpty() ? "-" :
                        miglioriAttacchi.stream()
                                .map(s -> " > " + s.getNome() + " (" + maxGolFatti + " gol segnati)")
                                .collect(Collectors.joining("\n"))
        );

        stats.put("migliorDifesa",
                miglioriDifese.isEmpty() ? "-" :
                        miglioriDifese.stream()
                                .map(s -> " > " + s.getNome() + " (" + minGolSubiti + " gol subiti)")
                                .collect(Collectors.joining("\n"))
        );

        stats.put("capocannoniere",
                capocannonieri.isEmpty() ? "-" :
                        capocannonieri.stream()
                                .map(g -> " > " + g.getNome() + " " + g.getCognome()
                                        + " - " + g.getSquadra().getNome()
                                        + " (" + maxGolGiocatore + " gol)")
                                .collect(Collectors.joining("\n"))
        );


        stats.put("partitaPiuGol",
                partitePiuGol.isEmpty() ? "-" :
                        partitePiuGol.stream()
                                .map(p -> " > " + formatPartitaConDettagli(p))
                                .collect(Collectors.joining("\n")));

        stats.put("partitaMenoGol",
                partiteMenoGol.isEmpty() ? "-" :
                        partiteMenoGol.stream()
                                .map(p -> " > " + formatPartitaConDettagli(p))
                                .collect(Collectors.joining("\n")));

        stats.put("partiteAiRigori", partiteAiRigori);

        stats.put("partiteAiRigoriDettaglio",
                partiteDeciseAiRigori.isEmpty() ? "-" :
                        partiteDeciseAiRigori.stream()
                                .map(p -> " > " + formatPartitaAiRigori(p))
                                .collect(Collectors.joining("\n")));


        return stats;
    }


    // metodo helper per "tradurre" lato html le fasi del torneo
    private String formatPartitaConDettagli(Partita p) {

        if (p == null) return "-";

        String tipoDb = p.getTipoPartita().getTipo();
        String fase;

        switch (tipoDb.toLowerCase()) {
            case "semifinale1" -> fase = "Semifinale 1";
            case "semifinale2" -> fase = "Semifinale 2";
            case "finale3-4" -> fase = "Finale 3°-4° posto";
            case "finale" -> fase = "Finale";
            default -> fase = tipoDb;
        }

        return p.getSquadraHome().getNome() + " " +
                p.getRisultatoRegular() + " " +
                p.getSquadraAway().getNome() +
                " (" + fase + ")";
    }


    // metodo helper per "tradurre" lato html le partite finite ai rigori
    private String formatPartitaAiRigori(Partita p) {

        if (p == null) return "-";

        String tipoDb = p.getTipoPartita().getTipo();
        String fase;

        switch (tipoDb.toLowerCase()) {
            case "semifinale1" -> fase = "Semifinale 1";
            case "semifinale2" -> fase = "Semifinale 2";
            case "finale3-4" -> fase = "Finale 3°-4° posto";
            case "finale" -> fase = "Finale";
            default -> fase = tipoDb;
        }

        return p.getSquadraHome().getNome() + " " +
                p.getRisultatoRegular() + " " +
                p.getSquadraAway().getNome() +
                " (" + p.getRisultatoFinale() + " d.c.r.)" +
                "\n" + fase;
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

        // 3. PICHICHI STORICO (Giocatore con più gol totali + gestione ex-aequo)
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

        // 4. SARACINESCA (Solo tra i giocatori che sono Portieri + caso ex-aequo)
        List<ArchivioGiocatore> portieri = archivioGiocatoreRepository.findAll().stream()
                .filter(ag ->
                        ag.getGiocatore().getRuolo() != null &&
                                "Portiere".equalsIgnoreCase(
                                        ag.getGiocatore().getRuolo().getTipologia().getCategoria()
                                )
                )
                .toList();

        int maxRigoriParati = portieri.stream()
                .mapToInt(ag ->
                        ag.getRigoriRegolariParati() + ag.getRigoriLotteriaParati()
                )
                .max()
                .orElse(0);

        if (maxRigoriParati > 0) {

            List<String> migliori = portieri.stream()
                    .filter(ag ->
                            ag.getRigoriRegolariParati() + ag.getRigoriLotteriaParati()
                                    == maxRigoriParati
                    )
                    .map(ag ->
                            ag.getGiocatore().getCognome()
                                    + " - " + ag.getGiocatore().getSquadra().getNome()
                    )
                    .toList();

            hall.put(
                    "Para rigori",
                    String.join(", ", migliori)
                            + " (" + maxRigoriParati + " rigori parati)"
            );
        }


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


    @Override
    public List<Map<String, Object>> getClassificaPortieriAllTime() {

        List<ArchivioGiocatore> archivi = archivioGiocatoreRepository.findAll();
        List<Map<String, Object>> lista = new ArrayList<>();

        for (ArchivioGiocatore ag : archivi) {
            Giocatore g = ag.getGiocatore();

            // SOLO portieri
            if (!"PORTIERE".equalsIgnoreCase(g.getRuolo().getTipologia().getCategoria())) continue;

            // Rigori parati e rigori lotteria parati
            int rigoriParati = ag.getRigoriRegolariParati();
            int rigoriLotteriaParati = ag.getRigoriLotteriaParati();

            // Squadra e tornei partecipati
            Squadra squadra = g.getSquadra();
            int torneiPartecipati = 0;
            if (squadra != null) {
                Optional<ArchivioSquadra> as = archivioSquadraRepository.findBySquadra(squadra);
                if (as.isPresent()) {
                    torneiPartecipati = as.get().getTorneiPartecipati();
                }
            }

            int partiteGiocate = 2 * torneiPartecipati; // sempre 2 partite per torneo

            // --- CALCOLO CLEAN SHEETS E PARTITE DECIDE AI RIGORI ---
            int cleanSheets = 0;
            int partiteDeciseRigori = 0;

            // Recupera tutte le partite in cui questo portiere ha giocato
            List<Partita> partite = partitaRepository.findAllBySquadra(squadra);

            for (Partita p : partite) {
                boolean home = p.getSquadraHome().equals(squadra);
                boolean away = p.getSquadraAway().equals(squadra);

                if (!home && !away) continue;

                String risultato = p.getRisultatoRegular(); // esempio "4-1"
                String[] parts = risultato.split("-");
                int golHome = Integer.parseInt(parts[0]);
                int golAway = Integer.parseInt(parts[1]);

                int golSubiti = home ? golAway : golHome;

                if (golSubiti == 0) cleanSheets++;

                if (p.isRigori()) partiteDeciseRigori++;
            }

            Map<String, Object> row = new HashMap<>();
            row.put("nomeCompleto", g.getNome() + " " + g.getCognome());
            row.put("squadra", squadra != null ? squadra.getNome() : "-");
            row.put("partiteGiocate", partiteGiocate);
            row.put("rigoriParati", rigoriParati);
            row.put("cleanSheets", cleanSheets);
            row.put("partiteDeciseRigori", partiteDeciseRigori);
            row.put("rigoriLotteriaParati", rigoriLotteriaParati);

            lista.add(row);
        }

        // ordinamento: prima rigoriParati decrescente, poi cleanSheets, poi nome
        lista.sort(
                Comparator
                        .comparingInt((Map<String, Object> m) ->
                                (int) m.get("rigoriParati") + (int) m.get("rigoriLotteriaParati")
                        )
                        .reversed()
                        .thenComparing(
                                Comparator.comparingInt(
                                        (Map<String, Object> m) -> (int) m.get("cleanSheets")
                                ).reversed()
                        )
                        .thenComparing(m -> (String) m.get("nomeCompleto"))
        );



        // Posizione con ex-aequo
        int pos = 1;
        Integer rigoriPrev = null;
        for (Map<String, Object> r : lista) {
            int rp = (int) r.get("rigoriParati");
            if (rigoriPrev != null && rp == rigoriPrev) {
                r.put("posizione", 0); // "="
            } else {
                r.put("posizione", pos++);
                rigoriPrev = rp;
            }
        }

        return lista;
    }



    @Override
    public List<Map<String, Object>> getClassificaMarcatoriAllTime() {

        List<ArchivioGiocatore> archivi = archivioGiocatoreRepository.findAll();
        List<Map<String, Object>> lista = new ArrayList<>();

        for (ArchivioGiocatore ag : archivi) {
            Giocatore g = ag.getGiocatore();

            // solo giocatori di movimento
            if ("PORTIERE".equalsIgnoreCase(g.getRuolo().getTipologia().getCategoria())) continue;

            int gol = ag.getGolTotali();
            if (gol == 0) continue;

            int rigori = ag.getRigoriRegolariSegnati();

            // Recuperiamo i tornei partecipati della squadra del giocatore dall'archivio_squadra
            Optional<ArchivioSquadra> archivioSquadra = archivioSquadraRepository.findBySquadra(g.getSquadra());
            int torneiPartecipati = (archivioSquadra.isPresent()) ? archivioSquadra.get().getTorneiPartecipati() : 0;

            // Calcolo media gol: gol totali diviso (2 partite per torneo × tornei partecipati)
            double mediaGol = (torneiPartecipati > 0) ? ((double) gol) / (2 * torneiPartecipati) : 0;
            mediaGol = Math.round(mediaGol * 100.0) / 100.0;

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("nomeCompleto", g.getNome() + " " + g.getCognome());
            row.put("squadra", g.getSquadra().getNome());
            row.put("tipologia", g.getRuolo().getTipologia().getCategoria());
            row.put("partecipazioni", 2 * torneiPartecipati);
            row.put("gol", gol);
            row.put("rigori", rigori);
            row.put("mediaGol", mediaGol);

            lista.add(row);
        }

        // ordinamento: prima per gol, poi per cognome
        lista.sort(
                Comparator
                        .comparingInt((Map<String, Object> m) -> (int) m.get("gol"))
                        .reversed()
                        .thenComparing(m ->
                                        ((String) m.get("nomeCompleto"))
                                                .substring(((String) m.get("nomeCompleto")).lastIndexOf(" ") + 1),
                                String.CASE_INSENSITIVE_ORDER
                        )
        );

        // posizione con ex-aequo
        int pos = 1;
        Integer golPrev = null;
        for (Map<String, Object> r : lista) {
            int g = (int) r.get("gol");
            if (golPrev != null && g == golPrev) {
                r.put("posizione", 0); // "="
            } else {
                r.put("posizione", pos++);
                golPrev = g;
            }
        }

        return lista;
    }

    @Override
    public Map<String, MiniMatchDTO> getMiniTabellone(int idTorneo) {
        Map<String, MiniMatchDTO> tabellone = new HashMap<>();

        List<Partita> partite = torneoRepository
                .findById((long) idTorneo)
                .orElseThrow()
                .getPartite();

        for (Partita p : partite) {
            // Estrazione gol regolamentari
            int golHome = 0;
            int golAway = 0;

            if (p.getRisultatoRegular() != null) {
                String[] reg = p.getRisultatoRegular().split("-");
                golHome = Integer.parseInt(reg[0]);
                golAway = Integer.parseInt(reg[1]);
            }

            // rigori (SOLO se ci sono)
            Integer rigoriHome = null;
            Integer rigoriAway = null;

            if (p.isRigori()) {
                String[] rig = p.getRisultatoFinale().split("-");
                rigoriHome = Integer.parseInt(rig[0]);
                rigoriAway = Integer.parseInt(rig[1]);
            }

            // vincitore
            boolean homeWinner;
            boolean awayWinner;

            if (p.isRigori()) {
                homeWinner = rigoriHome > rigoriAway;
                awayWinner = rigoriAway > rigoriHome;
            } else {
                homeWinner = golHome > golAway;
                awayWinner = golAway > golHome;
            }


            // Creo i DTO
            MiniTeamDTO homeDTO = new MiniTeamDTO(
                    buildLogoSrc(p.getSquadraHome().getLogo()),
                    golHome,
                    rigoriHome,
                    homeWinner
            );


            MiniTeamDTO awayDTO = new MiniTeamDTO(
                    buildLogoSrc(p.getSquadraAway().getLogo()),
                    golAway,
                    rigoriAway,
                    awayWinner
            );


            MiniMatchDTO matchDTO = new MiniMatchDTO(homeDTO, awayDTO);

            // Aggiungo alla mappa con chiavi coerenti al template
            switch (p.getTipoPartita().getTipo().toLowerCase()) {
                case "semifinale1" -> tabellone.put("semi1", matchDTO);
                case "semifinale2" -> tabellone.put("semi2", matchDTO);
                case "finale3-4"   -> tabellone.put("finalina", matchDTO);
                case "finale"      -> tabellone.put("finale", matchDTO);
            }
        }

        return tabellone;
    }

    @Override
    public List<ClassificaSquadraDTO> getClassificaTorneoDettagliata(int idTorneo) {

        List<Partita> partite = partitaRepository.findByTorneoIdTorneo(idTorneo);

        // Recupera classifica e stats
        Map<Integer, String> classifica = getClassificaTorneo(idTorneo); // 1°-4° posto
        Map<String, Object> stats = getStatsTorneo(idTorneo);
        System.out.println("STATS TORNEO: " + stats);

        List<ClassificaSquadraDTO> result = new ArrayList<>();

        for (Map.Entry<Integer, String> entry : classifica.entrySet()) {
            String squadraNome = entry.getValue();

            int golFatti = 0;
            int golSubiti = 0;
            int vittorie = 0;

            for (Partita p : partite) {

                boolean isHome = p.getSquadraHome().getNome().equals(squadraNome);
                boolean isAway = p.getSquadraAway().getNome().equals(squadraNome);

                if (!isHome && !isAway) continue;

                String risultatoGol = p.getRisultatoRegular(); // SEMPRE per gol
                String risultatoVittoria = p.isRigori()
                        ? p.getRisultatoFinale()
                        : p.getRisultatoRegular();


                String[] scoreGol = risultatoGol.split("-");
                String[] scoreVittoria = risultatoVittoria.split("-");

                int golHome = Integer.parseInt(scoreGol[0]);
                int golAway = Integer.parseInt(scoreGol[1]);

                int winHome = Integer.parseInt(scoreVittoria[0]);
                int winAway = Integer.parseInt(scoreVittoria[1]);


                if (isHome) {
                    golFatti += golHome;
                    golSubiti += golAway;
                    if (winHome > winAway) vittorie++;
                } else {
                    golFatti += golAway;
                    golSubiti += golHome;
                    if (winAway > winHome) vittorie++;
                }

            }


            ClassificaSquadraDTO dto = new ClassificaSquadraDTO();
            dto.setPosizione(entry.getKey());
            dto.setNome(squadraNome);

            // Recupera logo direttamente dall'archivio tramite nome
            dto.setLogo(buildLogoSrc(getLogoSquadra(squadraNome)));

            int numeroPartite = partitaRepository.countByTorneoIdAndSquadra(squadraNome, idTorneo);
            dto.setPartiteGiocate(numeroPartite);

            dto.setGolFatti(golFatti);
            dto.setGolSubiti(golSubiti);
            dto.setDifferenzaReti(golFatti - golSubiti);
            dto.setVittorie(vittorie);

            result.add(dto);
        }

        // Ordina per posizione (1°,2°,3°,4°)
        result.sort(Comparator.comparingInt(ClassificaSquadraDTO::getPosizione));

        return result;
    }


    // Metodo helper per recuperare logo tramite nome squadra
    private String getLogoSquadra(String nomeSquadra) {
        try {
            ArchivioSquadra archivio = archivioSquadraService.getOrCreateArchivioByNome(nomeSquadra);
            return archivio.getSquadra().getLogo();
        } catch (Exception e) {
            // log dell’errore, ma non facciamo crashare la pagina
            System.out.println("Logo non trovato per squadra: " + nomeSquadra + " -> uso default");
            return "/images/default_logo.png";
        }
    }



    private String buildLogoSrc(String logo) {
        if (logo == null || logo.isBlank()) {
            return "/images/default.png";
        }

        // base64?
        if (!logo.startsWith("images") && !logo.startsWith("/images")) {
            return "data:image/png;base64," + logo;
        }

        // path
        return logo.startsWith("/") ? logo : "/" + logo;
    }


    @Override
    public List<ClassificaMarcatoriDTO> getClassificaMarcatoriDettagliata(int idTorneo) {

        List<TabellinoPartita> tabellini =
                tabellinoPartitaRepository.findByPartitaTorneoIdTorneo(idTorneo);

        // 👉 USIAMO GLI ID, NON LE ENTITY
        Map<Long, int[]> stats = new HashMap<>(); // idGiocatore -> [golTotali, rigori]
        Map<Long, Giocatore> giocatoriMap = new HashMap<>();

        for (TabellinoPartita tp : tabellini) {

            Giocatore g = tp.getGiocatore();
            EventoPartita ev = tp.getEventoPartita();

            if (g == null || ev == null) continue;

            // escludi portieri
            String categoria = g.getRuolo()
                    .getTipologia()
                    .getCategoria();
            if ("PORTIERE".equalsIgnoreCase(categoria)) continue;

            String tipo = ev.getTipoEvento();
            String esito = ev.getEsitoEvento();

            boolean golNormale =
                    "Tiro".equalsIgnoreCase(tipo) &&
                            "Goal".equalsIgnoreCase(esito);

            boolean rigoreInPartita =
                    "Rigore_regolare".equalsIgnoreCase(tipo) &&
                            "Goal".equalsIgnoreCase(esito);

            if (!golNormale && !rigoreInPartita) continue;

            long idGiocatore = g.getIdGiocatore();

            stats.putIfAbsent(idGiocatore, new int[]{0, 0});
            giocatoriMap.putIfAbsent(idGiocatore, g);

            stats.get(idGiocatore)[0]++; // gol totali
            if (rigoreInPartita) {
                stats.get(idGiocatore)[1]++; // rigori
            }
        }

        // costruzione DTO
        List<ClassificaMarcatoriDTO> lista = new ArrayList<>();

        for (Map.Entry<Long, int[]> entry : stats.entrySet()) {

            Giocatore g = giocatoriMap.get(entry.getKey());
            int gol = entry.getValue()[0];
            int rigori = entry.getValue()[1];

            if (gol == 0) continue;

            ClassificaMarcatoriDTO dto = new ClassificaMarcatoriDTO();
            dto.setNomeCompleto(g.getNome() + " " + g.getCognome());
            dto.setSquadra(g.getSquadra().getNome());
            dto.setTipologia(g.getRuolo().getTipologia().getCategoria());
            dto.setGol(gol);
            dto.setRigori(rigori);
            dto.setMediaGol((double) gol / 2); // partite sempre 2

            lista.add(dto);
        }

        // ordinamento
        lista.sort(
                Comparator.comparingInt(ClassificaMarcatoriDTO::getGol).reversed()
                        .thenComparing(dto ->
                                        dto.getNomeCompleto()
                                                .substring(dto.getNomeCompleto().lastIndexOf(" ") + 1),
                                String.CASE_INSENSITIVE_ORDER
                        )
        );

        // gestione ex-aequo
        int posizione = 1;
        Integer golPrecedenti = null;

        for (int i = 0; i < lista.size(); i++) {
            ClassificaMarcatoriDTO dto = lista.get(i);

            if (golPrecedenti != null && dto.getGol() == golPrecedenti) {
                dto.setPosizione(0); // "="
            } else {
                posizione = i + 1;
                dto.setPosizione(posizione);
                golPrecedenti = dto.getGol();
            }
        }

        return lista;
    }

    @Override
    public List<ClassificaPortieriDTO> getClassificaPortieriDettagliata(int idTorneo) {

        List<Partita> partite =
                partitaRepository.findByTorneoIdTorneo(idTorneo);

        List<TabellinoPartita> tabellini =
                tabellinoPartitaRepository.findByPartitaTorneoIdTorneo(idTorneo);

        // ==========================
        // MAPPE STATISTICHE
        // ==========================
        Map<Long, Integer> rigoriParati = new HashMap<>();
        Map<Long, Integer> rigoriLotteriaParati = new HashMap<>();
        Map<Long, Integer> partiteAiRigori = new HashMap<>();
        Map<Long, Integer> cleanSheet = new HashMap<>();
        Map<Long, Giocatore> portieriMap = new HashMap<>();


    /* ==========================
       1️⃣ PARTITE DECISE AI RIGORI
       ========================== */

        // 1️⃣ Inizializza TUTTI i portieri
        for (Partita p : partite) {
            for (Squadra s : List.of(p.getSquadraHome(), p.getSquadraAway())) {
                Giocatore portiere = s.getGiocatori().stream()
                        .filter(g -> "PORTIERE".equalsIgnoreCase(
                                g.getRuolo().getTipologia().getCategoria()))
                        .findFirst()
                        .orElse(null);

                if (portiere != null) {
                    long id = portiere.getIdGiocatore();
                    portieriMap.putIfAbsent(id, portiere);

                    rigoriParati.putIfAbsent(id, 0);
                    rigoriLotteriaParati.putIfAbsent(id, 0);
                    partiteAiRigori.putIfAbsent(id, 0);
                    cleanSheet.putIfAbsent(id, 0);
                }
            }
        }

        // 2️⃣ Incrementa solo quando partita ai rigori
        for (Partita p : partite) {
            if (!p.isRigori()) continue;

            for (Squadra s : List.of(p.getSquadraHome(), p.getSquadraAway())) {
                Giocatore portiere = s.getGiocatori().stream()
                        .filter(g -> "PORTIERE".equalsIgnoreCase(
                                g.getRuolo().getTipologia().getCategoria()))
                        .findFirst()
                        .orElse(null);

                if (portiere != null) {
                    long id = portiere.getIdGiocatore();
                    partiteAiRigori.merge(id, 1, Integer::sum);
                }
            }
        }



    /* ==========================
       2️⃣ RIGORI PARATI (TABELLINO)
       ========================== */
        for (TabellinoPartita tp : tabellini) {

            Giocatore g = tp.getGiocatore();
            EventoPartita ev = tp.getEventoPartita();

            if (g == null || ev == null) continue;

            if (!"PORTIERE".equalsIgnoreCase(
                    g.getRuolo().getTipologia().getCategoria())) continue;

            long id = g.getIdGiocatore();
            portieriMap.putIfAbsent(id, g);

            if ("Rigore_regolare".equalsIgnoreCase(ev.getTipoEvento())
                    && "Parato".equalsIgnoreCase(ev.getEsitoEvento())) {

                rigoriParati.merge(id, 1, Integer::sum);
            }

            if ("Rigore_lotteria".equalsIgnoreCase(ev.getTipoEvento())
                    && "Parato".equalsIgnoreCase(ev.getEsitoEvento())) {

                rigoriLotteriaParati.merge(id, 1, Integer::sum);
            }
        }


    /* ==========================
       3️⃣ CLEAN SHEET
       ========================== */
        for (Partita p : partite) {

            String[] score = p.getRisultatoRegular().split("-");
            int golHome = Integer.parseInt(score[0]);
            int golAway = Integer.parseInt(score[1]);

            if (golAway == 0) {
                Giocatore gk = p.getSquadraHome().getGiocatori().stream()
                        .filter(g -> "PORTIERE".equalsIgnoreCase(
                                g.getRuolo().getTipologia().getCategoria()))
                        .findFirst()
                        .orElse(null);

                if (gk != null) {
                    long id = gk.getIdGiocatore();
                    cleanSheet.merge(id, 1, Integer::sum);
                    portieriMap.putIfAbsent(id, gk);
                }
            }

            if (golHome == 0) {
                Giocatore gk = p.getSquadraAway().getGiocatori().stream()
                        .filter(g -> "PORTIERE".equalsIgnoreCase(
                                g.getRuolo().getTipologia().getCategoria()))
                        .findFirst()
                        .orElse(null);

                if (gk != null) {
                    long id = gk.getIdGiocatore();
                    cleanSheet.merge(id, 1, Integer::sum);
                    portieriMap.putIfAbsent(id, gk);
                }
            }
        }


    /* ==========================
       4️⃣ COSTRUZIONE DTO
       ========================== */
        List<ClassificaPortieriDTO> lista = new ArrayList<>();

        for (Giocatore g : portieriMap.values()) {

            long id = g.getIdGiocatore();

            ClassificaPortieriDTO dto = new ClassificaPortieriDTO();
            dto.setNomeCompleto(g.getNome() + " " + g.getCognome());
            dto.setSquadra(g.getSquadra().getNome());

            dto.setRigoriParati(rigoriParati.getOrDefault(id, 0));
            dto.setRigoriLotteriaParati(rigoriLotteriaParati.getOrDefault(id, 0));
            dto.setPartiteAiRigori(partiteAiRigori.getOrDefault(id, 0));
            dto.setCleanSheet(cleanSheet.getOrDefault(id, 0));

            lista.add(dto);
        }


    /* ==========================
       5️⃣ ORDINAMENTO + EX-AEQUO
       ========================== */
        lista.sort(
                Comparator
                        .comparingInt(ClassificaPortieriDTO::getCleanSheet).reversed()
                        .thenComparing(ClassificaPortieriDTO::getRigoriParati, Comparator.reverseOrder())
                        .thenComparing(dto ->
                                        dto.getNomeCompleto()
                                                .substring(dto.getNomeCompleto().lastIndexOf(" ") + 1),
                                String.CASE_INSENSITIVE_ORDER
                        )
        );

        int posizione = 1;
        Integer csPrev = null;
        Integer rigPrev = null;

        for (ClassificaPortieriDTO dto : lista) {

            if (csPrev != null &&
                    dto.getCleanSheet() == csPrev &&
                    dto.getRigoriParati() == rigPrev) {

                dto.setPosizione(0); // "="
            } else {
                dto.setPosizione(posizione++);
                csPrev = dto.getCleanSheet();
                rigPrev = dto.getRigoriParati();
            }
        }

        return lista;
    }


}
