package com.example.quadrangolare_calcio.dto;

import lombok.Data;

import java.util.List;

@Data
public class TorneoSalvataggioDTO {
    private String nomeTorneo;
    private List<PartitaDTO> partite;
    private int idPrimo;
    private int idSecondo;
    private int idTerzo;
    private int idQuarto;

    @Data
    public static class PartitaDTO {
        private String tipoPartita;
        private int idSquadraHome;
        private int idSquadraAway;
        private String risultatoRegular;
        private String risultatoFinale;
        private boolean rigori;
        private List<EventoDTO> eventi;
    }

    @Data
    public static class EventoDTO {
        private int idGiocatore;
        private String tipoEvento; // es. "GOL"
        private int minuto;
    }
}