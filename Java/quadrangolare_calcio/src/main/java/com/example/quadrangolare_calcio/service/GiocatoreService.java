package com.example.quadrangolare_calcio.service;

import com.example.quadrangolare_calcio.model.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface GiocatoreService {

    void registraGiocatore(String nome, String cognome, MultipartFile immagine, int numeroMaglia, LocalDate dataNascita, String descrizione, Ruolo ruolo, Squadra squadra, Nazionalita nazionalita);

    List<Giocatore> elencoGiocatori();

    Giocatore dettaglioGiocatore(int idGiocatore);
}
