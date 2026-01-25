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

}