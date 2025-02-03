package com.example.project_work_giornale.dao;

import com.example.project_work_giornale.model.Evento;
import org.springframework.data.repository.CrudRepository;

public interface EventoDao extends CrudRepository<Evento, Integer> {
}
