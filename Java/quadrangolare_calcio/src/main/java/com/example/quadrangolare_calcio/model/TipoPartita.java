package com.example.quadrangolare_calcio.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tipo_partita")
public class TipoPartita {

    @Id
    @Column(name = "id_tipo_partita")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idTipoPartita;

    @Column
    private String tipo;
}
