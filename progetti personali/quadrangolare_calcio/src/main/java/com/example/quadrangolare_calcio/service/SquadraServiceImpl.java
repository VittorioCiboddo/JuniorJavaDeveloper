package com.example.quadrangolare_calcio.service;

import com.example.quadrangolare_calcio.dao.SquadraDao;
import com.example.quadrangolare_calcio.model.Squadra;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SquadraServiceImpl implements SquadraService{

    @Autowired
    private SquadraDao squadraDao;

    @Override
    public void registraSquadra(Squadra squadra, String nome, MultipartFile logo, String modulo, String allenatore, String capitano, String descrizione) {
        squadra.setNome(nome); // setto il nome
        if (logo != null && !logo.isEmpty()) { // setto l'immagine del logo della squadra con la codifica in base 64
            try {
                String formato = logo.getContentType();
                String logoCodificato = "data:" + formato + ";base64," +
                        Base64.getEncoder().encodeToString(logo.getBytes());
                squadra.setLogo(logoCodificato);
            } catch (Exception e) {
                System.out.println("Error encoding logo: " + e.getMessage());
            }
        }
        squadra.setModulo(modulo); // setto il modulo di gioco
        squadra.setAllenatore(allenatore); // setto l'allenatore
        squadra.setCapitano(capitano); // setto il capitano
        squadra.setDescrizione(descrizione); // setto la descrizione (storia) della squadra
        squadraDao.save(squadra); // salvo la squadra
    }

    @Override
    public List<Squadra> elencoSquadre() {
        List<Squadra> squadre = (List<Squadra>) squadraDao.findAll(); // recupero TUTTE le squadre salvate/registrate
        Comparator<Squadra> comparator = Comparator.comparing(Squadra::getNome); // sto ordinando le squadre in base al loro nome (ORDINE ALFABETICO)
        squadre = squadre.stream().sorted(comparator).collect(Collectors.toList());

        return squadre;
    }

    @Override
    public Squadra dettaglioSquadra(int idSquadra) {
        Optional<Squadra> squadraOptional = squadraDao.findById(idSquadra);
        if(squadraOptional.isPresent())
            return squadraOptional.get();
        return null;
    }

}
