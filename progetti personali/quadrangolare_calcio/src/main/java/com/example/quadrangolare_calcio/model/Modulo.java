package com.example.quadrangolare_calcio.model;

import jakarta.persistence.*;

@Entity
@Table(name = "modulo")
public class Modulo {

    @Id
    @Column(name = "id_modulo")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idModulo;

    @Column(name = "schema_gioco")
    private String schemaGioco;

    public int getIdModulo() {
        return idModulo;
    }

    public void setIdModulo(int idModulo) {
        this.idModulo = idModulo;
    }

    public String getSchemaGioco() {
        return schemaGioco;
    }

    public void setSchemaGioco(String schemaGioco) {
        this.schemaGioco = schemaGioco;
    }
}
