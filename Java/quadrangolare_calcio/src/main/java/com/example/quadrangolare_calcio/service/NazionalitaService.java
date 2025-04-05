package com.example.quadrangolare_calcio.service;

import com.example.quadrangolare_calcio.model.Modulo;
import com.example.quadrangolare_calcio.model.Nazionalita;

import java.util.List;

public interface NazionalitaService {

    Nazionalita getByNome(String nome);
    List<Nazionalita> elencoNazioni();
}
