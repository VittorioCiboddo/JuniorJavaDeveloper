package com.example.quadrangolare_calcio.repository;

import com.example.quadrangolare_calcio.model.Giocatore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GiocatoreRepository extends JpaRepository<Giocatore, Long> {
}
