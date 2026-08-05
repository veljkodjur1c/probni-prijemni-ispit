package rs.fon.probniispit.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import rs.fon.probniispit.dto.PrijavaNaSistemZahtev;
import rs.fon.probniispit.dto.RegistracijaZahtev;
import rs.fon.probniispit.dto.TokenOdgovor;
import rs.fon.probniispit.service.AutentifikacijaServis;

@RestController
@RequestMapping("/api/auth")
public class AutentifikacijaController {

    private final AutentifikacijaServis servis;

    public AutentifikacijaController(AutentifikacijaServis servis) {
        this.servis = servis;
    }

    @PostMapping("/registracija")
    public TokenOdgovor registracija(@Valid @RequestBody RegistracijaZahtev zahtev) {
        return servis.registruj(zahtev);
    }

    @PostMapping("/prijava")
    public TokenOdgovor prijava(@Valid @RequestBody PrijavaNaSistemZahtev zahtev) {
        return servis.prijaviSe(zahtev);
    }
}