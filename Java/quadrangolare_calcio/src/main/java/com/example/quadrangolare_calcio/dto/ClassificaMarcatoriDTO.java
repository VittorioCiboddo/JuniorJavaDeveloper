package com.example.quadrangolare_calcio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassificaMarcatoriDTO {

    private int posizione;
    private String nomeCompleto;
    private String squadra;
    private String tipologia;

    private int gol;
    private int rigori;
    private double mediaGol;
}
