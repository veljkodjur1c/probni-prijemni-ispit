package rs.fon.probniispit.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record StavkaKatalogaZahtev(
        @NotNull(message = "Vrsta ispita je obavezna")
        String vrstaIspita,

        @NotNull(message = "Cena je obavezna")
        @DecimalMin(value = "0.01", message = "Cena mora biti veća od nule")
        BigDecimal cena
) {}