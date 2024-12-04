package com.example.quadrangolare_calcio.model;

import jakarta.persistence.*;

@Entity
@Table(name = "ruolo")
public class Ruolo {

    @Id
    @Column(name = "id_ruolo")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idRuolo;

    @Column
    private String sigla;

    @Column
    private String descrizione;

    @Column(name = "heat_map")
    private String heatMap;

    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "fk_id_tipologia", referencedColumnName = "id_tipologia")
    private Tipologia tipologia;

    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "fk_id_modulo", referencedColumnName = "id_modulo")
    private Modulo modulo;

    public int getIdRuolo() {
        return idRuolo;
    }

    public void setIdRuolo(int idRuolo) {
        this.idRuolo = idRuolo;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getHeatMap() {
        return heatMap;
    }

    public void setHeatMap(String heatMap) {
        this.heatMap = heatMap;
    }

    public Tipologia getTipologia() {
        return tipologia;
    }

    public void setTipologia(Tipologia tipologia) {
        this.tipologia = tipologia;
    }

    public Modulo getModulo() {
        return modulo;
    }

    public void setModulo(Modulo modulo) {
        this.modulo = modulo;
    }
}
