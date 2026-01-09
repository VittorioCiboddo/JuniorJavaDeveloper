package com.example.quadrangolare_calcio.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "nazionalita")
public class Nazionalita {

    @Id
    @Column(name = "id_nazionalita")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idNazionalita;

    @Column
    private String nazione;

    @Column
    private String sigla;

    @Column
    private String bandiera;

    @OneToMany(mappedBy = "nazionalita", cascade = CascadeType.REMOVE)
    private List<Squadra> squadre;

    @OneToMany(mappedBy = "nazionalita", cascade = CascadeType.REMOVE)
    private List<Giocatore> giocatori;

    @OneToMany(mappedBy = "nazionalita", cascade = CascadeType.REMOVE)
    private List<Allenatore> allenatori;

}
