package com.example.quadrangolare_calcio.service;

import com.example.quadrangolare_calcio.model.Ruolo;
import com.example.quadrangolare_calcio.model.Tipologia;

import java.util.List;

public interface RuoloService {

    List<Ruolo> elencoRuoli();
    List<Ruolo> getRuoliPerModulo(Long idModulo);
    Ruolo getById(Long id);

    List<Ruolo> getRuoliDisponibiliPerCategoriaEModulo(String categoria, int moduloId);

    Object getRuoliPerCategoria(Tipologia tipologia);
}
