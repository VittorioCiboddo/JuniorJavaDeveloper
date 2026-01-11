package com.example.quadrangolare_calcio.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "archivio_squadra")
public class ArchivioSquadra {

    @Id
    @Column(name = "id_archivio_squadra")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idArchivioSquadra;

    @Column(name = "tornei_partecipati")
    private int torneiPartecipati;

    @Column(name = "tornei_vinti")
    private int torneiVinti;

    @Column(name = "secondi_posti")
    private int secondiPosti;

    @Column(name = "terzi_posti")
    private int terziPosti;

    @Column(name = "quarti_posti")
    private int quartiPosti;

    @Column(name = "vittorie_regolari")
    private int vittorieRegolari;

    @Column(name = "vittorie_rigori")
    private int vittorieRigori;

    @Column(name = "sconfitte_regolari")
    private int sconfitteRegolari;

    @Column(name = "sconfitte_rigori")
    private int sconfitteRigori;

    @Column(name = "gol_fatti_totali")
    private int golFattiTotali;

    @Column(name = "gol_subiti_totali")
    private int golSubitiTotali;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "fk_id_squadra", referencedColumnName = "id_squadra")
    private Squadra squadra;


}
