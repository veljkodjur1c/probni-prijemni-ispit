package rs.fon.probniispit.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record TerminZahtev(
        @NotNull(message = "Datum je obavezan")
        @Future(message = "Datum mora biti u budućnosti")
        LocalDate datum,

        @NotNull(message = "Vreme početka je obavezno")
        LocalTime vremePocetka,

        @NotNull(message = "Vrsta ispita je obavezna")
        String vrstaIspita,

        @NotBlank(message = "Adresa je obavezna")
        String adresa
) {}