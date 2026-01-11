package com.example.quadrangolare_calcio.service;

import com.example.quadrangolare_calcio.model.TipoPartita;

import java.util.List;

public interface TipoPartitaService {

    List<TipoPartita> getTuttiTipi();
    TipoPartita getPerNome(String nome); // es. "Finale"
    TipoPartita getPerId(Long id);

}
