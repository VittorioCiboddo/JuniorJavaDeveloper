package com.example.quadrangolare_calcio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MiniTeamDTO {

    private String logo;      // path immagine
    private Integer gol;      // gol nei tempi regolamentari
    private Integer rigori;   // gol ai rigori (null se non ci sono)
    private boolean winner;   // evidenzia la squadra vincente

}

