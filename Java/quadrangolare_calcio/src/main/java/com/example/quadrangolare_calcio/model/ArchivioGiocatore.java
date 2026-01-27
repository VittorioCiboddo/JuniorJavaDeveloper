package com.example.quadrangolare_calcio.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "archivio_giocatore")
public class ArchivioGiocatore {

    @Id
    @Column(name = "id_archivio_giocatore")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idArchivioGiocatore;

    @Column(name = "gol_totali")
    private int golTotali;

    @Column(name = "rigori_regolari_segnati")
    private int rigoriRegolariSegnati;

    @Column(name = "rigori_lotteria_segnati")
    private int rigoriLotteriaSegnati;

    @Column(name = "rigori_regolari_parati")
    private int rigoriRegolariParati;

    @Column(name = "rigori_lotteria_parati")
    private int rigoriLotteriaParati;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "fk_id_giocatore", referencedColumnName = "id_giocatore")
    private Giocatore giocatore;


}
