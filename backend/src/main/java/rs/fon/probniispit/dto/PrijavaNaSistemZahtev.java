package rs.fon.probniispit.dto;

import jakarta.validation.constraints.NotBlank;

public record PrijavaNaSistemZahtev(
        @NotBlank(message = "Email je obavezan")
        String email,

        @NotBlank(message = "Lozinka je obavezna")
        String lozinka
) {}