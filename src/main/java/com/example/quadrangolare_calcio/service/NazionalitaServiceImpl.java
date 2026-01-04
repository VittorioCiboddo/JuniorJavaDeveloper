package com.example.quadrangolare_calcio.service;

import com.example.quadrangolare_calcio.dao.NazionalitaDao;
import com.example.quadrangolare_calcio.model.Nazionalita;
import com.example.quadrangolare_calcio.repository.NazionalitaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NazionalitaServiceImpl implements NazionalitaService{

    @Autowired
    private NazionalitaRepository nazionalitaRepository;

    @Autowired
    NazionalitaDao nazionalitaDao;

    @Override
    public List<Nazionalita> elencoNazioni() {
        List<Nazionalita> nazionalita = (List<Nazionalita>) nazionalitaDao.findAll(); // recupero TUTTE le nazionalità salvate/registrate
        Comparator<Nazionalita> comparator = Comparator.comparing(Nazionalita::getNazione); // sto ordinando le nazionalità in base al loro nome (ORDINE ALFABETICO)
        nazionalita = nazionalita.stream().sorted(comparator).collect(Collectors.toList());

        return nazionalita;
    }

    @Override
    public Nazionalita getNazionalitaById(Long id) {
        return nazionalitaRepository.findById(id).orElse(null);
    }

    @Override
    public Nazionalita getByNome(String nome) {
        return nazionalitaDao.findByNazione(nome);
    }


}
