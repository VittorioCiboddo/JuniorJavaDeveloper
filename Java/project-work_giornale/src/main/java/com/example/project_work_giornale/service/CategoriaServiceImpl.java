package com.example.project_work_giornale.service;
import com.example.project_work_giornale.dao.CategoriaDao;
import com.example.project_work_giornale.model.Categoria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    @Autowired
    private CategoriaDao categoriaDao;

    @Override
    public void registraCategoria(Categoria categoria) {
        categoriaDao.save(categoria);
    }

    @Override
    public List<Categoria> elencoCategorie() {
        return (List<Categoria>) categoriaDao.findAll();
    }

    @Override
    public Categoria datiCategoria(int id) {
        return categoriaDao.findById(id).get();
    }

    @Override
    public void eliminaCategoria(int id) {
        categoriaDao.deleteById(id);
    }
}