package rs.fon.probniispit.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.fon.probniispit.service.IzvozServis;

@RestController
@RequestMapping("/api/izvoz")
@PreAuthorize("hasRole('ADMIN')")
public class IzvozController {

    private final IzvozServis izvozServis;

    public IzvozController(IzvozServis izvozServis) {
        this.izvozServis = izvozServis;
    }

    @GetMapping("/spisak/termin/{terminId}")
    public ResponseEntity<byte[]> spisak(@PathVariable Integer terminId) {
        byte[] fajl = izvozServis.spisakKandidata(terminId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=spisak-termin-" + terminId + ".xlsx")
                .body(fajl);
    }
}