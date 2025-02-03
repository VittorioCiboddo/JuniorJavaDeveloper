package com.example.project_work_giornale.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "evento")
public class Evento {

    @Id
    @Column(name = "id_evento")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idEvento;

    @Column
    private String nome;

    @Column
    private String luogo;

    @Column(name = "data_inizio")
    private LocalDateTime dataInizio;
    @Transient // Questo campo non sarà persistito nel DB
    private String dataInizioString; // campo per la stringa formattata

    @Column(name = "data_fine")
    private LocalDateTime dataFine;
    @Transient // Questo campo non sarà persistito nel DB
    private String dataFineString; // campo per la stringa formattata

    @Column
    private String immagine;


    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "fk_id_categoria", referencedColumnName = "id_categoria")
    private Categoria categoria;

    @Column
    private String descrizione;


    public int getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(int idEvento) {
        this.idEvento = idEvento;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLuogo() {
        return luogo;
    }

    public void setLuogo(String luogo) {
        this.luogo = luogo;
    }

    public LocalDateTime getDataInizio() {
        return dataInizio;
    }

    public void setDataInizio(LocalDateTime dataInizio) {
        this.dataInizio = dataInizio;
    }

    public String getDataInizioString() {
        return dataInizioString;
    }

    public void setDataInizioString(String dataInizioString) {
        this.dataInizioString = dataInizioString;
    }

    public LocalDateTime getDataFine() {
        return dataFine;
    }

    public void setDataFine(LocalDateTime dataFine) {
        this.dataFine = dataFine;
    }

    public String getDataFineString() {
        return dataFineString;
    }

    public void setDataFineString(String dataFineString) {
        this.dataFineString = dataFineString;
    }

    public String getImmagine() {
        return immagine;
    }

    public void setImmagine(String immagine) {
        this.immagine = immagine;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
}
