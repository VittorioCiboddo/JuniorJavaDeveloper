package com.example.quadrangolare_calcio.service;

import com.example.quadrangolare_calcio.dao.NazionalitaDao;
import com.example.quadrangolare_calcio.dao.SquadraDao;
import com.example.quadrangolare_calcio.model.Modulo;
import com.example.quadrangolare_calcio.model.Nazionalita;
import com.example.quadrangolare_calcio.model.Ruolo;
import com.example.quadrangolare_calcio.model.Squadra;
import com.example.quadrangolare_calcio.repository.NazionalitaRepository;
import com.example.quadrangolare_calcio.repository.SquadraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SquadraServiceImpl implements SquadraService{

    @Autowired
    private NazionalitaRepository nazionalitaRepository;

    @Autowired
    private NazionalitaDao nazionalitaDao;

    @Autowired
    private SquadraDao squadraDao;

    @Autowired
    private SquadraRepository squadraRepository;

    @Autowired
    private ModuloService moduloService; // SquadraService dipende da ModuloService

    @Autowired
    private NazionalitaService nazionalitaService;

    @Autowired
    private RuoloService ruoloService;

    @Override
    public Squadra registraSquadra(String nome, MultipartFile logo, Nazionalita nazionalita, Modulo modulo, String capitano, String descrizione) {
        Squadra squadra = new Squadra();
        squadra.setNome(nome);

        if (logo != null && !logo.isEmpty()) {
            try {
                String formato = logo.getContentType();
                String immagineCodificata = "data:" + formato + ";base64," +
                        Base64.getEncoder().encodeToString(logo.getBytes());
                squadra.setLogo(immagineCodificata);
            } catch (Exception e) {
                System.out.println("Error encoding image: " + e.getMessage());
            }
        }

        squadra.setNazionalita(nazionalita);
        squadra.setModulo(modulo);
        squadra.setCapitano(capitano);
        squadra.setDescrizione(descrizione);
        squadraDao.save(squadra);
        return squadra;
    }

    @Override
    public List<Squadra> elencoSquadre() {
        List<Squadra> squadre = (List<Squadra>) squadraDao.findAll();
        Comparator<Squadra> comparator = Comparator.comparing(Squadra::getNome);
        return squadre.stream().sorted(comparator).collect(Collectors.toList());
    }

    @Override
    public Squadra dettaglioSquadra(Long idSquadra) {
        Optional<Squadra> squadraOptional = squadraDao.findById(idSquadra);
        return squadraOptional.orElse(null);
    }

    @Override
    public Squadra getSquadraById(Long idSquadra) {
        Optional<Squadra> squadra = squadraRepository.findById(idSquadra);
        return squadra.orElse(null);
    }

    @Override
    public List<Ruolo> getRuoliPerSquadra(Long idSquadra) {
        Squadra squadra = squadraRepository.findById(idSquadra).orElse(null);
        if (squadra != null) {
            Modulo modulo = squadra.getModulo();
            if (modulo != null) {
                return ruoloService.getRuoliPerModulo((long) modulo.getIdModulo());
            }
        }
        return Collections.emptyList();
    }

    @Override
    public Modulo getModuloBySquadra(int id) {
        return squadraDao.findById((long) id).map(Squadra::getModulo).orElse(null);
    }


    @Override
    public List<Squadra> getSquadreConSpazioPerCategoria(String tipologia) {
        return squadraDao.findSquadreConSpazioPerCategoria(tipologia); // ora tipologia è la categoria
    }

    @Override
    public List<Squadra> getAllSquadre() {
        return (List<Squadra>) squadraDao.findAll();
    }

    @Override
    public List<Squadra> getSquadreSenzaAllenatore() {
        return squadraDao.findSquadreSenzaAllenatore();
    }

    @Override
    public List<Squadra> getSquadreSenzaStadio() {
        return squadraDao.findSquadreSenzaStadio();
    }

    @Override
    public void salvaSquadra(Squadra squadra) {
        squadraDao.save(squadra);
    }

    @Override
    public void eliminaSquadra(int id) {
        squadraDao.deleteById((long) id);
    }

    @Override
    public List<Nazionalita> getAllNazionalita() {
        return (List<Nazionalita>) nazionalitaDao.findAll();
    }

    @Override
    public Nazionalita getNazionalitaById(Long id) {
        return nazionalitaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Nazionalità con ID " + id + " non trovata."));
    }



}
