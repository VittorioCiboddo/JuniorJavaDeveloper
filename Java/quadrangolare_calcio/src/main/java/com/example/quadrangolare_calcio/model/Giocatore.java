package com.example.quadrangolare_calcio.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "giocatore")
public class Giocatore {

    @Id
    @Column(name = "id_giocatore")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idGiocatore;

    @Column
    private String nome;

    @Column
    private String cognome;

    @Column
    private String immagine;

    @Column(name = "numero_maglia")
    private int numeroMaglia;

    @Column(name = "data_nascita")
    private LocalDate dataNascita;

    @Column
    private String descrizione;

    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "fk_id_ruolo", referencedColumnName = "id_ruolo")
    private Ruolo ruolo;

    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "fk_id_squadra", referencedColumnName = "id_squadra")
    private Squadra squadra;

    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "fk_id_nazionalita", referencedColumnName = "id_nazionalita")
    private Nazionalita nazionalita;

    @OneToOne(mappedBy = "giocatore", cascade = CascadeType.ALL, orphanRemoval = true)
    private ArchivioGiocatore archivioGiocatore;

    @OneToMany(mappedBy = "giocatore", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TabellinoPartita> tabellinoPartita;


}
