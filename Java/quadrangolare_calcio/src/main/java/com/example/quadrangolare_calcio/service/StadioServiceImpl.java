package com.example.quadrangolare_calcio.service;

import com.example.quadrangolare_calcio.dao.StadioDao;
import com.example.quadrangolare_calcio.model.Giocatore;
import com.example.quadrangolare_calcio.model.Stadio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StadioServiceImpl implements StadioService {

    @Autowired
    private StadioDao stadioDao;

    @Override
    public void salvaStadio(Stadio stadio) {
        stadioDao.save(stadio);

    }

    @Override
    public Stadio getStadioBySquadraId(Long idSquadra) {
        return stadioDao.findBySquadraId(idSquadra);
    }

    @Override
    public Stadio dettaglioStadio(int idStadio) {
        Optional<Stadio> stadioOptional = stadioDao.findById((long) idStadio);
        if(stadioOptional.isPresent())
            return stadioOptional.get();
        return null;
    }

    @Override
    public List<Stadio> getAllStadi() {
        return (List<Stadio>) stadioDao.findAll();
    }

    @Override
    public void eliminaStadio(int id) {
        stadioDao.deleteById((long) id);
    }

}
