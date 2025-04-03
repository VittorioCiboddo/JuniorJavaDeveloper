package com.example.quadrangolare_calcio.service;

import com.example.quadrangolare_calcio.dao.GiocatoreDao;
import com.example.quadrangolare_calcio.model.Giocatore;
import com.example.quadrangolare_calcio.model.Nazionalita;
import com.example.quadrangolare_calcio.model.Ruolo;
import com.example.quadrangolare_calcio.model.Squadra;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GiocatoreServiceImpl implements GiocatoreService{

    @Autowired
    private SquadraService squadraService;

    @Autowired
    private NazionalitaService nazionalitaService;

    @Autowired
    private GiocatoreDao giocatoreDao;

    private Giocatore giocatore;


    @Override
    public void registraGiocatore(String nome, String cognome, MultipartFile immagine, int numeroMaglia, LocalDate dataNascita, String descrizione, Ruolo ruolo, Squadra squadra, Nazionalita nazionalita) {
        Giocatore giocatore = new Giocatore();

        giocatore.setNome(nome);
        giocatore.setCognome(cognome);

        if (immagine != null && !immagine.isEmpty()) {
            try {
                String formato = immagine.getContentType();
                String immagineCodificata = "data:" + formato + ";base64," +
                        Base64.getEncoder().encodeToString(immagine.getBytes());
                squadra.setLogo(immagineCodificata);
            } catch (Exception e) {
                System.out.println("Error encoding image: " + e.getMessage());
            }
        }

        giocatore.setNumeroMaglia(numeroMaglia);
        giocatore.setDataNascita(dataNascita);
        giocatore.setDescrizione(descrizione);
        giocatore.setRuolo(ruolo);
        giocatore.setSquadra(squadra);
        giocatore.setNazionalita(nazionalita);

        giocatoreDao.save(giocatore);

    }

    @Override
    public List<Giocatore> elencoGiocatori() {
        List<Giocatore> giocatori = (List<Giocatore>) giocatoreDao.findAll(); // recupero TUTTI i giocatori salvati/registrati

        // Comparatore per la categoria del ruolo
        Comparator<Giocatore> comparatorByCategoria = (g1, g2) -> {
            // Otteniamo la categoria per ogni giocatore tramite il ruolo
            String categoria1 = g1.getRuolo().getTipologia().getCategoria(); // categoria del primo giocatore
            String categoria2 = g2.getRuolo().getTipologia().getCategoria(); // categoria del secondo giocatore

            // Ordine della categoria: portiere, difensore, centrocampista, attaccante
            Map<String, Integer> categoriaOrder = new HashMap<>();
            categoriaOrder.put("Portiere", 1);
            categoriaOrder.put("Difensore", 2);
            categoriaOrder.put("Centrocampista", 3);
            categoriaOrder.put("Attaccante", 4);

            // Compariamo le categorie usando la mappa definita sopra
            return Integer.compare(categoriaOrder.get(categoria1), categoriaOrder.get(categoria2));
        };

        // Comparatore per il cognome (all'interno di ogni categoria)
        Comparator<Giocatore> comparatorByCognome = Comparator.comparing(Giocatore::getCognome);

        // Combiniamo i due comparatori: prima per categoria, poi per cognome
        Comparator<Giocatore> combinedComparator = comparatorByCategoria.thenComparing(comparatorByCognome);

        // Ordiniamo i giocatori usando il comparatore combinato
        giocatori = giocatori.stream().sorted(combinedComparator).collect(Collectors.toList());

        return giocatori;
    }


    @Override
    public Giocatore dettaglioGiocatore(int idGiocatore) {
        Optional<Giocatore> giocatoreOptional = giocatoreDao.findById(idGiocatore);
        if(giocatoreOptional.isPresent())
            return giocatoreOptional.get();
        return null;
    }
}
