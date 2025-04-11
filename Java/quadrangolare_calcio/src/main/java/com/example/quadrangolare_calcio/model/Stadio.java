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

    @Column
    private String ultras;

    @Column
    private String descrizione;

    @OneToOne
    @JoinColumn(name = "fk_id_squadra", referencedColumnName = "id_squadra")
    private Squadra squadra;

    public int getIdStadio() {
        return idStadio;
    }

    public void setIdStadio(int idStadio) {
        this.idStadio = idStadio;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getImmagine() {
        return immagine;
    }

    public void setImmagine(String immagine) {
        this.immagine = immagine;
    }

    public int getCapienza() {
        return capienza;
    }

    public void setCapienza(int capienza) {
        this.capienza = capienza;
    }

    public String getUltras() {
        return ultras;
    }

    public void setUltras(String ultras) {
        this.ultras = ultras;
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
}
