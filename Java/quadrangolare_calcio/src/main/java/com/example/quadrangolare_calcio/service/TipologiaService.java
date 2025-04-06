package com.example.quadrangolare_calcio.service;

import com.example.quadrangolare_calcio.model.Tipologia;

import java.util.List;

public interface TipologiaService {

    List<Tipologia> elencoTipologie();
    List<Tipologia> getTipologiePerModulo(Long idModulo);
    Tipologia getById(Long id);

}
