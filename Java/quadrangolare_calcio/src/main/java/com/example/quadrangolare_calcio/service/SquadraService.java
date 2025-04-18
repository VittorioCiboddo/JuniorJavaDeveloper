package com.example.quadrangolare_calcio.service;

import com.example.quadrangolare_calcio.model.Modulo;
import com.example.quadrangolare_calcio.model.Nazionalita;
import com.example.quadrangolare_calcio.model.Ruolo;
import com.example.quadrangolare_calcio.model.Squadra;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SquadraService {

    Squadra registraSquadra(String nome, MultipartFile logo, Nazionalita nazionalita, Modulo modulo, String capitano, String descrizione);

    List<Squadra> elencoSquadre();

    Squadra dettaglioSquadra(Long idSquadra);

    Squadra getSquadraById(Long idSquadra);

    List<Ruolo> getRuoliPerSquadra(Long idSquadra);

    Modulo getModuloBySquadra(int id);

    List<Squadra> getSquadreConSpazioPerCategoria(String tipologia);

    Object getAllSquadre();

    List<Squadra> getSquadreSenzaAllenatore();

    List<Squadra> getSquadreSenzaStadio();

    void salvaSquadra(Squadra squadra);

    void eliminaSquadra(int id);
}
