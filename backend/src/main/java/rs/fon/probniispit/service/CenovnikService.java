package rs.fon.probniispit.service;

import org.springframework.stereotype.Service;
import rs.fon.probniispit.dto.CenovnikDTO;
import rs.fon.probniispit.dto.CenovnikZahtev;
import rs.fon.probniispit.exception.NevalidanZahtevException;
import rs.fon.probniispit.mapper.Mapper;
import rs.fon.probniispit.model.Cenovnik;
import rs.fon.probniispit.model.VrstaIspita;
import rs.fon.probniispit.repository.CenovnikRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class CenovnikService {

    private final CenovnikRepository cenovnikRepository;
    private final Mapper mapper;

    public CenovnikService(CenovnikRepository cenovnikRepository, Mapper mapper) {
        this.cenovnikRepository = cenovnikRepository;
        this.mapper = mapper;
    }

    public BigDecimal vazecaCena(VrstaIspita vrsta) {
        return vazecaCena(vrsta, LocalDate.now());
    }

    public BigDecimal vazecaCena(VrstaIspita vrsta, LocalDate datum) {
        return cenovnikRepository.findValidPrice(vrsta, datum)
                .map(Cenovnik::getCena)
                .orElseThrow(() -> new NevalidanZahtevException(
                        "Nije definisana cena za vrstu ispita: " + vrsta));
    }

    public List<CenovnikDTO> sveCene() {
        return cenovnikRepository.findAll().stream()
                .map(mapper::uCenovnikDTO)
                .toList();
    }

    public CenovnikDTO trenutneCene(VrstaIspita vrsta) {
        BigDecimal cena = vazecaCena(vrsta);
        return new CenovnikDTO(null, vrsta.name(), cena, LocalDate.now());
    }

    public CenovnikDTO postaviIzZahteva(CenovnikZahtev zahtev) {
        VrstaIspita vrsta = procitajVrstu(zahtev.vrstaIspita());
        return mapper.uCenovnikDTO(postaviCenu(vrsta, zahtev.cena(), zahtev.vaziOd()));
    }

    public Cenovnik postaviCenu(VrstaIspita vrsta, BigDecimal cena, LocalDate vaziOd) {
        if (cena == null || cena.compareTo(BigDecimal.ZERO) <= 0) {
            throw new NevalidanZahtevException("Cena mora biti veća od nule");
        }

        Cenovnik cenovnik = new Cenovnik();
        cenovnik.setVrstaIspita(vrsta);
        cenovnik.setCena(cena);
        cenovnik.setVaziOd(vaziOd);

        return cenovnikRepository.save(cenovnik);
    }

    private VrstaIspita procitajVrstu(String vrednost) {
        try {
            return VrstaIspita.valueOf(vrednost);
        } catch (IllegalArgumentException e) {
            throw new NevalidanZahtevException("Nepoznata vrsta ispita: " + vrednost);
        }
    }
}