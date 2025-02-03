package com.example.project_work_giornale.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "notizia")
public class Notizia {

    @Id
    @Column(name = "id_notizia")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idNotizia;

    @Column
    private String titolo;

    @Column
    private String descrizione;

    @Column
    private String autore;

    @Column(name = "data_pubblicazione")
    private LocalDate dataPubblicazione;

    @Column
    private String immagine;

    @Column(name = "link_video")
    private String linkVideo;

    @ManyToOne
    @JoinColumn(name = "fk_id_categoria", referencedColumnName = "id_categoria")
    private Categoria categoria;

    public int getIdNotizia() {
        return idNotizia;
    }

    public void setIdNotizia(int idNotizia) {
        this.idNotizia = idNotizia;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getAutore() {
        return autore;
    }

    public void setAutore(String autore) {
        this.autore = autore;
    }

    public LocalDate getDataPubblicazione() {
        return dataPubblicazione;
    }

    public void setDataPubblicazione(LocalDate dataPubblicazione) {
        this.dataPubblicazione = dataPubblicazione;
    }

    public String getImmagine() {
        return immagine;
    }

    public void setImmagine(String immagine) {
        this.immagine = immagine;
    }

    public String getLinkVideo() {
        return linkVideo;
    }

    public void setLinkVideo(String linkVideo) {
        this.linkVideo = linkVideo;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }
}
