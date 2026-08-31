package rs.fon.probniispit.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record KatalogZahtev(
        @NotBlank(message = "Naziv kataloga je obavezan")
        String naziv,

        @NotNull(message = "Datum od kada važi je obavezan")
        LocalDate vaziOd,

        @Valid
        @NotEmpty(message = "Katalog mora imati bar jednu stavku")
        List<StavkaKatalogaZahtev> stavke
) {}