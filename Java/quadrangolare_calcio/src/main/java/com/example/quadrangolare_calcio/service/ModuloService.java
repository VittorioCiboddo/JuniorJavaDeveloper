package com.example.quadrangolare_calcio.service;

import com.example.quadrangolare_calcio.model.Modulo;
import com.example.quadrangolare_calcio.model.Squadra;

import java.util.List;

public interface ModuloService {

    List<Modulo> elencoModuli();
    Modulo getModuloPerSquadra(Squadra squadra);
    Modulo getModuloById(Long idModulo);

}
