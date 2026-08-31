package rs.fon.probniispit.dto;

import java.time.LocalDate;
import java.util.List;

public record KatalogDTO(
        Integer id,
        String naziv,
        LocalDate vaziOd,
        boolean vaziDanas,
        List<StavkaKatalogaDTO> stavke
) {}