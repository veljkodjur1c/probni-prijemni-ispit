package rs.fon.probniispit.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistracijaZahtev(
        @NotBlank(message = "Ime je obavezno")
        @Size(max = 100, message = "Ime može imati najviše 100 karaktera")
        String ime,

        @NotBlank(message = "Prezime je obavezno")
        @Size(max = 100, message = "Prezime može imati najviše 100 karaktera")
        String prezime,

        @NotBlank(message = "Email je obavezan")
        @Email(message = "Email nije u ispravnom formatu")
        String email,

        @NotBlank(message = "Lozinka je obavezna")
        @Size(min = 6, message = "Lozinka mora imati bar 6 karaktera")
        String lozinka
) {}