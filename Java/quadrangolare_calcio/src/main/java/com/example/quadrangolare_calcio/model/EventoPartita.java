package com.example.quadrangolare_calcio.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "evento_partita")
public class EventoPartita {

    @Id
    @Column(name = "id_evento_partita")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idEventoPartita;

    @Column(name = "tipo_evento")
    private String tipoEvento;

    @Column(name = "esito_evento")
    private String esitoEvento;

}
