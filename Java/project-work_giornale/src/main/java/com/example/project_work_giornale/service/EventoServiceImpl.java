package com.example.project_work_giornale.service;

import com.example.project_work_giornale.dao.EventoDao;
import com.example.project_work_giornale.model.Categoria;
import com.example.project_work_giornale.model.Evento;
import com.example.project_work_giornale.model.Notizia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventoServiceImpl implements EventoService{

    @Autowired
    private EventoDao eventoDao;

    @Autowired
    private CategoriaService categoriaService;


    @Override
    public List<Evento> elencoEventi() {
        List<Evento> eventi = (List<Evento>) eventoDao.findAll();
        Comparator<Evento> comparator = Comparator.comparing(Evento::getDataInizio);
        eventi = eventi.stream().sorted(comparator).collect(Collectors.toList());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (Evento evento : eventi) {
            evento.setDataInizioString(evento.getDataInizio().format(formatter));
            evento.setDataFineString(evento.getDataFine().format(formatter));
        }
        return eventi;
    }

    @Override
    public Evento dettaglioEvento(int idEvento) {
        Optional<Evento> eventoOptional = eventoDao.findById(idEvento);
        if(eventoOptional.isPresent())
            return eventoOptional.get();
        return null;
    }

    @Override
    public void aggiungiEvento(Evento evento, String nome, String luogo, LocalDateTime dataInizio, LocalDateTime dataFine, int idCategoria, String descrizione, MultipartFile immagine) {
        evento.setNome(nome);
        evento.setLuogo(luogo);
        evento.setDataInizio(dataInizio);
        evento.setDataFine(dataFine);
        evento.setCategoria(categoriaService.datiCategoria(idCategoria));
        if (immagine != null && !immagine.isEmpty()) {
            try {
                String formato = immagine.getContentType();
                String immagineCodificata = "data:" + formato + ";base64," +
                        Base64.getEncoder().encodeToString(immagine.getBytes());
                evento.setImmagine(immagineCodificata);
            } catch (Exception e) {
                System.out.println("Error encoding image: " + e.getMessage());
            }
        }
        eventoDao.save(evento);
    }


    @Override
    public void eliminaEvento(int idEvento) {
        eventoDao.deleteById(idEvento);
    }


}
