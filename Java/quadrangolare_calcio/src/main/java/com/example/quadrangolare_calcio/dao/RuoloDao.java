package com.example.quadrangolare_calcio.dao;

import com.example.quadrangolare_calcio.model.Ruolo;
import com.example.quadrangolare_calcio.model.Tipologia;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RuoloDao extends CrudRepository<Ruolo, Integer> {


    @Query("""
    SELECT r
    FROM Ruolo r
    WHERE r.tipologia = :categoria
    AND r.modulo.idModulo = :moduloId
    AND r NOT IN (
        SELECT g.ruolo FROM Giocatore g WHERE g.ruolo = r
    ) """)
    List<Ruolo> findDisponibiliByCategoriaAndModulo(@Param("categoria") String categoria,
                                                    @Param("moduloId") int moduloId);


    List<Ruolo> findByTipologia(Tipologia tipologia);
}
