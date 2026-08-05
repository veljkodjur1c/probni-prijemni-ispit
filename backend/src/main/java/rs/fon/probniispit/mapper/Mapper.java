package rs.fon.probniispit.mapper;

import org.springframework.stereotype.Component;
import rs.fon.probniispit.dto.*;
import rs.fon.probniispit.model.*;

import java.util.List;

@Component
public class Mapper {

    public KorisnikDTO uKorisnikDTO(Korisnik k) {
        return new KorisnikDTO(
                k.getId(),
                k.getIme(),
                k.getPrezime(),
                k.getEmail(),
                k.getUloga().name()
        );
    }

    public TerminDTO uTerminDTO(Termin t) {
        return new TerminDTO(
                t.getId(),
                t.getDatum(),
                t.getVremePocetka(),
                t.getVrstaIspita().name(),
                t.getAdresa()
        );
    }

    public CenovnikDTO uCenovnikDTO(Cenovnik c) {
        return new CenovnikDTO(
                c.getId(),
                c.getVrstaIspita().name(),
                c.getCena(),
                c.getVaziOd()
        );
    }

    public StavkaPrijaveDTO uStavkuDTO(PrijavaTermin pt) {
        return new StavkaPrijaveDTO(
                uTerminDTO(pt.getTermin()),
                pt.getCenaUTrenutkuPrijave()
        );
    }

    public PrijavaDTO uPrijavuDTO(Prijava p) {
        List<StavkaPrijaveDTO> stavke = p.getStavke().stream()
                .map(this::uStavkuDTO)
                .toList();

        Korisnik k = p.getKorisnik();

        return new PrijavaDTO(
                p.getId(),
                k.getId(),
                k.getIme() + " " + k.getPrezime(),
                p.getDatumPrijave(),
                p.getUkupnaCena(),
                p.getStatus().name(),
                stavke
        );
    }

    public UplataDTO uUplatuDTO(Uplata u) {
        Korisnik admin = u.getAdmin();
        return new UplataDTO(
                u.getId(),
                u.getPrijava().getId(),
                u.getIznos(),
                u.getDatumEvidentiranja(),
                admin.getIme() + " " + admin.getPrezime()
        );
    }
}