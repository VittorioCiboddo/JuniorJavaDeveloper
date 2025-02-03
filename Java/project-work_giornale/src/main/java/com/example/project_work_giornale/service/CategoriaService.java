package com.example.project_work_giornale.service;
import com.example.project_work_giornale.model.Categoria;
import java.util.List;

public interface CategoriaService {
    void registraCategoria(Categoria categoria);
    List<Categoria> elencoCategorie();
    Categoria datiCategoria(int id);
    void eliminaCategoria(int id);
}