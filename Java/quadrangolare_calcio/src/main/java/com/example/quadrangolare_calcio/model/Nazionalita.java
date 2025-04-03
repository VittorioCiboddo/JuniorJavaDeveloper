package com.example.quadrangolare_calcio.model;

import jakarta.persistence.*;

import java.util.List;

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

    public int getIdNazionalita() {
        return idNazionalita;
    }

    public void setIdNazionalita(int idNazionalita) {
        this.idNazionalita = idNazionalita;
    }

    public String getNazione() {
        return nazione;
    }

    public void setNazione(String nazione) {
        this.nazione = nazione;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public String getBandiera() {
        return bandiera;
    }

    public void setBandiera(String bandiera) {
        this.bandiera = bandiera;
    }

    public List<Squadra> getSquadre() {
        return squadre;
    }

    public void setSquadre(List<Squadra> squadre) {
        this.squadre = squadre;
    }

    public List<Giocatore> getGiocatori() {
        return giocatori;
    }

    public void setGiocatori(List<Giocatore> giocatori) {
        this.giocatori = giocatori;
    }
}
