package com.example.quadrangolare_calcio.repository;

import com.example.quadrangolare_calcio.model.Tipologia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TipologiaRepository extends JpaRepository<Tipologia, Long> {

    // Metodo per trovare tutte le tipologie associate a un modulo
    List<Tipologia> findByModuloId(Long idModulo);
}
