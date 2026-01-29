package com.example.quadrangolare_calcio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassificaSquadraDTO {

    private int posizione;
    private String nome;
    private String logo;

    private int partiteGiocate;
    private int vittorie;
    private int golFatti;
    private int golSubiti;
    private int differenzaReti;

    private boolean vincitore;
    private boolean migliorAttacco;
    private boolean migliorDifesa;
}

