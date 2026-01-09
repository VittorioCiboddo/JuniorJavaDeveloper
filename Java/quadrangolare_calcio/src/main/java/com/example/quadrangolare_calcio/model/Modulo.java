package com.example.quadrangolare_calcio.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "modulo")
public class Modulo {

    @Id
    @Column(name = "id_modulo")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idModulo;

    @Column(name = "schema_gioco")
    private String schemaGioco;


}
