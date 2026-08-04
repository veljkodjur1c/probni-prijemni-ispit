package rs.fon.probniispit.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.fon.probniispit.dto.UplataDTO;
import rs.fon.probniispit.exception.KonfliktException;
import rs.fon.probniispit.exception.NijePronadjenoException;
import rs.fon.probniispit.mapper.Mapper;
import rs.fon.probniispit.model.Korisnik;
import rs.fon.probniispit.model.Prijava;
import rs.fon.probniispit.model.StatusPrijave;
import rs.fon.probniispit.model.Uplata;
import rs.fon.probniispit.repository.KorisnikRepository;
import rs.fon.probniispit.repository.PrijavaRepository;
import rs.fon.probniispit.repository.UplataRepository;

import java.time.LocalDateTime;

@Service
public class UplataService {

    private final UplataRepository uplataRepository;
    private final PrijavaRepository prijavaRepository;
    private final KorisnikRepository korisnikRepository;
    private final Mapper mapper;

    public UplataService(UplataRepository uplataRepository,
                         PrijavaRepository prijavaRepository,
                         KorisnikRepository korisnikRepository,
                         Mapper mapper) {
        this.uplataRepository = uplataRepository;
        this.prijavaRepository = prijavaRepository;
        this.korisnikRepository = korisnikRepository;
        this.mapper = mapper;
    }

    @Transactional
    public UplataDTO evidentiraj(Integer prijavaId, Integer adminId) {
        Prijava prijava = prijavaRepository.findById(prijavaId)
                .orElseThrow(() -> new NijePronadjenoException("Prijava ne postoji"));

        Korisnik admin = korisnikRepository.findById(adminId)
                .orElseThrow(() -> new NijePronadjenoException("Administrator ne postoji"));

        if (prijava.getStatus() == StatusPrijave.PRIJAVLJEN) {
            throw new KonfliktException("Uplata je već evidentirana");
        }
        if (prijava.getStatus() == StatusPrijave.OTKAZANA) {
            throw new KonfliktException("Prijava je otkazana");
        }

        Uplata uplata = new Uplata();
        uplata.setPrijava(prijava);
        uplata.setIznos(prijava.getUkupnaCena());
        uplata.setDatumEvidentiranja(LocalDateTime.now());
        uplata.setAdmin(admin);

        Uplata sacuvana = uplataRepository.save(uplata);

        prijava.setStatus(StatusPrijave.PRIJAVLJEN);
        prijavaRepository.save(prijava);

        return mapper.uUplatuDTO(sacuvana);
    }

    @Transactional(readOnly = true)
    public UplataDTO zaPrijavu(Integer prijavaId) {
        return uplataRepository.findByPrijavaId(prijavaId)
                .map(mapper::uUplatuDTO)
                .orElseThrow(() -> new NijePronadjenoException("Uplata za ovu prijavu nije evidentirana"));
    }
}