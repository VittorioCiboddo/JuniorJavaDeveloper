package com.example.project_work_giornale.dao;

import com.example.project_work_giornale.model.Categoria;
import com.example.project_work_giornale.model.Notizia;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface NotiziaDao extends CrudRepository<Notizia, Integer> {

    @Query  (

            value = "SELECT n.*" +
                    "            FROM notizia n" +
                    "            JOIN categoria c ON n.fk_id_categoria = c.id_categoria" +
                   "            WHERE c.nome = ?1 " +
                  "ORDER BY n.data_pubblicazione DESC",
                nativeQuery = true
            )

    List<Notizia> findNotizieByCategoriaNome(String nomeCategoria);


    @Query  (

            value = "SELECT n.* FROM notizia n " +
                "WHERE n.data_pubblicazione = (SELECT MAX(n2.data_pubblicazione) " +
                "FROM notizia n2 JOIN categoria c ON n2.fk_id_categoria = c.id_categoria " +
                "WHERE c.nome = ?1)",
                nativeQuery = true

            )

    List<Notizia> getUltimaNotiziaPerCategoria(String nomeCategoria);



}
