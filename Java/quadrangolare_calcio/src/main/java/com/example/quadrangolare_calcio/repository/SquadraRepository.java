package com.example.quadrangolare_calcio.repository;

import com.example.quadrangolare_calcio.model.Squadra;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SquadraRepository extends JpaRepository<Squadra, Long> {

    // Metodo per trovare la squadra in base all'ID
    Squadra findByIdSquadra(Long idSquadra);

    // Metodo per trovare la squadra in base al nome
    Squadra findByNome(String nomeSquadra);
}


