package com.example.quadrangolare_calcio.dto;

import lombok.Data;

import java.util.List;

@Data
public class PartitaDTO {
    private String tipoPartita;
    private int idSquadraHome;
    private int idSquadraAway;
    private String risultatoRegular;
    private String risultatoFinale;
    private boolean rigori;
    private List<EventoDTO> eventi;
}
