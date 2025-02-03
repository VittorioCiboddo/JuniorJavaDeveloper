package com.example.project_work_giornale.service;

import com.example.project_work_giornale.dao.NotiziaDao;
import com.example.project_work_giornale.model.Notizia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class NotiziaServiceImpl implements NotiziaService {

    @Autowired
    private NotiziaDao notiziaDao;

    @Autowired
    private CategoriaService categoriaService;

    @Override
    public List<Notizia> elencoNotizia() {
        List<Notizia> notizie = (List<Notizia>) notiziaDao.findAll();
        Comparator<Notizia> comparator = Comparator.comparing(Notizia::getDataPubblicazione).reversed();
        notizie = notizie.stream().sorted(comparator).collect(Collectors.toList());
        return notizie;
    }

    @Override
    public List<Notizia> elencoNotiziaUltimora() {
        List<Notizia> notizie = (List<Notizia>) notiziaDao.findAll();
        Comparator<Notizia> comparator = Comparator.comparing(Notizia::getDataPubblicazione).reversed();
        notizie = notizie.stream().sorted(comparator).limit(5).collect(Collectors.toList());
        return notizie;
    }

    @Override
    public Notizia dettaglioNotizia(int idNotizia) {
        Optional<Notizia> notiziaOptional = notiziaDao.findById(idNotizia);
        return notiziaOptional.orElse(null);
    }

    @Override
    public void aggiungiNotizia(Notizia notizia, String titolo, String descrizione, String autore, LocalDate dataPubblicazione, MultipartFile immagine, String linkVideo, int idCategoria) {
        notizia.setTitolo(titolo);
        notizia.setDescrizione(descrizione);
        notizia.setAutore(autore);
        notizia.setCategoria(categoriaService.datiCategoria(idCategoria));
        notizia.setDataPubblicazione(dataPubblicazione);

        if (immagine != null && !immagine.isEmpty()) {
            try {
                String formato = immagine.getContentType();
                String immagineCodificata = "data:" + formato + ";base64," +
                        Base64.getEncoder().encodeToString(immagine.getBytes());
                notizia.setImmagine(immagineCodificata);
            } catch (Exception e) {
                System.out.println("Error encoding image: " + e.getMessage());
            }
        }
        notiziaDao.save(notizia);
    }

    @Override
    public void eliminaNotizia(int idNotizia) {
        notiziaDao.deleteById(idNotizia);
    }

    @Override
    public List<Notizia> getNotizieByCategoria(String nomeCategoria) {
        return notiziaDao.findNotizieByCategoriaNome(nomeCategoria);
    }

    public List<Notizia> getNotiziePerIndex(String nomeCategoria) {
        return notiziaDao.getUltimaNotiziaPerCategoria(nomeCategoria);
    }

    @Override
    public List<Notizia> getUltimeNotiziePerCategorie() {
        List<Notizia> ultimeNotizie = new ArrayList<>();
        ultimeNotizie.add(getNotiziePerIndex("Intelligenza Artificiale").get(0));
        ultimeNotizie.add(getNotiziePerIndex("Cyber Security").get(0));
        ultimeNotizie.add(getNotiziePerIndex("Robotica").get(0));
        ultimeNotizie.add(getNotiziePerIndex("Innovazioni").get(0));
        return ultimeNotizie;
    }

}
