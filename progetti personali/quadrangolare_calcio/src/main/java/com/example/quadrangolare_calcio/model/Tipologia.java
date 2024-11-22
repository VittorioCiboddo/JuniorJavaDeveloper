package com.example.quadrangolare_calcio.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tipologia")
public class Tipologia {

    @Id
    @Column(name = "id_tipologia")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idTipologia;

    @Column
    private String categoria;

    public int getIdTipologia() {
        return idTipologia;
    }

    public void setIdTipologia(int idTipologia) {
        this.idTipologia = idTipologia;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
}
