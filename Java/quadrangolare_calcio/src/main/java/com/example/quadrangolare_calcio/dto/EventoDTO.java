package com.example.quadrangolare_calcio.dto;

import lombok.Data;

@Data
public class EventoDTO {
    private int idGiocatore;
    private String tipoEvento;
    private String esitoEvento;
    private int minuto;
}
