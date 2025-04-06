package com.example.quadrangolare_calcio.service;

import com.example.quadrangolare_calcio.model.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface GiocatoreService {

    List<Giocatore> elencoGiocatori();

    Giocatore dettaglioGiocatore(int idGiocatore);

    boolean isRuoloGiaAssegnato(int idRuolo);

    List<Giocatore> getGiocatoriPerSquadra(Long idSquadra);


    void registraGiocatore(Giocatore giocatore);
}
