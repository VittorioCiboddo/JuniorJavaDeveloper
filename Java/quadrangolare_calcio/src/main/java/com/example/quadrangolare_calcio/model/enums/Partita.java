package com.example.quadrangolare_calcio.model.enums;

public enum Partita {

    SEMIFINALE_1("Prima semifinale"),
    SEMIFINALE_2("Seconda semifinale"),
    FINALINA("Finale 3°/4° posto"),
    FINALE("Finale");

    private final String descrizione;

    Partita(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getDescrizione() {
        return descrizione;
    }
}
