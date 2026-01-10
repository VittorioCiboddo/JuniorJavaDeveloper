package com.example.quadrangolare_calcio.repository;

import com.example.quadrangolare_calcio.model.ArchivioGiocatore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArchivioGiocatoreRepository extends JpaRepository<ArchivioGiocatore, Long> {

    Optional<ArchivioGiocatore> findByGiocatoreIdGiocatore(int idGiocatore);
}

