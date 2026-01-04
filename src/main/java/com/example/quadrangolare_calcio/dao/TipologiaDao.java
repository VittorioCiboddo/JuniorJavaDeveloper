package com.example.quadrangolare_calcio.dao;

import com.example.quadrangolare_calcio.model.Tipologia;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface TipologiaDao extends CrudRepository<Tipologia, Integer> {

    @Query("SELECT t FROM Tipologia t WHERE t.categoria = :categoria")
    Tipologia findByCategoria(@Param("categoria") String categoria);



}
