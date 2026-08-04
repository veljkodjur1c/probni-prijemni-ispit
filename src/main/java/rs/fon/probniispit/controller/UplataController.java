package rs.fon.probniispit.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import rs.fon.probniispit.dto.UplataDTO;
import rs.fon.probniispit.security.UlogovaniKorisnik;
import rs.fon.probniispit.service.UplataService;

@RestController
@RequestMapping("/api/uplate")
@PreAuthorize("hasRole('ADMIN')")
public class UplataController {

    private final UplataService uplataService;

    public UplataController(UplataService uplataService) {
        this.uplataService = uplataService;
    }

    @PostMapping("/prijava/{prijavaId}")
    public UplataDTO evidentiraj(@PathVariable Integer prijavaId,
                                 @AuthenticationPrincipal UlogovaniKorisnik admin) {
        return uplataService.evidentiraj(prijavaId, admin.getId());
    }

    @GetMapping("/prijava/{prijavaId}")
    public UplataDTO zaPrijavu(@PathVariable Integer prijavaId) {
        return uplataService.zaPrijavu(prijavaId);
    }
}