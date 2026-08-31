package rs.fon.probniispit.controller;

import jakarta.validation.Valid;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import rs.fon.probniispit.dto.NovaPrijavaZahtev;
import rs.fon.probniispit.dto.PrijavaDTO;
import rs.fon.probniispit.dto.StranicaDTO;
import rs.fon.probniispit.exception.NijePronadjenoException;
import rs.fon.probniispit.model.Korisnik;
import rs.fon.probniispit.model.StatusPrijave;
import rs.fon.probniispit.security.UlogovaniKorisnik;
import rs.fon.probniispit.service.KorisnikService;
import rs.fon.probniispit.service.PrijavaService;

import java.util.List;

@RestController
@RequestMapping("/api/prijave")
public class PrijavaController {

    private final PrijavaService prijavaService;
    private final KorisnikService korisnikService;

    public PrijavaController(PrijavaService prijavaService, KorisnikService korisnikService) {
        this.prijavaService = prijavaService;
        this.korisnikService = korisnikService;
    }

    @PostMapping
    public ResponseEntity<PrijavaDTO> kreiraj(@AuthenticationPrincipal UlogovaniKorisnik ulogovani,
                                              @Valid @RequestBody NovaPrijavaZahtev zahtev) {
        Korisnik korisnik = korisnikService.pronadjiPoId(ulogovani.getId())
                .orElseThrow(() -> new NijePronadjenoException("Korisnik ne postoji"));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(prijavaService.kreiraj(korisnik, zahtev.terminIds()));
    }

    @GetMapping("/moje")
    public List<PrijavaDTO> moje(@AuthenticationPrincipal UlogovaniKorisnik ulogovani) {
        return prijavaService.mojePrijave(ulogovani.getId());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> otkazi(@PathVariable Integer id,
                                       @AuthenticationPrincipal UlogovaniKorisnik ulogovani) {
        prijavaService.otkaziKaoKandidat(id, ulogovani.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public StranicaDTO<PrijavaDTO> sve(
            @RequestParam(required = false) StatusPrijave status,
            @RequestParam(required = false) String pretraga,
            @RequestParam(defaultValue = "0") int stranica,
            @RequestParam(defaultValue = "10") int velicina,
            @RequestParam(defaultValue = "datumPrijave") String sortiraj,
            @RequestParam(defaultValue = "desc") String smer) {

        Sort sort = smer.equalsIgnoreCase("asc")
                ? Sort.by(sortiraj).ascending()
                : Sort.by(sortiraj).descending();

        return prijavaService.pretrazi(status, pretraga, PageRequest.of(stranica, velicina, sort));
    }

    @DeleteMapping("/{id}/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> otkaziKaoAdmin(@PathVariable Integer id) {
        prijavaService.otkaziKaoAdmin(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{prijavaId}/termini/{terminId}")
    public PrijavaDTO otkaziTermin(@PathVariable Integer prijavaId,
                                @PathVariable Integer terminId,
                                @AuthenticationPrincipal UlogovaniKorisnik ulogovani) {
        return prijavaService.otkaziTermin(prijavaId, terminId, ulogovani.getId());
    }
}