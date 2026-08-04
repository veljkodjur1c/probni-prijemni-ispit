package rs.fon.probniispit.service;

import org.springframework.stereotype.Service;
import rs.fon.probniispit.dto.*;
import rs.fon.probniispit.exception.NevalidanZahtevException;
import rs.fon.probniispit.mapper.Mapper;
import rs.fon.probniispit.model.Korisnik;
import rs.fon.probniispit.security.JwtServis;

@Service
public class AutentifikacijaServis {

    private final KorisnikService korisnikService;
    private final JwtServis jwtServis;
    private final Mapper mapper;

    public AutentifikacijaServis(KorisnikService korisnikService,
                                 JwtServis jwtServis,
                                 Mapper mapper) {
        this.korisnikService = korisnikService;
        this.jwtServis = jwtServis;
        this.mapper = mapper;
    }

    public TokenOdgovor registruj(RegistracijaZahtev zahtev) {
        Korisnik korisnik = korisnikService.registruj(
                zahtev.ime(), zahtev.prezime(), zahtev.email(), zahtev.lozinka());

        return new TokenOdgovor(
                jwtServis.napraviToken(korisnik),
                mapper.uKorisnikDTO(korisnik));
    }

    public TokenOdgovor prijaviSe(PrijavaNaSistemZahtev zahtev) {
        Korisnik korisnik = korisnikService.pronadjiPoEmail(zahtev.email())
                .orElseThrow(() -> new NevalidanZahtevException("Pogrešan email ili lozinka"));

        if (!korisnikService.validirajLozinku(zahtev.lozinka(), korisnik.getLozinkaHash())) {
            throw new NevalidanZahtevException("Pogrešan email ili lozinka");
        }

        return new TokenOdgovor(
                jwtServis.napraviToken(korisnik),
                mapper.uKorisnikDTO(korisnik));
    }
}