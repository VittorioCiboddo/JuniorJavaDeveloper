package com.example.project_work_giornale.service;

import com.example.project_work_giornale.model.Notizia;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;


public interface NotiziaService {
    List<Notizia> elencoNotiziaUltimora();

    Notizia dettaglioNotizia(int idNotizia);

    void aggiungiNotizia(Notizia notizia, String titolo, String descrizione, String autore, LocalDate dataPubblicazione, MultipartFile immagine, String linkVideo, int idCategoria);

    void eliminaNotizia(int idNotizia);

    public List<Notizia> getNotizieByCategoria(String nomeCategoria);

    List<Notizia> getUltimeNotiziePerCategorie();

    List<Notizia> elencoNotizia();
}
