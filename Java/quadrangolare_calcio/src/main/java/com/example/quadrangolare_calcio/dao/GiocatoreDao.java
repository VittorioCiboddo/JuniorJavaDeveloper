package com.example.quadrangolare_calcio.dao;

import com.example.quadrangolare_calcio.model.Giocatore;
import com.example.quadrangolare_calcio.model.Tipologia;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GiocatoreDao extends CrudRepository<Giocatore, Integer> {

    boolean existsByRuolo_IdRuolo(int idRuolo);

    @Query("SELECT g FROM Giocatore g WHERE g.ruolo.tipologia.categoria = :categoria")
    List<Giocatore> findByRuoloTipologia(@Param("categoria") String categoria);




}
