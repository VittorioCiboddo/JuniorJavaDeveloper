package com.example.quadrangolare_calcio.repository;

import com.example.quadrangolare_calcio.model.TabellinoPartita;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TabellinoPartitaRepository extends JpaRepository<TabellinoPartita, Long> {

    List<TabellinoPartita> findByPartitaIdPartita(int idPartita);

    // Navigazione automatica: Tabellino -> Partita -> Torneo -> ID
    List<TabellinoPartita> findByPartitaTorneoIdTorneo(int idTorneo);

}
