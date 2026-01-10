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
@Table(name = "torneo")
public class Torneo {

    @Id
    @Column(name = "id_torneo")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idTorneo;

    @Column
    private String nome;

    @OneToMany(mappedBy = "torneo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Partita> partite;
}
