package rs.fon.probniispit.service;

import rs.fon.probniispit.dto.TerminDTO;
import rs.fon.probniispit.dto.TerminZahtev;
import rs.fon.probniispit.exception.KonfliktException;
import rs.fon.probniispit.exception.NevalidanZahtevException;
import rs.fon.probniispit.exception.NijePronadjenoException;
import rs.fon.probniispit.mapper.Mapper;
import rs.fon.probniispit.model.Termin;
import rs.fon.probniispit.model.VrstaIspita;
import rs.fon.probniispit.repository.PrijavaTerminRepository;
import rs.fon.probniispit.repository.TerminRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TerminService {

    private final TerminRepository terminRepository;
    private final PrijavaTerminRepository prijavaTerminRepository;
    private final Mapper mapper;

    public TerminService(TerminRepository terminRepository,
                     PrijavaTerminRepository prijavaTerminRepository,
                     Mapper mapper) {
        this.terminRepository = terminRepository;
        this.prijavaTerminRepository = prijavaTerminRepository;
        this.mapper = mapper;
    }

    public List<Termin> sviTermini() {
        return terminRepository.findAll();
    }

    public List<Termin> terminiUBuducnosti() {
        return terminRepository.findByDatumGreaterThanEqualOrderByDatumAsc(LocalDate.now());
    }

    public List<Termin> terminiPoVrsti(VrstaIspita vrsta) {
        return terminRepository.findByVrstaIspita(vrsta);
    }

    public Optional<Termin> pronadjiPoId(Integer id) {
        return terminRepository.findById(id);
    }

    public Termin kreiraj(Termin termin) {
        if (termin.getDatum().isBefore(LocalDate.now())) {
            throw new NevalidanZahtevException("Termin ne može biti u prošlosti");
        }
        return terminRepository.save(termin);
    }

    public void obrisi(Integer terminId) {
        if (!terminRepository.existsById(terminId)) {
            throw new NijePronadjenoException("Termin ne postoji");
        }
        if (!prijavaTerminRepository.findByTerminId(terminId).isEmpty()) {
            throw new KonfliktException("Termin ima prijavljene kandidate i ne može biti obrisan");
        }
        terminRepository.deleteById(terminId);
    }

    public List<TerminDTO> sviTerminiDTO() {
        return terminRepository.findAll().stream().map(mapper::uTerminDTO).toList();
    }

    public List<TerminDTO> buduciTerminiDTO() {
        return terminRepository.findByDatumGreaterThanEqualOrderByDatumAsc(LocalDate.now())
                .stream().map(mapper::uTerminDTO).toList();
    }

    public TerminDTO jedanDTO(Integer id) {
        return terminRepository.findById(id).map(mapper::uTerminDTO)
                .orElseThrow(() -> new NijePronadjenoException("Termin ne postoji"));
    }

    public TerminDTO kreirajIzZahteva(TerminZahtev z) {
        Termin t = new Termin();
        t.setDatum(z.datum());
        t.setVremePocetka(z.vremePocetka());
        t.setVrstaIspita(procitajVrstu(z.vrstaIspita()));
        t.setAdresa(z.adresa());
        return mapper.uTerminDTO(kreiraj(t));
    }

    public TerminDTO izmeni(Integer id, TerminZahtev z) {
        Termin t = terminRepository.findById(id)
                .orElseThrow(() -> new NijePronadjenoException("Termin ne postoji"));
        t.setDatum(z.datum());
        t.setVremePocetka(z.vremePocetka());
        t.setVrstaIspita(procitajVrstu(z.vrstaIspita()));
        t.setAdresa(z.adresa());
        return mapper.uTerminDTO(terminRepository.save(t));
    }

    private VrstaIspita procitajVrstu(String vrednost) {
        try {
            return VrstaIspita.valueOf(vrednost);
        } catch (IllegalArgumentException e) {
            throw new NevalidanZahtevException("Nepoznata vrsta ispita: " + vrednost);
        }
    }
}