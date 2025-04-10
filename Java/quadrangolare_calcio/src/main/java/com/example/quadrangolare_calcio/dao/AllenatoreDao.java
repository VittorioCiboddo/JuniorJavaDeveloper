package com.example.quadrangolare_calcio.dao;

import com.example.quadrangolare_calcio.model.Allenatore;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface AllenatoreDao extends CrudRepository<Allenatore, Integer> {

    @Query("SELECT a FROM Allenatore a WHERE a.squadra.idSquadra = :idSquadra")
    Allenatore findBySquadraId(@Param("idSquadra") Long idSquadra);

}
