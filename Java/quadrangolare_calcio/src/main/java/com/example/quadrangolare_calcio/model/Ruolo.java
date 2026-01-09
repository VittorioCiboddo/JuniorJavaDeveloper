package com.example.quadrangolare_calcio.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
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

    @Transient
    private boolean giocatoreRegistrato;

}
