package com.example.project_work_giornale.dao;

import com.example.project_work_giornale.model.Categoria;
import org.springframework.data.repository.CrudRepository;

public interface CategoriaDao extends CrudRepository<Categoria, Integer> {
}
