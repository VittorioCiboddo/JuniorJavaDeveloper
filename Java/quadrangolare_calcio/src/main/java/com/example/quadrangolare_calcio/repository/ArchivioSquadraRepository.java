package com.example.quadrangolare_calcio.repository;

import com.example.quadrangolare_calcio.model.ArchivioSquadra;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArchivioSquadraRepository extends JpaRepository<ArchivioSquadra, Long> {

    Optional<ArchivioSquadra> findBySquadraIdSquadra(int idSquadra);

}
