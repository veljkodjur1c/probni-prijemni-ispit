package rs.fon.probniispit.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rs.fon.probniispit.dto.TerminDTO;
import rs.fon.probniispit.dto.TerminZahtev;
import rs.fon.probniispit.service.TerminService;

import java.util.List;

@RestController
@RequestMapping("/api/termini")
public class TerminController {

    private final TerminService terminService;

    public TerminController(TerminService terminService) {
        this.terminService = terminService;
    }

    @GetMapping
    public List<TerminDTO> sviTermini(@RequestParam(required = false) Boolean samoBuduci) {
        return Boolean.TRUE.equals(samoBuduci)
                ? terminService.buduciTerminiDTO()
                : terminService.sviTerminiDTO();
    }

    @GetMapping("/{id}")
    public TerminDTO jedan(@PathVariable Integer id) {
        return terminService.jedanDTO(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TerminDTO> kreiraj(@Valid @RequestBody TerminZahtev zahtev) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(terminService.kreirajIzZahteva(zahtev));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public TerminDTO izmeni(@PathVariable Integer id, @Valid @RequestBody TerminZahtev zahtev) {
        return terminService.izmeni(id, zahtev);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> obrisi(@PathVariable Integer id) {
        terminService.obrisi(id);
        return ResponseEntity.noContent().build();
    }
}