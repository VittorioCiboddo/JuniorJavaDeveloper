package com.example.quadrangolare_calcio.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tipologia")
public class Tipologia {

    @Id
    @Column(name = "id_tipologia")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idTipologia;

    @Column
    private String categoria;

}
