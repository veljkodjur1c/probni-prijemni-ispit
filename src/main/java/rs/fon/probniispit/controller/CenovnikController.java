package rs.fon.probniispit.controller;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rs.fon.probniispit.dto.CenovnikDTO;
import rs.fon.probniispit.dto.CenovnikZahtev;
import rs.fon.probniispit.service.CenovnikService;

import java.util.List;

@RestController
@RequestMapping("/api/cenovnik")
public class CenovnikController {

    private final CenovnikService cenovnikService;

    public CenovnikController(CenovnikService cenovnikService) {
        this.cenovnikService = cenovnikService;
    }

    @GetMapping
    public List<CenovnikDTO> sve() {
        return cenovnikService.sveCene();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public CenovnikDTO postavi(@Valid @RequestBody CenovnikZahtev zahtev) {
        return cenovnikService.postaviIzZahteva(zahtev);
    }
}