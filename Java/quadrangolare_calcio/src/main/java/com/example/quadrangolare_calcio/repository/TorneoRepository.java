package com.example.quadrangolare_calcio.repository;

import com.example.quadrangolare_calcio.model.Torneo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TorneoRepository extends JpaRepository<Torneo, Long> {

    boolean existsBy();
}
