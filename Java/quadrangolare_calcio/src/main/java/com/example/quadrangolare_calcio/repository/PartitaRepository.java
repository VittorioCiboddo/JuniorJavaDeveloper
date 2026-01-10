package com.example.quadrangolare_calcio.repository;

import com.example.quadrangolare_calcio.model.Partita;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PartitaRepository extends JpaRepository<Partita, Long> {

    List<Partita> findByTorneoIdTorneo(int idTorneo);

}
