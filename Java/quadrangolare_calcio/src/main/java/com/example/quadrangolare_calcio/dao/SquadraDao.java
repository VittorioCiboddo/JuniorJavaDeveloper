package com.example.quadrangolare_calcio.dao;

import com.example.quadrangolare_calcio.model.Squadra;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SquadraDao extends CrudRepository<Squadra, Long> {


    @Query("""
        SELECT s
        FROM Squadra s
        WHERE EXISTS (
            SELECT r
            FROM Ruolo r
            WHERE r.tipologia.categoria = :categoria
            AND r.modulo = s.modulo
            AND r NOT IN (
                SELECT g.ruolo
                FROM Giocatore g
                WHERE g.squadra = s
            ) )""")
            List<Squadra> findSquadreConSpazioPerCategoria(@Param("categoria") String categoria);


    @Query(value = "SELECT * FROM squadra s WHERE s.id_squadra NOT IN (SELECT fk_id_squadra FROM allenatore)", nativeQuery = true)
    List<Squadra> findSquadreSenzaAllenatore();

    @Query("SELECT s FROM Squadra s WHERE s.idSquadra NOT IN (SELECT st.squadra.idSquadra FROM Stadio st)")
    List<Squadra> findSquadreSenzaStadio();





}
