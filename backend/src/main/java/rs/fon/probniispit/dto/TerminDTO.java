package rs.fon.probniispit.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record TerminDTO(
        Integer id,
        LocalDate datum,
        LocalTime vremePocetka,
        String vrstaIspita,
        String adresa
) {}