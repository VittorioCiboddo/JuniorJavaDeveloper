package com.example.quadrangolare_calcio.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "modulo")
public class Modulo {

    @Id
    @Column(name = "id_modulo")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idModulo;

    @Column(name = "schema_gioco")
    private String schemaGioco;

//    @OneToMany
//            (
//                    mappedBy = "squadra",
//                    cascade = CascadeType.REMOVE,
//                    fetch = FetchType.EAGER,
//                    orphanRemoval = true
//            )
//    private List<Squadra> squadra = new ArrayList<>();

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

//    public List<Squadra> getSquadra() {
//        return squadra;
//    }
//
//    public void setSquadra(List<Squadra> squadra) {
//        this.squadra = squadra;
//    }
}
