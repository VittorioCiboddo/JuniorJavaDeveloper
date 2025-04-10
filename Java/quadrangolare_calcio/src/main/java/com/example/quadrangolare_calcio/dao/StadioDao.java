package com.example.quadrangolare_calcio.dao;

import com.example.quadrangolare_calcio.model.Stadio;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface StadioDao extends CrudRepository<Stadio, Long> {

    @Query("SELECT s FROM Stadio s WHERE s.squadra.idSquadra = :idSquadra")
    Stadio findBySquadraId(@Param("idSquadra") Long idSquadra);

}
