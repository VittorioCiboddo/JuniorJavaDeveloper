package com.example.quadrangolare_calcio.model;

import jakarta.persistence.*;

import java.util.List;

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
    private String modulo;

    @Column
    private String allenatore;

    @Column
    private String capitano;

    @Column
    private String descrizione;

    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "fk_id_nazionalita", referencedColumnName = "id_nazionalita")
    private Nazionalita nazionalita;

    @OneToMany(mappedBy = "squadra", cascade = CascadeType.REMOVE)
    private List<Giocatore> giocatori; // aggiungo questa lista per mantenere la relazione

    public int getIdSquadra() {
        return idSquadra;
    }

    public void setIdSquadra(int idSquadra) {
        this.idSquadra = idSquadra;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public String getAllenatore() {
        return allenatore;
    }

    public void setAllenatore(String allenatore) {
        this.allenatore = allenatore;
    }

    public String getCapitano() {
        return capitano;
    }

    public void setCapitano(String capitano) {
        this.capitano = capitano;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public Nazionalita getNazionalita() {
        return nazionalita;
    }

    public void setNazionalita(Nazionalita nazionalita) {
        this.nazionalita = nazionalita;
    }

    public List<Giocatore> getGiocatori() {
        return giocatori;
    }

    public void setGiocatori(List<Giocatore> giocatori) {
        this.giocatori = giocatori;
    }
}
