package rs.fon.probniispit.service;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.fon.probniispit.dto.PrijavaDTO;
import rs.fon.probniispit.dto.StranicaDTO;
import rs.fon.probniispit.exception.KonfliktException;
import rs.fon.probniispit.exception.NevalidanZahtevException;
import rs.fon.probniispit.exception.NijePronadjenoException;
import rs.fon.probniispit.mapper.Mapper;
import rs.fon.probniispit.model.Korisnik;
import rs.fon.probniispit.model.Prijava;
import rs.fon.probniispit.model.PrijavaTermin;
import rs.fon.probniispit.model.StatusPrijave;
import rs.fon.probniispit.model.Termin;
import rs.fon.probniispit.repository.PrijavaRepository;
import rs.fon.probniispit.repository.PrijavaTerminRepository;
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
    private final KatalogService katalogService;
    private final UplatnicaServis uplatnicaServis;
    private final EmailServis emailServis;
    private final PrijavaTerminRepository prijavaTerminRepository;
    private final Mapper mapper;

    public PrijavaService(PrijavaRepository prijavaRepository,
                          TerminRepository terminRepository,
                          KatalogService katalogService,
                          UplatnicaServis uplatnicaServis,
                          EmailServis emailServis,
                          PrijavaTerminRepository prijavaTerminRepository,
                          Mapper mapper) {
        this.prijavaRepository = prijavaRepository;
        this.terminRepository = terminRepository;
        this.katalogService = katalogService;
        this.uplatnicaServis = uplatnicaServis;
        this.emailServis = emailServis;
        this.prijavaTerminRepository = prijavaTerminRepository;
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

            BigDecimal cena = katalogService.vazecaCena(termin.getVrstaIspita());
            prijava.dodajStavku(new PrijavaTermin(prijava, termin, cena));
            ukupno = ukupno.add(cena);
        }

        prijava.setUkupnaCena(ukupno);
        Prijava sacuvana = prijavaRepository.save(prijava);

        byte[] uplatnica = uplatnicaServis.napravi(sacuvana);
        emailServis.posaljiUplatnicu(sacuvana, uplatnica);

        return mapper.uPrijavuDTO(sacuvana);
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

        prijava.getStavke().forEach(s -> s.setOtkazana(true));
        prijava.setUkupnaCena(BigDecimal.ZERO);
        prijava.setStatus(StatusPrijave.OTKAZANA);
        prijavaRepository.save(prijava);
    }

    @Transactional(readOnly = true)
    public StranicaDTO<PrijavaDTO> pretrazi(StatusPrijave status,
                                            String pretraga,
                                            Pageable pageable) {
        String tekst = (pretraga == null || pretraga.isBlank()) ? null : pretraga.trim();
        return StranicaDTO.od(
                prijavaRepository.pretrazi(status, tekst, pageable),
                mapper::uPrijavuDTO);
    }

    @Transactional
    public PrijavaDTO otkaziTermin(Integer prijavaId, Integer terminId, Integer korisnikId) {
        Prijava prijava = prijavaRepository.findByIdAndKorisnikId(prijavaId, korisnikId)
                .orElseThrow(() -> new NijePronadjenoException("Prijava ne postoji"));

        if (prijava.getStatus() != StatusPrijave.NA_CEKANJU) {
            throw new KonfliktException(
                    "Termini se mogu otkazati samo dok uplata nije evidentirana");
        }

        PrijavaTermin stavka = prijava.getStavke().stream()
                .filter(s -> s.getTermin().getId().equals(terminId))
                .findFirst()
                .orElseThrow(() -> new NijePronadjenoException(
                        "Termin nije deo ove prijave"));

        if (stavka.isOtkazana()) {
            throw new KonfliktException("Termin je već otkazan");
        }

        stavka.setOtkazana(true);

        BigDecimal novaCena = prijava.getStavke().stream()
                .filter(s -> !s.isOtkazana())
                .map(PrijavaTermin::getCenaUTrenutkuPrijave)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        prijava.setUkupnaCena(novaCena);

        boolean sveOtkazano = prijava.getStavke().stream()
                .allMatch(PrijavaTermin::isOtkazana);

        if (sveOtkazano) {
            prijava.setStatus(StatusPrijave.OTKAZANA);
        }

        return mapper.uPrijavuDTO(prijavaRepository.save(prijava));
    }
}