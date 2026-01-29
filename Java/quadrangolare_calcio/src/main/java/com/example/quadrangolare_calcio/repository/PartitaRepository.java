package com.example.quadrangolare_calcio.repository;

import com.example.quadrangolare_calcio.model.Partita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PartitaRepository extends JpaRepository<Partita, Long> {

    List<Partita> findByTorneoIdTorneo(int idTorneo);

    @Query("SELECT COUNT(p) FROM Partita p WHERE p.torneo.idTorneo = :idTorneo AND (p.squadraHome.nome = :nomeSquadra OR p.squadraAway.nome = :nomeSquadra)")
    int countByTorneoIdAndSquadra(@Param("nomeSquadra") String nomeSquadra, @Param("idTorneo") int idTorneo);

}
