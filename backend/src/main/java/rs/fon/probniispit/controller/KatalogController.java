package rs.fon.probniispit.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rs.fon.probniispit.dto.KatalogDTO;
import rs.fon.probniispit.dto.KatalogZahtev;
import rs.fon.probniispit.service.KatalogService;

import java.util.List;

@RestController
@RequestMapping("/api/katalozi")
public class KatalogController {

    private final KatalogService katalogService;

    public KatalogController(KatalogService katalogService) {
        this.katalogService = katalogService;
    }

    @GetMapping("/vazeci")
    public KatalogDTO vazeci() {
        return katalogService.vazeciKatalog();
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<KatalogDTO> svi() {
        return katalogService.sviKatalozi();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public KatalogDTO jedan(@PathVariable Integer id) {
        return katalogService.jedan(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<KatalogDTO> kreiraj(@Valid @RequestBody KatalogZahtev zahtev) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(katalogService.kreiraj(zahtev));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> obrisi(@PathVariable Integer id) {
        katalogService.obrisi(id);
        return ResponseEntity.noContent().build();
    }
}