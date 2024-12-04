package com.example.quadrangolare_calcio.model;

import jakarta.persistence.*;

import java.time.LocalDate;

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

    public int getIdGiocatore() {
        return idGiocatore;
    }

    public void setIdGiocatore(int idGiocatore) {
        this.idGiocatore = idGiocatore;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getImmagine() {
        return immagine;
    }

    public void setImmagine(String immagine) {
        this.immagine = immagine;
    }

    public int getNumeroMaglia() {
        return numeroMaglia;
    }

    public void setNumeroMaglia(int numeroMaglia) {
        this.numeroMaglia = numeroMaglia;
    }

    public LocalDate getDataNascita() {
        return dataNascita;
    }

    public void setDataNascita(LocalDate dataNascita) {
        this.dataNascita = dataNascita;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public Ruolo getRuolo() {
        return ruolo;
    }

    public void setRuolo(Ruolo ruolo) {
        this.ruolo = ruolo;
    }

    public Squadra getSquadra() {
        return squadra;
    }

    public void setSquadra(Squadra squadra) {
        this.squadra = squadra;
    }

    public Nazionalita getNazionalita() {
        return nazionalita;
    }

    public void setNazionalita(Nazionalita nazionalita) {
        this.nazionalita = nazionalita;
    }
}
