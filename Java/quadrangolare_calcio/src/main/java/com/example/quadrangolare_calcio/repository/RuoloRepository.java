package com.example.quadrangolare_calcio.repository;

import com.example.quadrangolare_calcio.model.Ruolo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RuoloRepository extends JpaRepository<Ruolo, Long> {

    List<Ruolo> findByModulo_IdModulo(Long idModulo);
}

