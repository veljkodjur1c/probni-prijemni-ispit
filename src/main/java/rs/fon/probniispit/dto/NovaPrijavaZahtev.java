package rs.fon.probniispit.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record NovaPrijavaZahtev(
        @NotEmpty(message = "Morate izabrati bar jedan termin")
        List<Integer> terminIds
) {}