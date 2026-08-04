package rs.fon.probniispit.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import rs.fon.probniispit.dto.PrijavaDTO;
import rs.fon.probniispit.exception.KonfliktException;
import rs.fon.probniispit.exception.NevalidanZahtevException;
import rs.fon.probniispit.exception.NijePronadjenoException;
import rs.fon.probniispit.mapper.Mapper;
import rs.fon.probniispit.model.*;
import rs.fon.probniispit.repository.PrijavaRepository;
import rs.fon.probniispit.repository.TerminRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class PrijavaService {

    private final PrijavaRepository prijavaRepository;
    private final TerminRepository terminRepository;
    private final CenovnikService cenovnikService;
    private final Mapper mapper;

    public PrijavaService(PrijavaRepository prijavaRepository,
                          TerminRepository terminRepository,
                          CenovnikService cenovnikService,
                          Mapper mapper) {
        this.prijavaRepository = prijavaRepository;
        this.terminRepository = terminRepository;
        this.cenovnikService = cenovnikService;
        this.mapper = mapper;
    }

    @Transactional
    public PrijavaDTO kreiraj(Korisnik korisnik, List<Integer> terminIds) {
        if (terminIds == null || terminIds.isEmpty()) {
            throw new NevalidanZahtevException("Morate izabrati bar jedan termin");
        }

        Set<Integer> jedinstveni = new HashSet<>(terminIds);
        if (jedinstveni.size() != terminIds.size()) {
            throw new NevalidanZahtevException("Isti termin je izabran više puta");
        }

        Prijava prijava = new Prijava();
        prijava.setKorisnik(korisnik);
        prijava.setDatumPrijave(LocalDateTime.now());
        prijava.setStatus(StatusPrijave.NA_CEKANJU);

        BigDecimal ukupno = BigDecimal.ZERO;

        for (Integer terminId : terminIds) {
            Termin termin = terminRepository.findById(terminId)
                    .orElseThrow(() -> new NijePronadjenoException(
                            "Termin sa id " + terminId + " ne postoji"));

            if (termin.getDatum().isBefore(LocalDate.now())) {
                throw new NevalidanZahtevException("Termin je već prošao");
            }

            BigDecimal cena = cenovnikService.vazecaCena(termin.getVrstaIspita());
            prijava.dodajStavku(new PrijavaTermin(prijava, termin, cena));
            ukupno = ukupno.add(cena);
        }

        prijava.setUkupnaCena(ukupno);
        return mapper.uPrijavuDTO(prijavaRepository.save(prijava));
    }

    public List<Prijava> prijaveKorisnika(Integer korisnikId) {
        return prijavaRepository.findByKorisnikId(korisnikId);
    }

    public List<Prijava> svePrijave() {
        return prijavaRepository.findAll();
    }

    public List<Prijava> prijavePoStatusu(StatusPrijave status) {
        return prijavaRepository.findByStatus(status);
    }

    public Optional<Prijava> pronadjiZaKorisnika(Integer prijavaId, Integer korisnikId) {
        return prijavaRepository.findByIdAndKorisnikId(prijavaId, korisnikId);
    }

    @Transactional
    public void otkaziKaoKandidat(Integer prijavaId, Integer korisnikId) {
        Prijava prijava = pronadjiZaKorisnika(prijavaId, korisnikId)
                .orElseThrow(() -> new NijePronadjenoException("Prijava ne postoji"));
        otkazi(prijava);
    }

    @Transactional
    public void otkaziKaoAdmin(Integer prijavaId) {
        Prijava prijava = prijavaRepository.findById(prijavaId)
                .orElseThrow(() -> new NijePronadjenoException("Prijava ne postoji"));
        otkazi(prijava);
    }

    private void otkazi(Prijava prijava) {
        if (prijava.getStatus() != StatusPrijave.NA_CEKANJU) {
            throw new KonfliktException(
                    "Prijava se može otkazati samo dok uplata nije evidentirana");
        }
        prijava.setStatus(StatusPrijave.OTKAZANA);
        prijavaRepository.save(prijava);
    }

    @Transactional(readOnly = true)
    public List<PrijavaDTO> mojePrijave(Integer korisnikId) {
        return prijavaRepository.findByKorisnikId(korisnikId)
                .stream().map(mapper::uPrijavuDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<PrijavaDTO> svePrijaveDTO(StatusPrijave status) {
        List<Prijava> lista = (status == null)
                ? prijavaRepository.findAll()
                : prijavaRepository.findByStatus(status);
        return lista.stream().map(mapper::uPrijavuDTO).toList();
    }

    @Transactional(readOnly = true)
    public PrijavaDTO mojaPrijava(Integer prijavaId, Integer korisnikId) {
        return prijavaRepository.findByIdAndKorisnikId(prijavaId, korisnikId)
                .map(mapper::uPrijavuDTO)
                .orElseThrow(() -> new NijePronadjenoException("Prijava ne postoji"));
    }
}