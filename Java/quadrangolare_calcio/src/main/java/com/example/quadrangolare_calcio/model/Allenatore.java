package com.example.quadrangolare_calcio.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "allenatore")
public class Allenatore {

    @Id
    @Column(name = "id_allenatore")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idAllenatore;

    @Column
    private String nome;

    @Column
    private String cognome;

    @Column(columnDefinition = "LONGTEXT")
    private String immagine;

    @Column(columnDefinition = "TEXT")
    private String descrizione;

    @OneToOne
    @JoinColumn(name = "fk_id_squadra", referencedColumnName = "id_squadra")
    private Squadra squadra;

    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "fk_id_nazionalita", referencedColumnName = "id_nazionalita")
    private Nazionalita nazionalita;


}
