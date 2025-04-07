package com.example.quadrangolare_calcio.model;


import jakarta.persistence.*;

@Entity
@Table(name = "stadio")
public class Stadio {

    @Id
    @Column(name = "id_stadio")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idStadio;

    @Column
    private String nome;

    @Column
    private String immagine;

    @Column
    private int capienza;

    @OneToOne
    @JoinColumn(name = "fk_id_squadra", referencedColumnName = "id_squadra")
    private Squadra squadra;

}
