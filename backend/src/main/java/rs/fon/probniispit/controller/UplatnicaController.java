package rs.fon.probniispit.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.fon.probniispit.security.UlogovaniKorisnik;
import rs.fon.probniispit.service.UplatnicaServis;

@RestController
@RequestMapping("/api/uplatnice")
public class UplatnicaController {

    private final UplatnicaServis uplatnicaServis;

    public UplatnicaController(UplatnicaServis uplatnicaServis) {
        this.uplatnicaServis = uplatnicaServis;
    }

    @GetMapping("/prijava/{prijavaId}")
    public ResponseEntity<byte[]> preuzmi(@PathVariable Integer prijavaId,
                                          @AuthenticationPrincipal UlogovaniKorisnik ulogovani) {

        byte[] pdf = ulogovani.jeAdmin()
                ? uplatnicaServis.generisi(prijavaId)
                : uplatnicaServis.generisiZaKandidata(prijavaId, ulogovani.getId());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=uplatnica-" + prijavaId + ".pdf")
                .body(pdf);
    }
}