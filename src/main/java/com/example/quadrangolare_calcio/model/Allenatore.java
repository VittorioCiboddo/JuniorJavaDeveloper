package com.example.quadrangolare_calcio.model;

import jakarta.persistence.*;

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

    @Column
    private String immagine;

    @Column
    private String descrizione;

    @OneToOne
    @JoinColumn(name = "fk_id_squadra", referencedColumnName = "id_squadra")
    private Squadra squadra;

    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "fk_id_nazionalita", referencedColumnName = "id_nazionalita")
    private Nazionalita nazionalita;

    public long getIdAllenatore() {
        return idAllenatore;
    }

    public void setIdAllenatore(int idAllenatore) {
        this.idAllenatore = idAllenatore;
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

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
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
