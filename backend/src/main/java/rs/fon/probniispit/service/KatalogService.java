package rs.fon.probniispit.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.fon.probniispit.dto.KatalogDTO;
import rs.fon.probniispit.dto.KatalogZahtev;
import rs.fon.probniispit.dto.StavkaKatalogaZahtev;
import rs.fon.probniispit.exception.KonfliktException;
import rs.fon.probniispit.exception.NevalidanZahtevException;
import rs.fon.probniispit.exception.NijePronadjenoException;
import rs.fon.probniispit.mapper.Mapper;
import rs.fon.probniispit.model.Katalog;
import rs.fon.probniispit.model.StavkaKataloga;
import rs.fon.probniispit.model.VrstaIspita;
import rs.fon.probniispit.repository.KatalogRepository;
import rs.fon.probniispit.repository.StavkaKatalogaRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class KatalogService {

    private final KatalogRepository katalogRepository;
    private final StavkaKatalogaRepository stavkaRepository;
    private final Mapper mapper;

    public KatalogService(KatalogRepository katalogRepository,
                          StavkaKatalogaRepository stavkaRepository,
                          Mapper mapper) {
        this.katalogRepository = katalogRepository;
        this.stavkaRepository = stavkaRepository;
        this.mapper = mapper;
    }

    public BigDecimal vazecaCena(VrstaIspita vrsta) {
        return vazecaCena(vrsta, LocalDate.now());
    }

    public BigDecimal vazecaCena(VrstaIspita vrsta, LocalDate datum) {
        return stavkaRepository.pronadjiVazecuCenu(vrsta, datum)
                .map(StavkaKataloga::getCena)
                .orElseThrow(() -> new NevalidanZahtevException(
                        "Nije definisana cena za vrstu ispita: " + vrsta));
    }

    @Transactional(readOnly = true)
    public List<KatalogDTO> sviKatalozi() {
        Integer vazeciId = katalogRepository.pronadjiVazeci(LocalDate.now())
                .map(Katalog::getId)
                .orElse(null);

        return katalogRepository.findAllByOrderByVaziOdDesc().stream()
                .map(k -> mapper.uKatalogDTO(k, k.getId().equals(vazeciId)))
                .toList();
    }

    @Transactional(readOnly = true)
    public KatalogDTO vazeciKatalog() {
        Katalog katalog = katalogRepository.pronadjiVazeci(LocalDate.now())
                .orElseThrow(() -> new NijePronadjenoException("Nema važećeg kataloga"));
        return mapper.uKatalogDTO(katalog, true);
    }

    @Transactional(readOnly = true)
    public KatalogDTO jedan(Integer id) {
        Katalog katalog = katalogRepository.findById(id)
                .orElseThrow(() -> new NijePronadjenoException("Katalog ne postoji"));

        boolean vaziDanas = katalogRepository.pronadjiVazeci(LocalDate.now())
                .map(k -> k.getId().equals(id))
                .orElse(false);

        return mapper.uKatalogDTO(katalog, vaziDanas);
    }

    @Transactional
    public KatalogDTO kreiraj(KatalogZahtev zahtev) {
        Set<String> vrste = new HashSet<>();
        for (StavkaKatalogaZahtev s : zahtev.stavke()) {
            if (!vrste.add(s.vrstaIspita())) {
                throw new NevalidanZahtevException(
                        "Ista vrsta ispita je navedena više puta: " + s.vrstaIspita());
            }
        }

        Katalog katalog = new Katalog();
        katalog.setNaziv(zahtev.naziv());
        katalog.setVaziOd(zahtev.vaziOd());

        for (StavkaKatalogaZahtev s : zahtev.stavke()) {
            katalog.dodajStavku(new StavkaKataloga(
                    procitajVrstu(s.vrstaIspita()), s.cena()));
        }

        Katalog sacuvan = katalogRepository.save(katalog);
        boolean vaziDanas = !sacuvan.getVaziOd().isAfter(LocalDate.now());

        return mapper.uKatalogDTO(sacuvan, vaziDanas);
    }

    @Transactional
    public void obrisi(Integer id) {
        Katalog katalog = katalogRepository.findById(id)
                .orElseThrow(() -> new NijePronadjenoException("Katalog ne postoji"));

        if (!katalog.getVaziOd().isAfter(LocalDate.now())) {
            throw new KonfliktException(
                    "Katalog koji je stupio na snagu ne može biti obrisan");
        }

        katalogRepository.delete(katalog);
    }

    private VrstaIspita procitajVrstu(String vrednost) {
        try {
            return VrstaIspita.valueOf(vrednost);
        } catch (IllegalArgumentException e) {
            throw new NevalidanZahtevException("Nepoznata vrsta ispita: " + vrednost);
        }
    }
}