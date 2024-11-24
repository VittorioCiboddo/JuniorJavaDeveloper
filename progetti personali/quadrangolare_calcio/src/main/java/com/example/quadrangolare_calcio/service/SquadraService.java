package com.example.quadrangolare_calcio.service;

import com.example.quadrangolare_calcio.model.Squadra;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SquadraService {

    void registraSquadra(Squadra squadra, String nome, MultipartFile logo, String modulo, String allenatore, String capitano, String descrizione);

    List<Squadra> elencoSquadre();

    Squadra dettaglioSquadra(int idSquadra);


}
