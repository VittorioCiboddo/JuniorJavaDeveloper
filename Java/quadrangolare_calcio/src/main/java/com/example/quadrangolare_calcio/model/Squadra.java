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
@Table(name = "squadra")
public class Squadra {

    @Id
    @Column(name = "id_squadra")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idSquadra;

    @Column
    private String nome;

    @Column
    private String logo;

    @Column
    private String capitano;

    @Column
    private String descrizione;

    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "fk_id_modulo", referencedColumnName = "id_modulo")
    private Modulo modulo;

    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "fk_id_nazionalita", referencedColumnName = "id_nazionalita")
    private Nazionalita nazionalita;

    @OneToMany(mappedBy = "squadra", cascade = CascadeType.REMOVE)
    private List<Giocatore> giocatori; // aggiungo questa lista per mantenere la relazione

    @OneToOne(mappedBy = "squadra", cascade = CascadeType.REMOVE)
    private Stadio stadio;

    @OneToMany(mappedBy = "squadra", cascade = CascadeType.REMOVE)
    private List<Allenatore> allenatori;

}
