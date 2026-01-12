package com.example.quadrangolare_calcio.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "stadio")
public class Stadio {

    @Id
    @Column(name = "id_stadio")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idStadio;

    @Column
    private String nome;

    @Column(columnDefinition = "LONGTEXT")
    private String immagine;

    @Column
    private int capienza;

    @Column
    private String ultras;

    @Column(columnDefinition = "TEXT")
    private String descrizione;

    @OneToOne
    @JoinColumn(name = "fk_id_squadra", referencedColumnName = "id_squadra")
    private Squadra squadra;

}
