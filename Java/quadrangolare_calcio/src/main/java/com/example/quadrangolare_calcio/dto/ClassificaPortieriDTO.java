package com.example.quadrangolare_calcio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassificaPortieriDTO {

    private int posizione;
    private String nomeCompleto;
    private String squadra;

    private int partiteAiRigori;

    private int rigoriParati;
    private int rigoriLotteriaParati;
    private int cleanSheet;

}
