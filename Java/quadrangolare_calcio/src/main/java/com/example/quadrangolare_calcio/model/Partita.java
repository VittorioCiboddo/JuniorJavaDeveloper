package com.example.quadrangolare_calcio.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "partita")
public class Partita {

    @Id
    @Column(name = "id_partita")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idPartita;

    @Column(name = "risultato_regular")
    private String risultatoRegular;

    @Column(name = "risultato_finale")
    private String risultatoFinale;

    @Column
    private boolean rigori;

    @ManyToOne
    @JoinColumn(name = "fk_id_tipo_partita", referencedColumnName = "id_tipo_partita")
    private TipoPartita tipoPartita;

    @ManyToOne
    @JoinColumn(name = "fk_id_squadra_home", referencedColumnName = "id_squadra")
    private Squadra squadraHome;

    @ManyToOne
    @JoinColumn(name = "fk_id_squadra_away", referencedColumnName = "id_squadra")
    private Squadra squadraAway;

    @ManyToOne
    @JoinColumn(name = "fk_id_torneo", referencedColumnName = "id_torneo")
    private Torneo torneo;

    @OneToMany(mappedBy = "partita", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TabellinoPartita> tabellini;


}
