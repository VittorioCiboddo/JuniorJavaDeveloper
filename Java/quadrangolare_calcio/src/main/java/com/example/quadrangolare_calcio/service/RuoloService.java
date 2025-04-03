package com.example.quadrangolare_calcio.service;

import com.example.quadrangolare_calcio.model.Ruolo;

import java.util.List;

public interface RuoloService {

    List<Ruolo> elencoRuoli();
    List<Ruolo> getRuoliPerModulo(Long idModulo);
}
