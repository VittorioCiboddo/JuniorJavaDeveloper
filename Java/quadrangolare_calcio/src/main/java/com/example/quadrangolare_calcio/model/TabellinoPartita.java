package com.example.quadrangolare_calcio.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tabellino_partita")
public class TabellinoPartita {

    @Id
    @Column(name = "id_tabellino_partita")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idTabellinoPartita;

    @Column
    private int minuto;

    @ManyToOne
    @JoinColumn(name = "fk_id_partita", referencedColumnName = "id_partita")
    private Partita partita;

    @ManyToOne
    @JoinColumn(name = "fk_id_giocatore", referencedColumnName = "id_giocatore")
    private Giocatore giocatore;

    @ManyToOne
    @JoinColumn(name = "fk_id_evento", referencedColumnName = "id_evento_partita")
    private EventoPartita eventoPartita;

}
